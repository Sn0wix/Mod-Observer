package net.sn0wix_.modObserverPlugin.modChecking;

import net.kyori.adventure.text.Component;
import net.sn0wix_.modObserverPlugin.ModObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum IllegalStates {
    INCOMPATIBLE(0),
    REQUIRED(1),
    HASH_MISMATCH(2),
    BAD_CHILDREN(3);

    final int code;

    IllegalStates(int code) {
        this.code = code;
    }

    public static final String IDENTIFIER = "$" + ModObserver.MOD_ID + "$";

    public static Component createKickMessage(Map<IllegalStates, List<String>> illegalStatesMap) {
        StringBuilder message = new StringBuilder(IDENTIFIER);
        illegalStatesMap.forEach((state, modids) -> {
            if (!modids.isEmpty()) {
                message.append(state.getCode()).append(":");
                message.append(modids);
                message.append(";");
            }
        });

        return Component.text(message.toString());
    }

    public int getCode() {
        return code;
    }

    public static Map<IllegalStates, List<String>> getMap() {
        Map<IllegalStates, List<String>> illegalStatesMap = new HashMap<>();

        for (int i = 0; i < IllegalStates.values().length; i++) {
            illegalStatesMap.put(IllegalStates.values()[i], new ArrayList<>());
        }

        return illegalStatesMap;
    }
}
