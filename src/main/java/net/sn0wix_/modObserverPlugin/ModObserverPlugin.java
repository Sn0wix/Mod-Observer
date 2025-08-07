package net.sn0wix_.modObserverPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.papermc.paper.connection.PlayerConnection;
import net.sn0wix_.modObserverPlugin.commands.ConfigurationCommand;
import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.listeners.Events;
import net.sn0wix_.modObserverPlugin.networking.PacketHandler;
import net.sn0wix_.modObserverPlugin.players.WaitingForResponsePlayers;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public final class ModObserverPlugin extends JavaPlugin {

    /*https://minecraft.wiki/w/Minecraft_Wiki:Projects/wiki.vg_merge/Minecraft_Forge_Handshake
     * Forge handshake emulation - config enabled
     * S -> C mods wanted packet - bl check tampering, bl send children, bl send hashes
     * C -> S mods packet - json?
     * S -> C kick (optional) - string message, json? mods
     *
     * Json packet formatting
     * serverside tampering detection - config enabled
     * Mod jars hashes - config enabled
     *
     * whitelist, blacklist, required mods jsons
     * Config generator tool
     * */
    public static Plugin PLUGIN;
    public static Logger LOGGER;
    public static FileConfiguration CONFIG;
    public static final String MOD_ID = "mod_observer";
    public static BukkitRunnable pluginTicker;
    ProtocolManager manager;

    @Override
    public void onEnable() {
        manager = ProtocolLibrary.getProtocolManager();

        getServer().getMessenger().registerIncomingPluginChannel(this, "mod_observer:mods", new PluginMessageListener() {
            @Override
            public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
                LOGGER.info("RECEIVED");
            }


            @Override
            public void onPluginMessageReceived(@NotNull String channel, @NotNull PlayerConnection connection, byte @NotNull [] message) {
                LOGGER.info("RECEIVED");
            }
        });


        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Configuration.Client.CUSTOM_PAYLOAD) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                LOGGER.info("CUSTOM PAYLOAD PACKET");
                LOGGER.info(event.getPacket().toString());
                LOGGER.info(event.toString());
                super.onPacketReceiving(event);
            }
        });

        /*manager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Client.CUSTOM_PAYLOAD) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                LOGGER.info("CUSTOM PAYLOAD PACKET");
                LOGGER.info(event.getPacket().toString());
                LOGGER.info(event.toString());
                super.onPacketReceiving(event);
            }
        });*/

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
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, PacketHandler.MODS_FOR_APPROVAL_CHANNEL);
        LOGGER.info("ModObserver disabled!");*/
    }
}
