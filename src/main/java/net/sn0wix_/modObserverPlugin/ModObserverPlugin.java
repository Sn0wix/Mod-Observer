package net.sn0wix_.modObserverPlugin;

import net.sn0wix_.modObserverPlugin.commands.ConfigurationCommand;
import net.sn0wix_.modObserverPlugin.commands.ConfirmCommand;
import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.listeners.Events;
import net.sn0wix_.modObserverPlugin.networking.PacketHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

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

        //Commands
        try {
            getCommand("modObserver").setExecutor(new ConfigurationCommand());
            getCommand("modObserverConfirm").setExecutor(new ConfirmCommand());
        } catch (NullPointerException e) {
            LOGGER.warning("Could not register command interface. Configuration must be done directly in the config file.");
        }

        //Plugin messaging
        getServer().getMessenger().registerIncomingPluginChannel(this, PacketHandler.MODS_FOR_APPROVAL_CHANNEL, PacketHandler::receive);
        getServer().getMessenger().registerOutgoingPluginChannel(this, PacketHandler.MOD_REQUEST_CHANNEL);

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
        getServer().getMessenger().unregisterIncomingPluginChannel(this, PacketHandler.MODS_FOR_APPROVAL_CHANNEL);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, PacketHandler.MOD_REQUEST_CHANNEL);
    }
}
