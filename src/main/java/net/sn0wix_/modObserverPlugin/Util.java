package net.sn0wix_.modObserverPlugin;

import net.sn0wix_.modObserverPlugin.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Util {
    //Players
    public static List<String> getAllOnlinePlayers() {
        ArrayList<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
        return players;
    }

    public static boolean checkPlayer(Player player) {
        boolean bl = true;
        if (!IncomingPlayers.isApproved(player.getName())) {
            if (!IncomingPlayers.hasSendPacket(player.getName())) {
                player.kickPlayer(Config.MOD_OBSERVER_REQUIRED_MESSAGE);
                bl = false;
            } else if (!IncomingPlayers.getNonApprovedMods(player.getName()).isEmpty()) {
                player.kickPlayer(Config.PROHIBITED_MODS_FOUND_MESSAGE.replace("<$MODS$>", IncomingPlayers.getNonApprovedMods(player.getName())));
                bl = false;
            } else if (!IncomingPlayers.getMissingRequiredMods(player.getName()).isEmpty()) {
                player.kickPlayer(Config.REQUIRED_MODS_MESSAGE.replace("<$MODS$>", IncomingPlayers.getMissingRequiredMods(player.getName())));
                bl = false;
            }
        }

        return bl;
    }


    //Mod checking
    public static ArrayList<String> getNonApprovedMods(String[] modids) {
        ArrayList<String> notApprovedMods = new ArrayList<>();

        if (Config.MODE.equals(Config.Mode.WHITELIST)) {
            for (String modid : modids) {
                if (!Config.WHITELISTED_MODS.contains(modid)) {
                    notApprovedMods.add(modid);
                }
            }
        } else if (Config.MODE.equals(Config.Mode.BLACKLIST)) {
            for (String modid : modids) {
                if (Config.BLACKLISTED_MODS.contains(modid)) {
                    notApprovedMods.add(modid);
                }
            }
        }
        return notApprovedMods;
    }

    public static ArrayList<String> getMissingRequiredMods(String[] modids) {
        ArrayList<String> missingRequiredMods = new ArrayList<>();
        ArrayList<String> receivedRequiredMods = new ArrayList<>();

        if (!Config.REQUIRED_MODS.isEmpty()) {
            for (String modid : modids) {
                if (Config.REQUIRED_MODS.contains(modid)) {
                    return receivedRequiredMods;
                }
            }

            missingRequiredMods.addAll(Config.REQUIRED_MODS);
            missingRequiredMods.removeAll(receivedRequiredMods);
        }

        return missingRequiredMods;
    }
}
