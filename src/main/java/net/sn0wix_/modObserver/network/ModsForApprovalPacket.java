package net.sn0wix_.modObserver.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.sn0wix_.modObserver.ModObserver;

public class ModsForApprovalPacket implements CustomPayload {
    public static final Identifier ID = Identifier.of(ModObserver.MOD_ID, "mods_for_approval");
    public static final Id<ModsForApprovalPacket> PAYLOAD_ID = new Id<>(ID);
    public static final PacketCodec<PacketByteBuf, ModsForApprovalPacket> CODEC = PacketCodec.of(ModsForApprovalPacket::write,
            ModsForApprovalPacket::decode);


    private static ModsForApprovalPacket decode(PacketByteBuf byteBuf) {
        return new ModsForApprovalPacket();
    }

    public void write(PacketByteBuf byteBuf) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String str : ModObserver.getMods()) {
            stringBuilder.append(str).append(",");
        }

        byteBuf.writeBytes(stringBuilder.toString().getBytes());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }
}
