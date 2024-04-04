package net.sn0wix_.modObserverPlugin;

import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.listeners.Events;
import net.sn0wix_.modObserverPlugin.networking.ModMessagingHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Logger;

public final class ModObserverPlugin extends JavaPlugin {
    public static Logger LOGGER;
    public static FileConfiguration CONFIG;
    public static final String MOD_ID = "mod_observer";

    //TODO command configuring
    @Override
    public void onEnable() {
        //Initialize logger
        LOGGER = getServer().getLogger();

        //Register listeners
        getServer().getPluginManager().registerEvents(new Events(), this);

        //Plugin messaging
        getServer().getMessenger().registerIncomingPluginChannel(this, ModMessagingHandler.MODS_FOR_APPROVAL_CHANNEL, ModMessagingHandler::receive);

        //Config
        CONFIG = getConfig();
        saveConfig();

        Config.loadValues(CONFIG);
    }

    @Override
    public void onDisable() {
        //Config
        Config.saveValues(CONFIG);
        saveConfig();

        //Plugin messaging
        getServer().getMessenger().unregisterIncomingPluginChannel(this, ModMessagingHandler.MODS_FOR_APPROVAL_CHANNEL);
    }


    //Mod checking
    public static ArrayList<String> getNonApprovedMods(String[] modids) {
        ArrayList<String> notApprovedMods = new ArrayList<>();

        if (Config.MODE.equals(Config.Mode.WHITELIST)) {
            for (String modid : modids) {
                if (!Config.WHITELISTED_MODS.contains(modid)) {
                    notApprovedMods.add(modid);
                }
            }
        } else if (Config.MODE.equals(Config.Mode.BLACKLIST)) {
            for (String modid : modids) {
                if (Config.BLACKLISTED_MODS.contains(modid)) {
                    notApprovedMods.add(modid);
                }
            }
        }
        return notApprovedMods;
    }
}
