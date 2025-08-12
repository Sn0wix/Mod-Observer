package net.sn0wix_.modObserverPlugin.modChecking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum IllegalStates {
    INCOMPATIBLE(0),
    REQUIRED(1),
    HASH_MISMATCH(2),
    MISSING_HASH(3),
    BAD_CHILDREN(4);

    final int code;

    IllegalStates(int code) {
        this.code = code;
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
