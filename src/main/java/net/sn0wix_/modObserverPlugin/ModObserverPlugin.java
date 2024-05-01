package net.sn0wix_.modObserverPlugin;

import net.sn0wix_.modObserverPlugin.commands.ConfigurationCommand;
import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.listeners.Events;
import net.sn0wix_.modObserverPlugin.networking.PacketHandler;
import net.sn0wix_.modObserverPlugin.players.WaitingForResponsePlayers;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.logging.Logger;

public final class ModObserverPlugin extends JavaPlugin {
    public static Plugin PLUGIN;
    public static Logger LOGGER;
    public static FileConfiguration CONFIG;
    public static final String MOD_ID = "mod_observer";

    @Override
    public void onEnable() {
        //Initialize logger
        LOGGER = getLogger();

        //Initialize plugin
        PLUGIN = this;

        //Config
        CONFIG = getConfig();

        Config.loadValues(CONFIG);
        saveConfig();

        //Register listeners
        getServer().getPluginManager().registerEvents(new Events(), this);

        //Response timer
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!WaitingForResponsePlayers.isEmpty()) {
                    WaitingForResponsePlayers.checkForTimedOuts().forEach(player -> player.getSender().sendMessage(ChatColor.RED + "Request has timed out!"));
                }
            }
        }.runTaskTimer(this, 69, 20);

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
        getServer().getMessenger().registerOutgoingPluginChannel(this, PacketHandler.MOD_REQUEST_CHANNEL);

        //Messages to the console
        LOGGER.info("ModObserverPlugin initialized!");
        LOGGER.info("You can use command /modobserver to configure it, or you can do it manually in the config file.");
        LOGGER.info("If this plugin gives you errors when someone is kicked, ignore them.");
    }

    @Override
    public void onDisable() {
        //Config
        Config.saveValues(CONFIG);
        saveConfig();

        //Plugin messaging
        getServer().getMessenger().unregisterIncomingPluginChannel(this, PacketHandler.MODS_FOR_APPROVAL_CHANNEL);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, PacketHandler.MOD_REQUEST_CHANNEL);
        LOGGER.info("ModObserverPlugin disabled!");
    }
}
