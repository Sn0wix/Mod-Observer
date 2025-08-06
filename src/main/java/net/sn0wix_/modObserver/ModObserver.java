package net.sn0wix_.modObserver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Identifier;
import net.sn0wix_.modObserver.networking.ModsPacket;
import net.sn0wix_.modObserver.networking.ModsRequestPacket;
import net.sn0wix_.modObserver.screen.ModsScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ModObserver implements ClientModInitializer {
    public static final String MOD_ID = "mod_observer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean bl = false;


    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.configurationC2S().register(ModsPacket.PAYLOAD_ID, ModsPacket.CODEC);
        PayloadTypeRegistry.configurationS2C().register(ModsRequestPacket.PAYLOAD_ID, ModsRequestPacket.CODEC);


        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if (minecraftClient.currentScreen instanceof TitleScreen && !bl) {
                minecraftClient.setScreen(new ModsScreen(ModsScreen.Container.cast(FabricLoader.getInstance().getAllMods().stream().toList())));
                bl = true;
            }
        });
    }
}
