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

public final class ModObserver extends JavaPlugin {

    /*https://minecraft.wiki/w/Minecraft_Wiki:Projects/wiki.vg_merge/Minecraft_Forge_Handshake
     * Forge handshake emulation - config enabled - maybe
     * Config generator tool, modmenu?
     *
     * https://docs.papermc.io/paper/dev/dialogs/
     * Mod observer required - open the website
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
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this, "mod_observer:mods", new PacketHandler());
        LOGGER.info("ModObserver disabled!");
    }

    public static Plugin getInstance() {
        return instance;
    }
}
