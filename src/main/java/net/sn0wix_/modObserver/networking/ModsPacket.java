package net.sn0wix_.modObserver.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.sn0wix_.modObserver.ModObserver;

public class ModsPacket implements CustomPayload {
    public static final Id<ModsPacket> PAYLOAD_ID = new Id<>(Identifier.of(ModObserver.MOD_ID, "mods"));
    public static final PacketCodec<PacketByteBuf, ModsPacket> CODEC = PacketCodec.of(ModsPacket::write, ModsPacket::decode);

    private void write(PacketByteBuf byteBuf) {


        /*try {
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
            MinecraftClient.getInstance().disconnect(e.getScreen(), false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
    }

    private static ModsPacket decode(PacketByteBuf byteBuf) {
        byteBuf.readerIndex(byteBuf.writerIndex());
        return new ModsPacket();
    }


    @Override
    public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }
}
