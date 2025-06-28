package net.sn0wix_.modObserver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import java.util.logging.Logger;

public class ModObserver implements ClientModInitializer {
    public static final String MOD_ID = "mod_observer";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.configurationC2S().register(ModsForApprovalPacket.PAYLOAD_ID, ModsForApprovalPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ModsForApprovalPacket.PAYLOAD_ID, ModsForApprovalPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(ModsForApprovalPacket.PAYLOAD_ID, ModsForApprovalPacket.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ModsForApprovalPacket.PAYLOAD_ID, (payload, context) -> ClientPlayNetworking.send(new ModsForApprovalPacket()));

        ClientConfigurationConnectionEvents.START.register((handler, client) -> ClientConfigurationNetworking.send(new ModsForApprovalPacket()));
    }
}
