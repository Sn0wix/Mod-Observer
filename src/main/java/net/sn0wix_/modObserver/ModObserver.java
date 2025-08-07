package net.sn0wix_.modObserver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.sn0wix_.modObserver.networking.ModsRequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModObserver implements ClientModInitializer {
    public static final String MOD_ID = "mod_observer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final boolean HAS_MODMENU = FabricLoader.getInstance().getModContainer("modmenu").isPresent();
    public static boolean bl = false;


    @Override
    public void onInitializeClient() {

        //PayloadTypeRegistry.configurationC2S().register(ModsPacket.PAYLOAD_ID, ModsPacket.CODEC);
        PayloadTypeRegistry.configurationC2S().register(ModsRequestPacket.PAYLOAD_ID, ModsRequestPacket.CODEC);

        ClientConfigurationConnectionEvents.START.register((handler, client) -> {
            ClientConfigurationNetworking.send(new ModsRequestPacket(false, false));
        });


        /*ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if (minecraftClient.currentScreen instanceof TitleScreen && !bl) {
                minecraftClient.setScreen(new IncompatibleModsScreen(ModsScreen.Container.cast(FabricLoader.getInstance().getAllMods().stream().toList()), Text.translatable("text.mod_observer.required_mods")));
                bl = true;
            }
        });*/
    }
}
