package net.sn0wix_.modObserver.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.sn0wix_.modObserver.ModObserver;

public class ModsRequestPacketPlay implements CustomPayload {
    public static final Id<ModsRequestPacketPlay> PAYLOAD_ID = new Id<>(Identifier.of(ModObserver.MOD_ID, "mods"));
    public static final PacketCodec<PacketByteBuf, ModsRequestPacketPlay> CODEC = PacketCodec.of(ModsRequestPacketPlay::write, ModsRequestPacketPlay::new);

    private final boolean checkTampering;
    private final boolean sendHashes;

    public ModsRequestPacketPlay(boolean checkTampering, boolean sendHashes) {
        this.checkTampering = checkTampering;
        this.sendHashes = sendHashes;
    }

    private ModsRequestPacketPlay(PacketByteBuf byteBuf) {
        this.checkTampering = byteBuf.readBoolean();
        this.sendHashes = byteBuf.readBoolean();
    }

    private void write(PacketByteBuf byteBuf) {
        byteBuf.writeBoolean(checkTampering);
        byteBuf.writeBoolean(sendHashes);
    }


    @Override
    public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }
}
