package net.sn0wix_.modObserverPlugin;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static List<String> getAllOnlinePlayers() {
        ArrayList<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
        return players;
    }
}
