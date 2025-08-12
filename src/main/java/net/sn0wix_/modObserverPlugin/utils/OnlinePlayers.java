package net.sn0wix_.modObserverPlugin.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OnlinePlayers {
    private static final Map<String, String> PLAYERS = new ConcurrentHashMap<>();

    public static void remove(String playerName) {
        PLAYERS.remove(playerName);
    }

    public static void add(String playerName, String rawPacket) {
        PLAYERS.put(playerName, rawPacket);
    }

    public static String getRawPacket(String playerName) {
        return PLAYERS.get(playerName);
    }
}
