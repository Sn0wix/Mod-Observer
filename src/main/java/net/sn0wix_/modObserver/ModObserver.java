package net.sn0wix_.modObserver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class ModObserver implements ClientModInitializer {
    private static final String MOD_ID = "mod_observer";

    @Override
    public void onInitializeClient() {
        /*try {
            getMods();
        } catch (TamperingException e) {
            e.showGui(MinecraftClient.getInstance());
        }*/

        PayloadTypeRegistry.configurationC2S().register(ModsForApprovalPacket.PAYLOAD_ID, ModsForApprovalPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ModsForApprovalPacket.PAYLOAD_ID, ModsForApprovalPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(ModsForApprovalPacket.PAYLOAD_ID, ModsForApprovalPacket.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ModsForApprovalPacket.PAYLOAD_ID, (payload, context) -> ClientPlayNetworking.send(new ModsForApprovalPacket()));

        ClientConfigurationConnectionEvents.START.register((handler, client) -> ClientConfigurationNetworking.send(new ModsForApprovalPacket()));
    }

    private static List<String> getMods() {
        ArrayList<String> mods = new ArrayList<>(FabricLoader.getInstance().getAllMods().size());
        for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            //TODO check the main classes for MOD_ID and MODID, check the resources location, the main class packages
            mods.add(modContainer.getMetadata().getId());
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

            for (String str : ModObserver.getMods()) {
                stringBuilder.append(str).append(",");
            }
            try {
                byte[] messageContent = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(MinecraftClient.getInstance().getGameProfile().getId().toString().getBytes(StandardCharsets.UTF_8), 0, 16, "AES"));

                byteBuf.writeBytes(MessageDigest.getInstance("SHA-256").digest(messageContent));
                byteBuf.writeBytes(cipher.doFinal(messageContent));

                System.out.println(new String(messageContent));
                System.out.println(byteBuf);
            } catch (NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return PAYLOAD_ID;
        }
    }

    private static class TamperingException extends Exception {
        private final String detectedOn;

        private TamperingException(String detectedOn) {
            this.detectedOn = detectedOn;
        }

        private String getDetectedOn() {
            return detectedOn;
        }

        private void showGui(MinecraftClient client) {

        }
    }

    //private static class TamperingErrorScreen extends Screen
}
