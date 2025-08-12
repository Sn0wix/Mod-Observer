package net.sn0wix_.modObserverPlugin.modChecking;

import net.kyori.adventure.text.Component;
import net.sn0wix_.modObserverPlugin.ModObserver;
import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.config.JsonLoader;

import java.util.*;
import java.util.stream.Collectors;

public enum IllegalStates {
    INCOMPATIBLE,
    REQUIRED,
    HASH_MISMATCH,
    BAD_CHILDREN;

    public static final String IDENTIFIER = "$" + ModObserver.MOD_ID + "$";

    public static Component createKickMessage(Map<IllegalStates, List<String>> illegalStatesMap) {
        StringBuilder data = new StringBuilder(IDENTIFIER);
        Map<IllegalStates, List<String>> filteredMap = illegalStatesMap.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        data.append(JsonLoader.getGson().toJson(filteredMap));
        return Config.getModObserverKickMessage().append(Component.text(data.toString()));
    }

    public static Map<IllegalStates, List<String>> getMap() {
        Map<IllegalStates, List<String>> illegalStatesMap = new HashMap<>();

        for (int i = 0; i < IllegalStates.values().length; i++) {
            illegalStatesMap.put(IllegalStates.values()[i], new ArrayList<>());
        }

        return illegalStatesMap;
    }
}
