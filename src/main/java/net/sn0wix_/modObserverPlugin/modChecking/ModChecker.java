package net.sn0wix_.modObserverPlugin.modChecking;

import io.papermc.paper.connection.PlayerConnection;
import net.sn0wix_.modObserverPlugin.ModObserver;
import net.sn0wix_.modObserverPlugin.config.JsonLoader;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModChecker {
    private static final Map<ModEntry, Object> whitelist = new ConcurrentHashMap<>();
    private static final Map<ModEntry, Object> blacklist = new ConcurrentHashMap<>();
    private static final Map<ModEntry, Object> required = new ConcurrentHashMap<>();

    public static void handle(String content, PlayerConnection connection) {
        ModObserver.LOGGER.info(content);
        ModObserver.LOGGER.info(JsonLoader.loadJsonFromString(content).toString());
        Map<ModEntry, Object> clientMods = toModEntryMap(JsonLoader.loadJsonFromString(content));

    }

    public static Map<ModEntry, Object> toModEntryMap(Map<String, Object> map) {
        HashMap<ModEntry, Object> newMap = new HashMap<>();

        map.forEach((key, value) -> {
            String modid = key.split(" ")[0];
            String hash;

            try {
                hash = key.split(" ")[1];
            } catch (IndexOutOfBoundsException ignored) {
                hash = (String) value;
            }

            newMap.put(new ModEntry(modid, hash), getChildren(value));
        });

        return newMap;
    }


    private static LinkedHashMap<ModEntry, Object> getChildren(Object object) {
        LinkedHashMap<ModEntry, Object> result = new LinkedHashMap<>();
        if (!(object instanceof Map<?,?>)) return result;

        Map<String, Object> children = (Map<String, Object>) object;

        children.forEach((key, value) -> {

        });

        /*object.getContainedMods().forEach(container -> {
            if (!container.getContainedMods().isEmpty()) {
                result.put(new ModEntry(container), new LinkedHashMap<>());
            } else {
                result.put(new ModEntry(container), List.of());
            }
        });*/

        return result;
    }
}
