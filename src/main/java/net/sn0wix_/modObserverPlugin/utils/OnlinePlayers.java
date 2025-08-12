package net.sn0wix_.modObserverPlugin.utils;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OnlinePlayers {
    private static final Map<String, String> PLAYERS = new ConcurrentHashMap<>();

    public static void remove(Player player) {
        PLAYERS.remove(player.getName());
    }

    public static void add(Player player, String rawPacket) {
        PLAYERS.put(player.getName(), rawPacket);
    }

    public static String getRawPacket(Player player) {
        return PLAYERS.get(player.getName());
    }
}
