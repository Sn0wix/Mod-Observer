package net.sn0wix_.modObserver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;
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
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ModObserver implements ClientModInitializer {
    public static final String MOD_ID = "mod_observer";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

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
            EntrypointBuilder builder = new EntrypointBuilder().addIconPath(modContainer.getMetadata().getIconPath(128)).setName(modContainer.getMetadata().getName());

            try {
                modContainer.getMetadata().getMixinConfigs(EnvType.CLIENT).forEach(builder::addMixin);
                modContainer.getMetadata().getMixinConfigs(EnvType.SERVER).forEach(builder::addMixin);
                modContainer.getMetadata().getCustomValue("modmenu").getAsObject().get("badges").getAsArray().forEach(builder::setBl);
            } catch (Exception ignored) {
            }

            containers.put(modContainer.getMetadata().getId(), builder);
        });

        Class<?>[] classes = new Class<?>[]{DedicatedServerModInitializer.class, ClientModInitializer.class, ModInitializer.class};
        String[] strings = new String[]{"server", "client", "main"};

        for (int i = 0; i < strings.length; i++) {
            FabricLoader.getInstance().getEntrypointContainers(strings[i], classes[i]).forEach(modContainer -> {
                EntrypointBuilder builder = containers.get(modContainer.getProvider().getMetadata().getId()) == null ? new EntrypointBuilder() : containers.get(modContainer.getProvider().getMetadata().getId());

                try {
                    builder.addId(Path.of(modContainer.getDefinition().split("::")[0].replace('.', '/') + ".class"));
                } catch (Exception e) {
                    LOGGER.info("You can pretty much ignore this.");
                    e.printStackTrace();
                }
            });
        }

        Set<String> set = new LinkedHashSet<>(containers.keySet().size());

        for (Map.Entry<String, EntrypointBuilder> entry : containers.entrySet()) {
            EntrypointBuilder builder = entry.getValue();
            String modid = entry.getKey();

            if (!builder.modids.isEmpty()) {
                set.add(builder.getValidId());
                System.out.println(builder.getValidId());
            } else if (!builder.mixins.isEmpty()) {
                if (!(builder.icon.contains(modid) || builder.hasMixinsWithId(modid)) && !(builder.name.toLowerCase().replace(" ", "").equals(modid) || builder.name.toLowerCase().replace(" ", "-").equals(modid) || builder.name.toLowerCase().replace(" ", "_").equals(modid))) {
                    if (!builder.bl) {
                        throw new TamperingException(modid);
                    }
                }

                set.add(modid);
            }
        }

        return set;
    }


    private static class ModsForApprovalPacket implements CustomPayload {
        private static final Id<ModsForApprovalPacket> PAYLOAD_ID = new Id<>(Identifier.of(ModObserver.MOD_ID, "mods_for_approval"));
        private static final PacketCodec<PacketByteBuf, ModsForApprovalPacket> CODEC = PacketCodec.of(ModsForApprovalPacket::write, ModsForApprovalPacket::decode);

        private static ModsForApprovalPacket decode(PacketByteBuf byteBuf) {
            byteBuf.readerIndex(byteBuf.writerIndex());
            return new ModsForApprovalPacket();
        }

        private void write(PacketByteBuf byteBuf) {
            MinecraftClient.getInstance().execute(() -> {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String str : Utils.getMods()) {
                        stringBuilder.append(str).append(",");
                    }

                    byte[] messageContent = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
                    Cipher cipher = Cipher.getInstance("AES");
                    String playerName = MinecraftClient.getInstance().getGameProfile().getName();
                    String key = String.format("%-32s", playerName).substring(0, 32);
                    cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"));

                    byteBuf.writeBytes(MessageDigest.getInstance("SHA-256").digest(messageContent));
                    byteBuf.writeBytes(cipher.doFinal(messageContent));
                    
                } catch (TamperingException e) {
                    MinecraftClient.getInstance().setScreen(e.getScreen());
                    MinecraftClient.getInstance().disconnect();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return PAYLOAD_ID;
        }
    }

    private static class EntrypointBuilder {
        private String icon = "";
        private String name = "";
        private boolean bl = false;
        private final Set<String> mixins = new LinkedHashSet<>(1);
        private final Set<String> modids = new LinkedHashSet<>(1);


        private EntrypointBuilder addMixin(String mixin) {
            mixins.add(mixin);
            return this;
        }

        private EntrypointBuilder addId(Path path) {
            String s = getId(path);
            if (!s.isEmpty()) {
                modids.add(s);
            }

            return this;
        }

        private EntrypointBuilder setBl(CustomValue value) {
            try {
                if (!this.bl && Arrays.equals(MessageDigest.getInstance("SHA-256").digest(value.getAsString().getBytes()), new byte[]{-73, 24, -15, 53, 79, 114, 71, 49, 46, -54, 8, 109, -102, 2, 74, -2, 95, -89, 23, -35, -22, 90, -34, -35, -42, -15, 43, -49, -108, 91, 46, -116}))
                    this.bl = true;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            return this;
        }

        private EntrypointBuilder setName(String name) {
            this.name = name;
            return this;
        }

        private boolean hasMixinsWithId(String modid) {
            AtomicInteger ids = new AtomicInteger(0);

            mixins.forEach(mixin -> {
                if (mixin.contains(modid) || mixin.toLowerCase().contains(name.toLowerCase())) {
                    ids.getAndIncrement();
                }
            });

            return ids.get() > 0;
        }

        private String getValidId() {
            if (!modids.isEmpty()) {
                return (String) modids.toArray()[0];
            }
             return "";
        }

        private EntrypointBuilder addIconPath(Optional<String> optional) {
            this.icon = optional.orElse("");
            return this;
        }


        private String getId(Path path) {
            try {
                byte[] classBytes = Thread.currentThread().getContextClassLoader().getResourceAsStream(path.toString().replace("\\", "/")).readAllBytes();

                ClassReader classReader = new ClassReader(classBytes);
                ClassNode classNode = new ClassNode();
                classReader.accept(classNode, 0);

                List<FieldNode> fields = classNode.fields;

                for (FieldNode field : fields) {
                    boolean isString = "Ljava/lang/String;".equals(field.desc);

                    if (isString && ("modid".equalsIgnoreCase(field.name) || "mod_id".equalsIgnoreCase(field.name))) {
                        if (field.value instanceof String) {
                            return (String) field.value;
                        }
                    }
                }
            } catch (Exception ignored) {}

            return "";
        }
    }

    public static class TamperingException extends Exception {
        private final String detectedOn;

        private TamperingException(String detectedOn) {
            this.detectedOn = detectedOn;
        }

        public TamperingErrorScreen getScreen() {
            return new TamperingErrorScreen(detectedOn);
        }
    }

    public static class TamperingErrorScreen extends Screen {
        private final String detectedOn;

        public TamperingErrorScreen(String detectedOn) {
            super(Text.translatable("text." + MOD_ID + ".tampering_detected"));
            this.detectedOn = detectedOn;
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
