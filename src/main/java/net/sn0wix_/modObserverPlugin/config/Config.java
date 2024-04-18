package net.sn0wix_.modObserverPlugin.config;

import net.sn0wix_.modObserverPlugin.ModObserverPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
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
    /*public static List<String> COMMENTS = List.of(
            "mode: Can have values: whitelist, which will allow only the whitelisted mods, or blacklist, which will allow everything else, except the blacklisted mods.",
            "prohibited_mods_found_message: This message will be the kick message if the player has prohibited mods.",
            "whitelisted_mods: Whitelist for mods, use the modid of the mod, otherwise it won't work. What is modid? https://fabricmc.net/wiki/tutorial:terms.",
            "do_not_check_players: Players that bypass the mod check, use player names, not uuids.",
            "Having issues? try adding \"mod_observer\" to the whitelist.");*/

    private static final String MODE_PATH = "mode";
    private static final String REQUIRED_MODS_PATH = "required_mods";
    private static final String WHITELISTED_MODS_PATH = "whitelisted_mods";
    private static final String BLACKLISTED_MODS_PATH = "blacklisted_mods";
    private static final String REQUIRED_MODS_MESSAGE_PATH = "required_mods_message";
    private static final String PROHIBITED_MODS_FOUND_MESSAGE_PATH = "prohibited_mods_found_message";
    private static final String MOD_OBSERVER_REQUIRED_MESSAGE_PATH = "mod_observer_required_message";
    private static final String IGNORED_PLAYERS_PATH = "ignored_players";

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
        saveValues(ModObserverPlugin.CONFIG);
    }

    public static void saveValues(FileConfiguration config) {
        config.set(MODE_PATH, MODE.getName());
        config.set(REQUIRED_MODS_PATH, REQUIRED_MODS);
        config.set(WHITELISTED_MODS_PATH, WHITELISTED_MODS);
        config.set(BLACKLISTED_MODS_PATH, BLACKLISTED_MODS);
        config.set(REQUIRED_MODS_MESSAGE_PATH, REQUIRED_MODS_MESSAGE);
        config.set(PROHIBITED_MODS_FOUND_MESSAGE_PATH, PROHIBITED_MODS_FOUND_MESSAGE);
        config.set(MOD_OBSERVER_REQUIRED_MESSAGE_PATH, MOD_OBSERVER_REQUIRED_MESSAGE);
        config.set(IGNORED_PLAYERS_PATH, IGNORED_PLAYERS);
    }

    public static List<String> getDefaultWhitelist(FileConfiguration configuration) {
        try {
            return configuration.getDefaults().getStringList(WHITELISTED_MODS_PATH);
        } catch (NullPointerException e) {
            ModObserverPlugin.LOGGER.warning("Could not load default whitelist!");
            return null;
        }
    }

    /*public static void addComments(FileConfiguration config) {
        config.setComments("comments", COMMENTS);
    }*/


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
