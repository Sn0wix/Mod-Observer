package net.sn0wix_.modObserverPlugin.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.sn0wix_.modObserverPlugin.ModObserver;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Objects;

public class Config {
    private static final String ignoredPlayersKey = "ignored-players";
    private static final String allowCommandInterfaceKey = "allow-command-interface";
    private static final String modeKey = "mode";
    private static final String modObserverRequiredMessageKey = "modobserver-required-message";
    private static final String useHashesKey = "use-hashes";
    private static final String checkNestedModsKey = "check-nested-mods";
    private static final String fabricApiAutodetectKey = "fabric-api-autodetect";
    private static final String modobserverKickMssageKey = "modobserver-kick-message";
    private static FileConfiguration config;



    public static void init(FileConfiguration fileConfiguration) {
        config = fileConfiguration;
        config.setComments(modeKey, List.of("Check out ModObserver wiki for further information:", "https://github.com/Sn0wix/Mod-Observer/wiki"));
    }



    public static List<String> getIgnoredPlayers() {
        return config.getStringList(ignoredPlayersKey);
    }

    public static boolean isCommandInterfaceAllowed() {
        return config.getBoolean(allowCommandInterfaceKey);
    }

    public static Mode getMode() {
        String loadedValue = config.getString(modeKey);

        for (Mode value : Mode.values()) {
            if (loadedValue != null && loadedValue.equals(value.getName())) {
                return value;
            }
        }

        ModObserver.LOGGER.warning("Found illegal property value, mode: " + loadedValue + ", setting mode to \"whitelist\"");
        return Mode.WHITELIST;
    }

    public static Component getModObserverRequiredMessage() {
        return MiniMessage.miniMessage().deserialize(Objects.requireNonNull(config.getString(modObserverRequiredMessageKey)));
    }

    public static boolean useHashes() {
        return config.getBoolean(useHashesKey);
    }

    public static boolean checkNestedMods() {
        return config.getBoolean(checkNestedModsKey);
    }

    public static boolean fabricApiAutodetect() {
        return config.getBoolean(fabricApiAutodetectKey);
    }
    public static Component getModObserverKickMessage() {
        return MiniMessage.miniMessage().deserialize(Objects.requireNonNull(config.getString(modobserverKickMssageKey)));
    }



    public static void setFabricApiAutodetect(String value) {
        config.set(modobserverKickMssageKey, value);
        save();
    }

    public static void setFabricApiAutodetect(boolean value) {
        config.set(fabricApiAutodetectKey, value);
        save();
    }

    public static void setCheckNestedMods(boolean value) {
        config.set(checkNestedModsKey, value);
        save();
    }

    public static void setUseHashes(boolean value) {
        config.set(useHashesKey, value);
        save();
    }

    public static void setIgnoredPlayers(List<String> ignoredPlayers) {
        config.set(ignoredPlayersKey, ignoredPlayers);
        save();
    }

    public static void setAllowCommandInterface(boolean allow) {
        config.set(allowCommandInterfaceKey, allow);
        save();
    }

    public static void setMode(Mode mode) {
        config.set(modeKey, mode.getName());
        save();
    }

    public static void setModObserverRequiredMessage(String message) {
        config.set(modObserverRequiredMessageKey, message);
        save();
    }

    public static void save() {
        ModObserver.getInstance().saveConfig();
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
