package net.sn0wix_.modObserverPlugin;

import net.sn0wix_.modObserverPlugin.commands.ConfigurationCommand;
import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.listeners.Events;
import net.sn0wix_.modObserverPlugin.networking.PacketHandler;
import net.sn0wix_.modObserverPlugin.players.OnlinePlayersToCheck;
import net.sn0wix_.modObserverPlugin.players.WaitingForResponsePlayers;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

public final class ModObserverPlugin extends JavaPlugin {
    public static Plugin PLUGIN;
    public static Logger LOGGER;
    public static FileConfiguration CONFIG;
    public static final String MOD_ID = "mod_observer";
    public static BukkitRunnable pluginTicker;

    @Override
    public void onEnable() {
        //Initialize logger
        LOGGER = getLogger();
        LOGGER.info("----------------ModObserver----------------");

        //Initialize plugin
        PLUGIN = this;

        //Config
        CONFIG = getConfig();

        Config.loadValues(CONFIG);
        saveConfig();

        //Register listeners
        getServer().getPluginManager().registerEvents(new Events(), this);

        //Plugin ticker
        pluginTicker = new BukkitRunnable() {
            @Override
            public void run() {
                if (!WaitingForResponsePlayers.isEmpty()) {
                    WaitingForResponsePlayers.checkForTimedOuts().forEach(player -> {
                        if (player.getSender() != null) {
                            player.getSender().sendMessage(ChatColor.RED + "Mod packet request has timed out!");
                        }
                    });
                }

                OnlinePlayersToCheck.tick();
            }
        };
        pluginTicker.runTaskTimer(this, 69, 20);

        //Commands
        if (Config.ALLOW_COMMAND_INTERFACE) {
            try {
                getCommand("modObserver").setExecutor(new ConfigurationCommand());
            } catch (NullPointerException e) {
                LOGGER.warning("Could not register command interface. Configuration must be done directly in the config file.");
            }
        }

        //Plugin messaging
        getServer().getMessenger().registerIncomingPluginChannel(this, PacketHandler.MODS_FOR_APPROVAL_CHANNEL, PacketHandler::receive);
        getServer().getMessenger().registerOutgoingPluginChannel(this, PacketHandler.MODS_FOR_APPROVAL_CHANNEL);

        //Messages to the console
        LOGGER.info("ModObserver initialized!");
        LOGGER.info("Here is a quick tutorial: \nWhen joining for the first time, it might not let you in, so execute /modobserver ignoredPlayers add <yourName>" +
                "\nThis will add you to the Ignored players list, so you won't be checked.\nOnce you are in game, execute /modobserver help to view the help message." +
                "\nAnd don't forget to remove yourself from the Ignored players list: /modobserver ignoredPlayers remove <yourName>");
        LOGGER.info("If this plugin gives you errors when someone is kicked, ignore them.");
        LOGGER.info("----------------ModObserver----------------");
    }

    @Override
    public void onDisable() {
        //Config
        Config.saveValues(CONFIG);
        saveConfig();

        //Plugin ticker
        pluginTicker.cancel();

        //Plugin messaging
        getServer().getMessenger().unregisterIncomingPluginChannel(this, PacketHandler.MODS_FOR_APPROVAL_CHANNEL);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, PacketHandler.MODS_FOR_APPROVAL_CHANNEL);
        LOGGER.info("ModObserver disabled!");
    }
}
