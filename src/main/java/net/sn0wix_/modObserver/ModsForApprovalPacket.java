package net.sn0wix_.modObserver;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class ModsForApprovalPacket implements CustomPayload {
    static final Id<ModsForApprovalPacket> PAYLOAD_ID = new Id<>(Identifier.of(ModObserver.MOD_ID, "mods_for_approval"));
    static final PacketCodec<PacketByteBuf, ModsForApprovalPacket> CODEC = PacketCodec.of(ModsForApprovalPacket::write, ModsForApprovalPacket::decode);

    private void write(PacketByteBuf byteBuf) {
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

        } catch (TamperingErrorScreen.TamperingException e) {
            MinecraftClient.getInstance().disconnect(e.getScreen(), false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ModsForApprovalPacket decode(PacketByteBuf byteBuf) {
        byteBuf.readerIndex(byteBuf.writerIndex());
        return new ModsForApprovalPacket();
    }


    @Override
    public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }
}
