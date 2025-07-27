package net.sn0wix_.modObserver.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.sn0wix_.modObserver.ModObserver;

public class ModsRequestPacket implements CustomPayload {
    public static final Id<ModsRequestPacket> PAYLOAD_ID = new Id<>(Identifier.of(ModObserver.MOD_ID, "mods_request"));
    public static final PacketCodec<PacketByteBuf, ModsRequestPacket> CODEC = PacketCodec.of(ModsRequestPacket::write, ModsRequestPacket::new);

    private final boolean checkTampering;
    private final boolean sendHashes;

    public ModsRequestPacket(boolean checkTampering, boolean sendHashes) {
        this.checkTampering = checkTampering;
        this.sendHashes = sendHashes;
    }

    private ModsRequestPacket(PacketByteBuf byteBuf) {
        this.checkTampering = byteBuf.readBoolean();
        this.sendHashes = byteBuf.readBoolean();
    }

    private void write(PacketByteBuf byteBuf) {
        //just in case it gets send during singleplayer for some reason
        byteBuf.writeBoolean(false);
        byteBuf.writeBoolean(false);
    }


    @Override
    public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }
}
