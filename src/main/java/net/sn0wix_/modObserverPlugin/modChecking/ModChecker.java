package net.sn0wix_.modObserverPlugin.modChecking;

import io.papermc.paper.connection.PlayerConnection;
import net.kyori.adventure.text.Component;
import net.sn0wix_.modObserverPlugin.Util;
import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.config.JsonLoader;
import net.sn0wix_.modObserverPlugin.utils.Connections;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModChecker {
    private static final Map<ModEntry, Object> whitelist = toModEntryMap(JsonLoader.getWhitelistMap());
    private static final Map<ModEntry, Object> blacklist = toModEntryMap(JsonLoader.getBlacklistMap());
    private static final Map<ModEntry, Object> required = toModEntryMap(JsonLoader.getRequiredMap());

    public static boolean handle(String content, PlayerConnection connection) {
        //Let the players join if the plugin is loaded with empty json configs
        if (required.isEmpty() && (Config.getMode().equals(Config.Mode.WHITELIST) && whitelist.isEmpty() || Config.getMode().equals(Config.Mode.BLACKLIST) && blacklist.isEmpty())) {
            Connections.get(connection).setOnJoin(player -> {
                if (player.isOp()) {
                    player.sendMessage(Component.text("<ModObserver> Mod configuration files are empty!"));
                }
            });
            return true;
        }

        Map<ModEntry, Object> clientMods = toModEntryMap(JsonLoader.loadJsonFromString(content));
        Map<IllegalStates, List<String>> illegalStatesMap = IllegalStates.getMap();

        //Required mods
        //If this is not here, it throws UnsupportedOperationException
        Map<ModEntry, Object> requiredModifiable = new HashMap<>(required);

        requiredModifiable.forEach((requiredMod, nested) -> {
            if (!Util.containsEntry(clientMods, requiredMod)) {
                illegalStatesMap.get(IllegalStates.REQUIRED).add(requiredMod.getId());
            } else if (Config.checkNestedMods() && !checkNested((Map<ModEntry, Object>) nested, (Map<ModEntry, Object>) Util.getValue(clientMods, requiredMod))) {
                illegalStatesMap.get(IllegalStates.BAD_CHILDREN).add(requiredMod.getId());
            }
        });


        //Final check
        AtomicBoolean passed = new AtomicBoolean(true);
        illegalStatesMap.forEach((key, value) -> {
            if (passed.get() && !value.isEmpty()) {
                passed.set(false);
            }
        });

        return passed.get();
    }

    private static boolean checkNested(Map<ModEntry, Object> origin, Map<ModEntry, Object> nested) {
        // Check if both maps are null or empty
        if (origin == null || nested == null) {
            return origin == nested; // Both should be null to be considered the same
        }

        // Check if the sizes of the maps are the same
        if (origin.size() != nested.size()) {
            return false;
        }

        // Iterate through the entries of the origin map
        for (Map.Entry<ModEntry, Object> entry : origin.entrySet()) {
            ModEntry key = entry.getKey();
            Object originValue = entry.getValue();
            Object nestedValue = Util.getValue(nested, key);

            // If the nested value is null or not the same type, return false
            if (nestedValue == null) {
                return false;
            }

            // If both values are maps, check them recursively
            if (originValue instanceof Map && nestedValue instanceof Map) {
                if (!checkNested((Map<ModEntry, Object>) originValue, (Map<ModEntry, Object>) nestedValue)) {
                    return false;
                }
            } else {
                // If they are not maps, check if they are equal
                if (!Objects.equals(originValue, nestedValue)) {
                    return false;
                }
            }
        }

        // If all checks passed, the maps are considered the same
        return true;
    }

    public static boolean isFabricApi(String modid) {
        if (List.of("mixinextras", "minecraft", "fabric-api", "fabric-api-base", "fabricloader", "java", "fabric-renderer-indigo").contains(modid)) {
            return true;
        }

        //fabric-[something]-api-v[number]
        try {
            Integer.parseInt(String.valueOf(modid.charAt(modid.length() - 1)));
        } catch (NumberFormatException ignored) {
            //last char is not a number
            return false;
        }

        return modid.startsWith("fabric-") && modid.lastIndexOf("-api-v") == modid.length() - 7;
    }


    public static Map<ModEntry, Object> toModEntryMap(Map<String, Object> map) {
        Map<ModEntry, Object> newMap = new ConcurrentHashMap<>(map.size());

        map.forEach((key, value) -> {
            String modid = key.split(" ")[0];
            String hash;

            if (modid.contains(" ")) {
                try {
                    hash = key.split(" ")[1];
                } catch (IndexOutOfBoundsException ignored) {
                    hash = (String) value;
                }
            } else {
                hash = "";
            }


            newMap.put(new ModEntry(modid, hash), getChildren(value));
        });

        return newMap;
    }


    private static LinkedHashMap<ModEntry, Object> getChildren(Object object) {
        LinkedHashMap<ModEntry, Object> result = new LinkedHashMap<>();
        if (!(object instanceof Map<?, ?>)) return result;

        Map<String, Object> children = (Map<String, Object>) object;

        children.forEach((key, value) -> {
            if (value instanceof Map<?, ?> map && !map.isEmpty()) {
                result.put(new ModEntry(key), getChildren(value));
            } else {
                result.put(new ModEntry(key), new LinkedHashMap<>());
            }
        });

        return result;
    }
}
