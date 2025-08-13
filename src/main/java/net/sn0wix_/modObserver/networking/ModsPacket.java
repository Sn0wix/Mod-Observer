package net.sn0wix_.modObserver.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.sn0wix_.modObserver.ModObserver;
import net.sn0wix_.modObserver.detection.Utils;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Deflater;

public class ModsPacket implements CustomPayload {
    public static final Id<ModsPacket> PAYLOAD_ID = new Id<>(Identifier.of(ModObserver.MOD_ID, "mods"));
    public static final PacketCodec<PacketByteBuf, ModsPacket> CODEC = PacketCodec.of(ModsPacket::write, ModsPacket::read);

    private static ModsPacket read(PacketByteBuf byteBuf) {
        byteBuf.writerIndex(byteBuf.readerIndex());
        return new ModsPacket();
    }

    private void write(PacketByteBuf byteBuf) {
        try {
            byte[] jsonBytes = Utils.getModsJson().getBytes();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(jsonBytes);

            Deflater deflater = new Deflater();
            deflater.setInput(jsonBytes);
            deflater.finish();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(jsonBytes.length);
            byte[] buffer = new byte[1024];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                outputStream.write(buffer, 0, count);
            }

            deflater.end();
            byteBuf.writeBytes(outputStream.toByteArray());
            byteBuf.writeBytes(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }
}
