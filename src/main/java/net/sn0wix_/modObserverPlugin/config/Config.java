package net.sn0wix_.modObserverPlugin.config;

import net.sn0wix_.modObserverPlugin.ModObserverPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config {
    public static Mode MODE;
    public static List<String> REQUIRED_MODS;
    public static List<String> WHITELISTED_MODS;
    public static List<String> BLACKLISTED_MODS;
    public static String REQUIRED_MODS_MESSAGE;
    public static String PROHIBITED_MODS_FOUND_MESSAGE;
    public static String MOD_OBSERVER_REQUIRED_MESSAGE;
    public static ArrayList<String> IGNORED_PLAYERS;
    public static Boolean ALLOW_COMMAND_INTERFACE;


    private static final String MODE_PATH = "mode";
    private static final String REQUIRED_MODS_PATH = "required_mods";
    private static final String WHITELISTED_MODS_PATH = "whitelisted_mods";
    private static final String BLACKLISTED_MODS_PATH = "blacklisted_mods";
    private static final String REQUIRED_MODS_MESSAGE_PATH = "required_mods_message";
    private static final String PROHIBITED_MODS_FOUND_MESSAGE_PATH = "prohibited_mods_found_message";
    private static final String MOD_OBSERVER_REQUIRED_MESSAGE_PATH = "mod_observer_required_message";
    private static final String IGNORED_PLAYERS_PATH = "ignored_players";
    private static final String ALLOW_COMMAND_INTERFACE_PATH = "allow_command_interface";

    public static void loadDefaults(FileConfiguration config) {
        String mode = config.getString(MODE_PATH);

        for (Mode value : Mode.values()) {
            if (mode != null && mode.equals(value.getName())) {
                MODE = value;
            }
        }

        if (MODE == null) {
            MODE = Mode.WHITELIST;
            ModObserverPlugin.LOGGER.warning("Found illegal property value, mode: " + mode + ", setting mode to \"whitelist\"");
        }
        try {
            REQUIRED_MODS = config.getDefaults().getStringList(REQUIRED_MODS_PATH);
            WHITELISTED_MODS = config.getDefaults().getStringList(WHITELISTED_MODS_PATH);
            BLACKLISTED_MODS = config.getDefaults().getStringList(BLACKLISTED_MODS_PATH);
            REQUIRED_MODS_MESSAGE = config.getDefaults().getString(REQUIRED_MODS_MESSAGE_PATH);
            PROHIBITED_MODS_FOUND_MESSAGE = config.getDefaults().getString(PROHIBITED_MODS_FOUND_MESSAGE_PATH);
            MOD_OBSERVER_REQUIRED_MESSAGE = config.getDefaults().getString(MOD_OBSERVER_REQUIRED_MESSAGE_PATH);
            IGNORED_PLAYERS = (ArrayList<String>) config.getDefaults().getStringList(IGNORED_PLAYERS_PATH);
            ALLOW_COMMAND_INTERFACE = config.getDefaults().getBoolean(ALLOW_COMMAND_INTERFACE_PATH);
        } catch (NullPointerException e) {
            ModObserverPlugin.LOGGER.warning("Can not load config defaults! Try deleting current config file to load them. If the issue persists, download the plugin again.");
        }

        saveValues(ModObserverPlugin.CONFIG);
    }

    public static void loadValues(FileConfiguration config) {
        String mode = config.getString(MODE_PATH);

        for (Mode value : Mode.values()) {
            if (mode != null && mode.equals(value.getName())) {
                MODE = value;
            }
        }

        if (MODE == null) {
            MODE = Mode.WHITELIST;
            ModObserverPlugin.LOGGER.warning("Found illegal property value, mode: " + mode + ", setting mode to \"whitelist\"");
        }

        REQUIRED_MODS = config.getStringList(REQUIRED_MODS_PATH);
        WHITELISTED_MODS = config.getStringList(WHITELISTED_MODS_PATH);
        BLACKLISTED_MODS = config.getStringList(BLACKLISTED_MODS_PATH);
        REQUIRED_MODS_MESSAGE = config.getString(REQUIRED_MODS_MESSAGE_PATH);
        PROHIBITED_MODS_FOUND_MESSAGE = config.getString(PROHIBITED_MODS_FOUND_MESSAGE_PATH);
        MOD_OBSERVER_REQUIRED_MESSAGE = config.getString(MOD_OBSERVER_REQUIRED_MESSAGE_PATH);
        IGNORED_PLAYERS = (ArrayList<String>) config.getStringList(IGNORED_PLAYERS_PATH);
        ALLOW_COMMAND_INTERFACE = config.getBoolean(ALLOW_COMMAND_INTERFACE_PATH);
        saveValues(ModObserverPlugin.CONFIG);
    }

    public static void saveValues(FileConfiguration config) {
        //Comments
        config.setComments(MODE_PATH, List.of("EDIT THIS FILE ONLY IF THE SERVER IS NOT RUNNING, OTHERWISE ALL CHANGES WILL BE LOST!", "USE /modobserver TO CONFIGURE THE PLUGIN IN-GAME", "Can have values: \"whitelist\", which will allow only the whitelisted mods, or \"blacklist\", which will allow everything else, except the blacklisted mods."));
        config.setComments(REQUIRED_MODS_MESSAGE_PATH, List.of("Mods that the user must have installed. You can just leave this empty."));
        config.setComments(WHITELISTED_MODS_PATH, List.of("Whitelist for mods, use modid of the mod.", "What is modid? https://fabricmc.net/wiki/tutorial:terms",
                "The default whitelist has all the mods, that are needed for ModObserver to work.", "If you deleted these mods, you can use /modobserver whitelist addDefaults to add them back."));
        config.setComments(BLACKLISTED_MODS_PATH, List.of("Blacklist for mods."));
        config.setComments(REQUIRED_MODS_MESSAGE, List.of("Message that is sent when player doesn't have required mods. Use <$MODS$> where you want the list to be added.", "If you want to add a link here, do it like this: [Click me!](https://your_link_here). This applies to all messages."));
        config.setComments(PROHIBITED_MODS_FOUND_MESSAGE_PATH, List.of("This message will be the kick message if the player has prohibited mods."));
        config.setComments(MOD_OBSERVER_REQUIRED_MESSAGE, List.of("Message that will show up, if the player tries to connect without ModObserver."));
        config.setComments(IGNORED_PLAYERS_PATH, List.of("Players that bypass the mod check. Use player names, not uuids."));
        config.setComments(ALLOW_COMMAND_INTERFACE_PATH, List.of("You can turn off commands used to configure this plugin. Updating this option requires server restart.", "If you turn this off, /modobserver will still show up in the commands list, but it won't do anything."));

        //Values
        config.set(MODE_PATH, MODE.getName());
        config.set(REQUIRED_MODS_PATH, REQUIRED_MODS);
        config.set(WHITELISTED_MODS_PATH, WHITELISTED_MODS);
        config.set(BLACKLISTED_MODS_PATH, BLACKLISTED_MODS);
        config.set(REQUIRED_MODS_MESSAGE_PATH, REQUIRED_MODS_MESSAGE);
        config.set(PROHIBITED_MODS_FOUND_MESSAGE_PATH, PROHIBITED_MODS_FOUND_MESSAGE);
        config.set(MOD_OBSERVER_REQUIRED_MESSAGE_PATH, MOD_OBSERVER_REQUIRED_MESSAGE);
        config.set(IGNORED_PLAYERS_PATH, IGNORED_PLAYERS);
        config.set(ALLOW_COMMAND_INTERFACE_PATH, ALLOW_COMMAND_INTERFACE);
    }

    public static List<String> getDefaultWhitelist(FileConfiguration configuration) {
        try {
            return configuration.getDefaults().getStringList(WHITELISTED_MODS_PATH);
        } catch (NullPointerException e) {
            ModObserverPlugin.LOGGER.warning("Could not load default whitelist!");
            return null;
        }
    }

    //TODO
    public static String parseMessage(String message) {
        //check if the string contains links
        if (message.contains("](")) {
            HashMap<String, String> links = new HashMap<>(1);
            StringBuilder text = new StringBuilder();
            boolean hasText = false;
            boolean hasLink = false;
            boolean shouldHadLink = false;
            boolean isLinkFinished = false;

            for (int i = 0; i < message.length(); i++) {
                char s = message.charAt(i);

                if (s == '[') {
                    hasText = true;
                    continue;
                }
                if (s == ']') {
                    hasText = false;
                    shouldHadLink = true;
                    continue;
                }
                if (hasText) {
                    text.append(s);
                }
                if (shouldHadLink && s == '(') {

                } else {
                    text = new StringBuilder();
                }
            }
        }

        return message;
    }

    public enum Mode {
        WHITELIST("whitelist"),
        BLACKLIST("blacklist");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
