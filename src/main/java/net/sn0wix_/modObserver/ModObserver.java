package net.sn0wix_.modObserver;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.sn0wix_.modObserver.network.ModsForApprovalPacket;

import java.util.ArrayList;
import java.util.List;

public class ModObserver implements ModInitializer {
    public static final String MOD_ID = "mod_observer";


    public static List<String> getMods() {
        ArrayList<String> mods = new ArrayList<>(FabricLoader.getInstance().getAllMods().size());
        for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            mods.add(modContainer.getMetadata().getId());
        }
        return mods;
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.configurationC2S().register(ModsForApprovalPacket.PAYLOAD_ID, ModsForApprovalPacket.CODEC);

        ClientConfigurationConnectionEvents.INIT.register((handler, client) -> {
            ClientConfigurationNetworking.send(new ModsForApprovalPacket());
        });
    }
}
