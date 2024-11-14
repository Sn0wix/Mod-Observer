package net.sn0wix_.modObserverPlugin;

import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.players.IncomingPlayers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Util {
    public static List<String> getAllOnlinePlayers() {
        ArrayList<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
        return players;
    }

    public static boolean checkIfOnline(String playername, CommandSender messenger) {
        if (Bukkit.getPlayerExact(playername) == null) {
            messenger.sendMessage(ChatColor.RED + playername + " is not online!");
            return false;
        }
        return true;
    }

    public static boolean checkPlayer(String playerName, String[] modids, boolean kick) {
        if (Config.IGNORED_PLAYERS.contains(playerName)) return true;
        if (modids.length == 0) return false;
        if (!getNonApprovedMods(modids).isEmpty()) {
            if (kick) {
                Objects.requireNonNull(Bukkit.getPlayerExact(playerName))
                        .kickPlayer(Config.PROHIBITED_MODS_FOUND_MESSAGE.replace("<$MODS$>", Util.getNonApprovedMods(modids).toString()));
            }
            return false;
        }

        if (!getMissingRequiredMods(modids).isEmpty()) {
            if (kick) {
                Objects.requireNonNull(Bukkit.getPlayerExact(playerName))
                        .kickPlayer(Config.REQUIRED_MODS_MESSAGE.replace("<$MODS$>", Util.getMissingRequiredMods(modids).toString()));
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

    public static void checkForSusActivity(String playerName, String[] modids) {
        if (modids.length == 0) {
            ModObserverPlugin.LOGGER.info("Suspicious activity from " + playerName + ". When asked for mods, the response was empty. Kicking the player.");
            try {
                Bukkit.getPlayerExact(playerName).kickPlayer("ModObserver didn't respond correctly, try reinstalling it.");
            } catch (NullPointerException e) {
                ModObserverPlugin.LOGGER.info("Couldn't kick " + playerName + " because the player is not online!");
            }
        }
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


    //other
    public static String getModString(List<String> modsList) {
        StringBuilder builder = new StringBuilder();

        if (modsList != null && !modsList.isEmpty()) {
            for (int i = 0; i < modsList.size(); i++) {
                builder.append(modsList.get(i)).append(i != modsList.size() - 1 ? ", " : "");
            }
        }

        return builder.toString();
    }
}
