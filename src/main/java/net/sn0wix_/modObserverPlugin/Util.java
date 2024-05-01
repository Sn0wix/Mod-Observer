package net.sn0wix_.modObserverPlugin;

import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.players.IncomingPlayers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Util {
    //Players
    public static List<String> getAllOnlinePlayers() {
        ArrayList<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
        return players;
    }

    public static boolean checkPlayer(String playerName, String[] modids, boolean kick) {
        if (Config.IGNORED_PLAYERS.contains(playerName)) return true;
        if (!getNonApprovedMods(modids).isEmpty()) {
            if (kick) {
                Objects.requireNonNull(Bukkit.getPlayerExact(playerName))
                        .kickPlayer(Config.PROHIBITED_MODS_FOUND_MESSAGE.replace("<$MODS$>", IncomingPlayers.getNonApprovedMods(playerName)));
            }
            return false;
        }

        if (!getMissingRequiredMods(modids).isEmpty()) {
            if (kick) {
                Objects.requireNonNull(Bukkit.getPlayerExact(playerName))
                        .kickPlayer(Config.REQUIRED_MODS_MESSAGE.replace("<$MODS$>", IncomingPlayers.getMissingRequiredMods(playerName)));
            }
            return false;
        }

        return true;
    }

    public static boolean checkIncomingPlayer(Player player) {
        if (!IncomingPlayers.isApproved(player.getName())) {
            if (!IncomingPlayers.hasSendPacket(player.getName())) {
                player.kickPlayer(Config.MOD_OBSERVER_REQUIRED_MESSAGE);
                return false;
            } else if (!IncomingPlayers.getNonApprovedMods(player.getName()).isEmpty()) {
                player.kickPlayer(Config.PROHIBITED_MODS_FOUND_MESSAGE.replace("<$MODS$>", IncomingPlayers.getNonApprovedMods(player.getName())));
                return false;
            } else if (!IncomingPlayers.getMissingRequiredMods(player.getName()).isEmpty()) {
                player.kickPlayer(Config.REQUIRED_MODS_MESSAGE.replace("<$MODS$>", IncomingPlayers.getMissingRequiredMods(player.getName())));
                return false;
            }
        }

        return true;
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
        ArrayList<String> missingRequiredMods = new ArrayList<>(List.copyOf(Config.REQUIRED_MODS));
        missingRequiredMods.removeAll(List.of(modids));
        return missingRequiredMods;
    }
}
