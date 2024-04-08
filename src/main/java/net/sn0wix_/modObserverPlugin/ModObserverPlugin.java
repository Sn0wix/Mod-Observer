package net.sn0wix_.modObserverPlugin;

import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.listeners.Events;
import net.sn0wix_.modObserverPlugin.networking.ModMessagingHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Logger;

public final class ModObserverPlugin extends JavaPlugin {
    public static Logger LOGGER;
    public static FileConfiguration CONFIG;
    public static final String MOD_ID = "mod_observer";

    //TODO command configuring
    //TODO add config optiongs: enableCommandConfiguration
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

        Config.loadValues(CONFIG);
        saveConfig();
    }

    @Override
    public void onDisable() {
        //Config
        Config.saveValues(CONFIG);
        saveConfig();

        //Plugin messaging
        getServer().getMessenger().unregisterIncomingPluginChannel(this, ModMessagingHandler.MODS_FOR_APPROVAL_CHANNEL);
    }


    public static boolean checkPlayer(Player player) {
        boolean bl = true;
        if (!IncomingPlayers.isApproved(player.getName())) {
            if (!IncomingPlayers.hasSendPacket(player.getName())) {
                player.kickPlayer(Config.MOD_OBSERVER_REQUIRED_MESSAGE);
                bl = false;
            } else if (!IncomingPlayers.getNonApprovedMods(player.getName()).isEmpty()) {
                player.kickPlayer(Config.PROHIBITED_MODS_FOUND_MESSAGE.replace("<$MODS$>", IncomingPlayers.getNonApprovedMods(player.getName())));
                bl = false;
            } else if (!IncomingPlayers.getMissingRequiredMods(player.getName()).isEmpty()) {
                player.kickPlayer(Config.REQUIRED_MODS_MESSAGE.replace("<$MODS$>", IncomingPlayers.getMissingRequiredMods(player.getName())));
                bl = false;
            }
        }

        return bl;
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

    public static ArrayList<String> getMissingRequiredMods(String[] modids) {
        ArrayList<String> missingRequiredMods = new ArrayList<>();
        ArrayList<String> receivedRequiredMods = new ArrayList<>();

        if (!Config.REQUIRED_MODS.isEmpty()) {
            for (String modid : modids) {
                if (Config.REQUIRED_MODS.contains(modid)) {
                    return receivedRequiredMods;
                }
            }

           missingRequiredMods.addAll(Config.REQUIRED_MODS);
           missingRequiredMods.removeAll(receivedRequiredMods);
        }

        return missingRequiredMods;
    }
}
