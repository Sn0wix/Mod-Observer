package net.sn0wix_.modObserverPlugin;

import net.sn0wix_.modObserverPlugin.config.ConfigOld;
import net.sn0wix_.modObserverPlugin.modChecking.ModEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Util {
    public static boolean containsEntry(Map<ModEntry, Object> map, ModEntry entry) {
        AtomicBoolean bl = new AtomicBoolean(false);
        map.keySet().forEach(key -> {
            if (key.equals(entry)) {
                bl.set(true);
            }
        });

        return bl.get();
    }

    public static ModEntry getEntry(Map<ModEntry, Object> map, ModEntry entry) {
        AtomicReference<ModEntry> returnValue = new AtomicReference<>();
        map.keySet().forEach(key -> {
            if (key.equals(entry)) {
                returnValue.set(key);
            }
        });

        return returnValue.get();
    }

    public static Object getValue(Map<ModEntry, Object> map, ModEntry entry) {
        AtomicReference<Object> returnValue = new AtomicReference<>();
        map.forEach((key, value) -> {
            if (key.equals(entry)) {
                returnValue.set(value);
            }
        });

        return returnValue.get();
    }

    public static boolean checkIfOnline(String playername, CommandSender messenger) {
        if (Bukkit.getPlayerExact(playername) == null) {
            messenger.sendMessage(ChatColor.RED + playername + " is not online!");
            return false;
        }
        return true;
    }

    public static boolean checkPlayer(String playerName, String[] modids, boolean kick) {
        if (ConfigOld.IGNORED_PLAYERS.contains(playerName)) return true;
        if (modids.length == 0) return false;
        if (!getNonApprovedMods(modids).isEmpty()) {
            if (kick) {
                Objects.requireNonNull(Bukkit.getPlayerExact(playerName))
                        .kickPlayer(ConfigOld.PROHIBITED_MODS_FOUND_MESSAGE.replace("<$MODS$>", Util.getNonApprovedMods(modids).toString()));
            }
            return false;
        }

        if (!getMissingRequiredMods(modids).isEmpty()) {
            if (kick) {
                Objects.requireNonNull(Bukkit.getPlayerExact(playerName))
                        .kickPlayer(ConfigOld.REQUIRED_MODS_MESSAGE.replace("<$MODS$>", Util.getMissingRequiredMods(modids).toString()));
            }
            return false;
        }

        return true;
    }

    /*public static boolean checkIncomingPlayer(Player player) {
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
    }*/

    public static boolean checkForSusActivity(String playerName, byte[] modids) {
        if (modids.length == 0) {
            ModObserver.LOGGER.info("Suspicious activity from " + playerName + ". When asked for mods, the response was empty. Kicking the player.");
            return true;
        }

        return false;
    }


    //Mod checking
    public static ArrayList<String> getNonApprovedMods(String[] modids) {
        ArrayList<String> notApprovedMods = new ArrayList<>();

        if (ConfigOld.MODE.equals(ConfigOld.Mode.WHITELIST)) {
            for (String modid : modids) {
                if (!ConfigOld.WHITELISTED_MODS.contains(modid)) {
                    notApprovedMods.add(modid);
                }
            }
        } else if (ConfigOld.MODE.equals(ConfigOld.Mode.BLACKLIST)) {
            for (String modid : modids) {
                if (ConfigOld.BLACKLISTED_MODS.contains(modid)) {
                    notApprovedMods.add(modid);
                }
            }
        }

        return notApprovedMods;
    }

    public static ArrayList<String> getMissingRequiredMods(String[] modids) {
        ArrayList<String> missingRequiredMods = new ArrayList<>(List.copyOf(ConfigOld.REQUIRED_MODS));
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
