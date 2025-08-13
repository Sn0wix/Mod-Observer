package net.sn0wix_.modobserver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.sn0wix_.modobserver.networking.ModsPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModObserver implements ClientModInitializer {
    public static final String MOD_ID = "mod_observer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final boolean HAS_MODMENU = FabricLoader.getInstance().getModContainer("modmenu").isPresent();


    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.configurationC2S().register(ModsPacket.PAYLOAD_ID, ModsPacket.CODEC);

        ClientConfigurationConnectionEvents.START.register((handler, client) -> ClientConfigurationNetworking.send(new ModsPacket()));
    }
}
