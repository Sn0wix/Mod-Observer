package net.sn0wix_.modObserverPlugin.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.sn0wix_.modObserverPlugin.ModObserver;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Objects;

public class Config {
    private static final String ignoredPlayersKey = "ignoredPlayers";
    private static final String allowCommandInterfaceKey = "allowCommandInterface";
    private static final String modeKey = "mode";
    private static final String modObserverRequiredMessageKey = "modObserverRequiredMessage";
    private static FileConfiguration config;

    public static void init(FileConfiguration fileConfiguration) {
        config = fileConfiguration;
    }


    public static List<String> getIgnoredPlayersKey() {
        return config.getStringList(ignoredPlayersKey);
    }

    public static boolean getAllowCommandInterfaceKey() {
        return config.getBoolean(allowCommandInterfaceKey);
    }

    public Mode getMode() {
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



    public static void setIgnoredPlayers(List<String> ignoredPlayers) {
        config.set(ignoredPlayersKey, ignoredPlayers);
    }

    public static void setAllowCommandInterface(boolean allow) {
        config.set(allowCommandInterfaceKey, allow);
    }

    public static void setMode(Mode mode) {
        config.set(modeKey, mode.getName());
    }

    public static void setModObserverRequiredMessage(String message) {
        config.set(modObserverRequiredMessageKey, message);
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
