package net.sn0wix_.modObserver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ModObserver implements ClientModInitializer {
    public static final String MOD_ID = "mod_observer";

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.configurationC2S().register(ModsForApprovalPacket.PAYLOAD_ID, ModsForApprovalPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ModsForApprovalPacket.PAYLOAD_ID, ModsForApprovalPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(ModsForApprovalPacket.PAYLOAD_ID, ModsForApprovalPacket.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ModsForApprovalPacket.PAYLOAD_ID, (payload, context) -> ClientPlayNetworking.send(new ModsForApprovalPacket()));

        ClientConfigurationConnectionEvents.START.register((handler, client) -> ClientConfigurationNetworking.send(new ModsForApprovalPacket()));
    }

    public static Set<String> getMods() throws TamperingException {
        HashMap<String, EntrypointBuilder> containers = new HashMap<>(FabricLoader.getInstance().getAllMods().size());

        FabricLoaderImpl.INSTANCE.getModsInternal().forEach(modContainer -> {
            EntrypointBuilder builder = new EntrypointBuilder();
            builder.addIconPath(modContainer.getMetadata().getIconPath(128));

            modContainer.getMetadata().getMixinConfigs(EnvType.CLIENT).forEach(builder::addMixin);
            modContainer.getMetadata().getMixinConfigs(EnvType.SERVER).forEach(builder::addMixin);
            containers.put(modContainer.getMetadata().getId(), builder);
        });

        Class<?>[] classes = new Class<?>[]{DedicatedServerModInitializer.class, ClientModInitializer.class, ModInitializer.class};
        String[] strings = new String[]{"server", "client", "common"};

        for (int i = 0; i < strings.length; i++) {
            FabricLoader.getInstance().getEntrypointContainers(strings[i], classes[i]).forEach(modContainer -> {
                EntrypointBuilder builder = containers.get(modContainer.getProvider().getMetadata().getId()) == null ? new EntrypointBuilder() : containers.get(modContainer.getProvider().getMetadata().getId());
                containers.put(modContainer.getProvider().getMetadata().getId(), builder.addId(modContainer.getEntrypoint().getClass()));
            });
        }

        for (Map.Entry<String, EntrypointBuilder> entry : containers.entrySet()) {
            EntrypointBuilder builder = entry.getValue();
            String modid = entry.getKey();

            if (!builder.getValidId(modid).equals(modid)) {
                throw new TamperingException(modid, builder.getValidId(modid));
            }

            if (!builder.icon.isEmpty() && !builder.mixins.isEmpty()) {
                if (!(builder.icon.contains(modid) || builder.hasMixinsWithId(modid))) {
                    throw new TamperingException("unknown", modid);
                }
            }
        }


        return containers.keySet();
    }


    private static class ModsForApprovalPacket implements CustomPayload {
        private static final Id<ModsForApprovalPacket> PAYLOAD_ID = new Id<>(Identifier.of(ModObserver.MOD_ID, "mods_for_approval"));
        private static final PacketCodec<PacketByteBuf, ModsForApprovalPacket> CODEC = PacketCodec.of(ModsForApprovalPacket::write, ModsForApprovalPacket::decode);

        private static ModsForApprovalPacket decode(PacketByteBuf byteBuf) {
            byteBuf.readerIndex(byteBuf.writerIndex());
            return new ModsForApprovalPacket();
        }

        private void write(PacketByteBuf byteBuf) {
            try {
                StringBuilder stringBuilder = new StringBuilder();

                for (String str : ModObserver.getMods()) {
                    stringBuilder.append(str).append(",");
                }

                try {
                    byte[] messageContent = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(MinecraftClient.getInstance().getGameProfile().getId().toString().getBytes(StandardCharsets.UTF_8), 0, 16, "AES"));

                    byteBuf.writeBytes(MessageDigest.getInstance("SHA-256").digest(messageContent));
                    byteBuf.writeBytes(cipher.doFinal(messageContent));
                } catch (NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                         BadPaddingException |
                         NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            } catch (TamperingException e) {
                MinecraftClient.getInstance().execute(() -> {
                    MinecraftClient.getInstance().setScreen(e.getScreen());
                    MinecraftClient.getInstance().disconnect();
                });
            }
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return PAYLOAD_ID;
        }
    }

    private static class EntrypointBuilder {
        private String icon = "";
        private final Set<String> mixins = new LinkedHashSet<>(1);
        private final Set<String> modids = new LinkedHashSet<>(1);


        private EntrypointBuilder addMixin(String mixin) {
            mixins.add(mixin);
            return this;
        }

        private EntrypointBuilder addId(Class<?> main) {
            if (!getId(main).isEmpty()) {
                modids.add(getId(main));
            }

            return this;
        }

        private boolean hasMixinsWithId(String modid) {
            AtomicInteger ids = new AtomicInteger(0);

            mixins.forEach(mixin -> {
                if (mixin.contains(modid)) {
                    ids.getAndIncrement();
                }
            });

            return ids.get() > 0;
        }

        private String getValidId(String originalId) {
            if (modids.isEmpty()) {
                return originalId;
            }

            if (!modids.contains(originalId)) {
                return (String) modids.toArray()[0];
            }

            return originalId;
        }

        private EntrypointBuilder addIconPath(Optional<String> optional) {
            this.icon = optional.orElse("");
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
        private final String detectedOn;
        private final String changedId;

        private TamperingException(String changedId, String detectedOn) {
            this.detectedOn = detectedOn;
            this.changedId = changedId;
        }

        public TamperingErrorScreen getScreen() {
            return new TamperingErrorScreen(changedId, detectedOn);
        }
    }

    public static class TamperingErrorScreen extends Screen {
        private final String detectedOn;
        private final String changedId;

        public TamperingErrorScreen(String changedId, String detectedOn) {
            super(Text.translatable("text." + MOD_ID + ".tampering_detected"));
            this.detectedOn = detectedOn;
            this.changedId = changedId;
        }


        @Override
        public void init() {
            int height = (this.height / 2) + (this.height / 4);
            assert client != null;

            this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.quit"), button -> client.scheduleStop()).dimensions((this.width / 2) - 100, height, 200, 20).build());
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("text." + MOD_ID + ".issue_tracker"), button -> ConfirmLinkScreen.open(this, "https://curseforge.com/minecraft/mc-mods/mod-observer/issues", true)).dimensions((this.width / 2) - 150, height - 25, 150, 20).build());
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("text." + MOD_ID + ".discord"), button -> ConfirmLinkScreen.open(this, "https://discord.gg/nNYHDryaj3", true)).dimensions((this.width / 2) + 10, height - 25, 150, 20).build());
            height = height + 30;
            this.addDrawableChild(new TextWidget(this.width, height, Text.translatable("text." + MOD_ID + ".tampering.detected", detectedOn), client.textRenderer));
            height = height + 30;
            this.addDrawableChild(new TextWidget(this.width, height, Text.translatable("text." + MOD_ID + ".tampering.original_id", changedId), client.textRenderer));
            height = height + 30;
            this.addDrawableChild(new TextWidget(this.width, height, Text.translatable("text." + MOD_ID + ".tampering.false_positive"), client.textRenderer));
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
