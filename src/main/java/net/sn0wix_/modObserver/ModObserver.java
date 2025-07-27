package net.sn0wix_.modObserver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.sn0wix_.modObserver.networking.ModsPacket;
import net.sn0wix_.modObserver.networking.ModsRequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModObserver implements ClientModInitializer {
    public static final String MOD_ID = "mod_observer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.configurationC2S().register(ModsPacket.PAYLOAD_ID, ModsPacket.CODEC);
        PayloadTypeRegistry.configurationS2C().register(ModsRequestPacket.PAYLOAD_ID, ModsRequestPacket.CODEC);
    }
}
