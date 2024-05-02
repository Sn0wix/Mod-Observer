package net.sn0wix_.modObserverPlugin.players;

import net.sn0wix_.modObserverPlugin.Util;
import net.sn0wix_.modObserverPlugin.config.Config;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class OnlinePlayersToCheck {
    private static final HashMap<String, Long> PLAYERS = new HashMap<>();

    public static void addPlayer(String playerName) {
        PLAYERS.put(playerName, Instant.now().getEpochSecond());
    }
    public static void addPlayer(String playerName, long time) {
        PLAYERS.put(playerName, time);
    }

    public static boolean contains(String playerName) {
        return PLAYERS.containsKey(playerName);
    }


    public static void removePlayer(String playerName) {
        PLAYERS.remove(playerName);
    }

    public static void tick() {
        if (Config.ALLOW_VERIFICATION_TIMER && !PLAYERS.isEmpty()) {
            ArrayList<String> playersToReset = new ArrayList<>(0);
            PLAYERS.forEach((playerName, aLong) -> {
                if (Instant.now().getEpochSecond() - aLong >= Config.VERIFICATION_TIMER_DELAY) {
                    playersToReset.add(playerName);
                    WaitingForResponsePlayers.addPlayer(new WaitingForResponsePlayers.WaitingForResponsePlayer(playerName, Instant.now().getEpochSecond(), modids -> {
                        //message received
                        Util.checkPlayer(playerName, modids, true);
                    }, () -> {
                        //message not received
                        if (Bukkit.getPlayerExact(playerName) != null) {
                            Bukkit.getPlayerExact(playerName).kickPlayer(Config.MOD_OBSERVER_REQUIRED_MESSAGE);
                        } else {
                            PLAYERS.remove(playerName);
                        }
                    }));
                }
            });

            playersToReset.forEach(playerToReset -> PLAYERS.put(playerToReset, Instant.now().getEpochSecond()));
        }
    }
}
