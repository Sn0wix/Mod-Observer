package net.sn0wix_;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ModObserver implements ClientModInitializer {
    public static final String MOD_ID = "mod_observer";
    public static final Logger LOGGER = LoggerFactory.getLogger("mod_observer");
    public static Identifier MODS_FOR_APPROVAL_PACKET = new Identifier(MOD_ID, "mods_for_approval");
    public static Identifier MOD_REQUEST_PACKET = new Identifier(MOD_ID, "request_mods");

    @Override
    public void onInitializeClient() {
        ClientConfigurationConnectionEvents.INIT.register((handler, client) -> ClientConfigurationNetworking.send(MODS_FOR_APPROVAL_PACKET, getModsBuf()));
        ClientPlayNetworking.registerGlobalReceiver(MOD_REQUEST_PACKET, (client, handler, buf, responseSender) -> client.execute(() -> ClientPlayNetworking.send(MODS_FOR_APPROVAL_PACKET, getModsBuf())));
    }

    private static PacketByteBuf getModsBuf() {
        PacketByteBuf buf = PacketByteBufs.create();
        StringBuilder stringBuilder = new StringBuilder();

        for (String str : getMods()) {
            stringBuilder.append(str).append(",");
        }

        buf.writeBytes(stringBuilder.toString().getBytes());

        return buf;
    }

    private static List<String> getMods() {
        ArrayList<String> mods = new ArrayList<>(FabricLoader.getInstance().getAllMods().size());
        for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            mods.add(modContainer.getMetadata().getId());
        }
        return mods;
    }
}
