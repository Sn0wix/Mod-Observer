package net.sn0wix_.modObserver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.ModContainerImpl;
import net.fabricmc.loader.impl.entrypoint.EntrypointStorage;
import net.fabricmc.loader.impl.launch.knot.KnotClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ModObserver implements ClientModInitializer {
    public static final String MOD_ID = "mod_observer";

    @Override
    public void onInitializeClient() {
        try {
            getMods();
        } catch (TamperingException e) {
            e.showGui(MinecraftClient.getInstance());
            return;
        }

        PayloadTypeRegistry.configurationC2S().register(ModsForApprovalPacket.PAYLOAD_ID, ModsForApprovalPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ModsForApprovalPacket.PAYLOAD_ID, ModsForApprovalPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(ModsForApprovalPacket.PAYLOAD_ID, ModsForApprovalPacket.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ModsForApprovalPacket.PAYLOAD_ID, (payload, context) -> ClientPlayNetworking.send(new ModsForApprovalPacket()));

        ClientConfigurationConnectionEvents.START.register((handler, client) -> ClientConfigurationNetworking.send(new ModsForApprovalPacket()));
    }

    private static List<String> getMods() throws TamperingException {
        ArrayList<String> mods = new ArrayList<>(FabricLoader.getInstance().getAllMods().size());

        HashMap<String, EntrypointBuilder> containers = new HashMap<>();

        FabricLoaderImpl.INSTANCE.getModsInternal().forEach(modContainer -> {
            EntrypointBuilder builder = new EntrypointBuilder();

            modContainer.getMetadata().getMixinConfigs(EnvType.CLIENT).forEach(builder::addClientMixin);
            modContainer.getMetadata().getMixinConfigs(EnvType.SERVER).forEach(builder::addServerMixin);
            containers.put(modContainer.getMetadata().getId(), builder);
        });

        for (String string : new String[]{"server", "client", "common"}) {
            FabricLoader.getInstance().getEntrypointContainers(string, DedicatedServerModInitializer.class).forEach(modContainer -> {
                EntrypointBuilder builder = containers.get(modContainer.getProvider().getMetadata().getId()) == null ? new EntrypointBuilder().addProvider(modContainer.getProvider()) : containers.get(modContainer.getProvider().getMetadata().getId());
                containers.put(modContainer.getProvider().getMetadata().getId(), builder.addId(modContainer.getEntrypoint().getClass()));
            });
        }


        return mods;
    }


    private static class ModsForApprovalPacket implements CustomPayload {
        private static final Id<ModsForApprovalPacket> PAYLOAD_ID = new Id<>(Identifier.of(ModObserver.MOD_ID, "mods_for_approval"));
        private static final PacketCodec<PacketByteBuf, ModsForApprovalPacket> CODEC = PacketCodec.of(ModsForApprovalPacket::write, ModsForApprovalPacket::decode);

        private static ModsForApprovalPacket decode(PacketByteBuf byteBuf) {
            byteBuf.readerIndex(byteBuf.writerIndex());
            return new ModsForApprovalPacket();
        }

        private void write(PacketByteBuf byteBuf) {
            StringBuilder stringBuilder = new StringBuilder();

            try {
                for (String str : ModObserver.getMods()) {
                    stringBuilder.append(str).append(",");
                }
            } catch (TamperingException e) {
                MinecraftClient.getInstance().disconnect();
            }

            try {
                byte[] messageContent = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(MinecraftClient.getInstance().getGameProfile().getId().toString().getBytes(StandardCharsets.UTF_8), 0, 16, "AES"));

                byteBuf.writeBytes(MessageDigest.getInstance("SHA-256").digest(messageContent));
                byteBuf.writeBytes(cipher.doFinal(messageContent));
            } catch (NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException |
                     NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return PAYLOAD_ID;
        }
    }

    private static class EntrypointBuilder {
        private ModContainer provider;
        private ArrayList<String> clientMixins = new ArrayList<>(1);
        private ArrayList<String> serverMixins = new ArrayList<>(1);
        private ArrayList<String> modids = new ArrayList<>(1);


        private EntrypointBuilder addClientMixin(String mixin) {
            clientMixins.add(mixin);
            return this;
        }

        private EntrypointBuilder addServerMixin(String mixin) {
            serverMixins.add(mixin);
            return this;
        }

        private EntrypointBuilder addId(Class main) {
            modids.add(getId(main));
            return this;
        }

        private EntrypointBuilder addProvider(ModContainer provider) {
            this.provider = provider;
            return this;
        }


        private String getId(Class<?> reference) {
            for (int i = 0; i < reference.getDeclaredFields().length; i++) {
                Field field = reference.getDeclaredFields()[i];
                try {
                    if (field.getName().equalsIgnoreCase("modid") || field.getName().equalsIgnoreCase("mod_id")) {
                        return (String) field.get("");
                    }
                } catch (IllegalAccessException | ClassCastException ignored) {
                }
            }

            return "";
        }
    }

    public static class TamperingException extends Exception {
        public final String detectedOn;

        public TamperingException(String detectedOn) {
            this.detectedOn = detectedOn;
        }

        public String getDetectedOn() {
            return detectedOn;
        }

        public void showGui(MinecraftClient client) {

        }
    }

    public static class TamperingErrorScreen extends Screen {
        public TamperingErrorScreen(Text title) {
            super(title);
        }

        @Override
        public void close() {

        }

        @Override
        public boolean shouldCloseOnEsc() {
            return false;
        }
    }
}
