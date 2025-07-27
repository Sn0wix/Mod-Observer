package net.sn0wix_.modObserver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.sn0wix_.modObserver.detection.ModEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

public class ModObserver implements ClientModInitializer {
    public static final String MOD_ID = "mod_observer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);



    @Override
    public void onInitializeClient() {
        LinkedHashMap<ModEntry, Object> mods = new LinkedHashMap<>();

        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            if (modContainer.getContainingMod().isEmpty()) {
                mods.put(new ModEntry(modContainer, Utils.getSHA256(modContainer)), Utils.getChildren(modContainer));
            }
        });

        LOGGER.info(mods.toString());
    }
}
