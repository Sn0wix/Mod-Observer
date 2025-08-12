package net.sn0wix_.modObserverPlugin;

import net.sn0wix_.modObserverPlugin.commands.ConfigurationCommand;
import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.config.JsonLoader;
import net.sn0wix_.modObserverPlugin.listeners.Events;
import net.sn0wix_.modObserverPlugin.networking.PacketHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

//TODO remove . files from git history
//TODO test event.getPlayer().getConnection().reenterConfiguration();
public final class ModObserver extends JavaPlugin {

    /*https://minecraft.wiki/w/Minecraft_Wiki:Projects/wiki.vg_merge/Minecraft_Forge_Handshake
     * Forge handshake emulation - config enabled
     * C -> S mods packet - json?
     * S -> C kick (optional) - string message, json? mods
     *
     * Mod jars hashes - config enabled
     *
     * whitelist, blacklist, required mods, jsons
     * Config generator tool, modmenu?
     * */
    private static Plugin instance;
    public static Logger LOGGER;
    @Deprecated
    public static FileConfiguration CONFIG;
    public static final String MOD_ID = "mod_observer";

    @Override
    public void onEnable() {
        //Initialize variables
        instance = this;
        LOGGER = getLogger();

        //Configs
        saveDefaultConfig();
        Config.init(getConfig());
        saveConfig();

        JsonLoader.init();

        //Registering stuff
        getServer().getPluginManager().registerEvents(new Events(), this);
        getServer().getMessenger().registerIncomingPluginChannel(this, "mod_observer:mods", new PacketHandler());

        if (Config.isCommandInterfaceAllowed()) {
            try {
                LOGGER.info("Registering command interface...");
                getCommand("modObserver").setExecutor(new ConfigurationCommand());
            } catch (NullPointerException e) {
                LOGGER.warning("Could not register command interface. Configuration must be done directly in the config file.");
            }
        }

        LOGGER.info("ModObserver initialized!");
        /*//Initialize logger
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
        LOGGER.info("----------------ModObserver----------------");*/
    }

    @Override
    public void onDisable() {
        /*//Config
        Config.saveValues(CONFIG);
        saveConfig();

        //Plugin ticker
        pluginTicker.cancel();

        //Plugin messaging
        getServer().getMessenger().unregisterIncomingPluginChannel(this, PacketHandler.MODS_FOR_APPROVAL_CHANNEL);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, PacketHandler.MODS_FOR_APPROVAL_CHANNEL);*/

        getServer().getMessenger().unregisterIncomingPluginChannel(this, "mod_observer:mods", new PacketHandler());
        LOGGER.info("ModObserver disabled!");
    }

    public static Plugin getInstance() {
        return instance;
    }
}
