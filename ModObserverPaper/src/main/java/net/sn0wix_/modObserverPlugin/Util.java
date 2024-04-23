package net.sn0wix_.modObserverPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.networking.PacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Util {
    //Players
    public static HashMap<String, PacketHandler.ResponseHandler> PLAYERS_WAITING_FOR_RESPONSE = new HashMap<>();


    public static List<String> getAllOnlinePlayers() {
        ArrayList<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
        return players;
    }

    public static boolean checkPlayer(Player player) {
        boolean bl = true;
        if (!IncomingPlayers.isApproved(player.getName())) {
            if (!IncomingPlayers.hasSendPacket(player.getName())) {
                player.kick(parseLinks(Config.MOD_OBSERVER_REQUIRED_MESSAGE));
                Bukkit.getOnlinePlayers().forEach(player1 -> player1.sendMessage(Config.MOD_OBSERVER_REQUIRED_MESSAGE));
                bl = false;
            } else if (!IncomingPlayers.getNonApprovedMods(player.getName()).isEmpty()) {
                player.kick(parseLinks(Config.PROHIBITED_MODS_FOUND_MESSAGE.replace("<$MODS$>", IncomingPlayers.getNonApprovedMods(player.getName()))));
                bl = false;
            } else if (!IncomingPlayers.getMissingRequiredMods(player.getName()).isEmpty()) {
                player.kick(parseLinks(Config.REQUIRED_MODS_MESSAGE.replace("<$MODS$>", IncomingPlayers.getMissingRequiredMods(player.getName()))));
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
        ArrayList<String> missingRequiredMods = new ArrayList<>(List.copyOf(Config.REQUIRED_MODS));
        missingRequiredMods.removeAll(List.of(modids));
        return missingRequiredMods;
    }


    //Misc
    public static TextComponent parseLinks(String message) {
        TextComponent component = Component.empty();

        //check if the string contains links
        if (message.contains("](")) {
            StringBuilder text = new StringBuilder();
            StringBuilder link = new StringBuilder();
            boolean hasText = false;
            boolean hasLink = false;
            boolean shouldHaveLink = false;

            for (int i = 0; i < message.length(); i++) {
                char c = message.charAt(i);

                if (c == '[') {
                    hasText = true;
                    continue;
                }
                if (c == ']' && hasText) {
                    hasText = false;
                    shouldHaveLink = true;
                    continue;
                }

                if (shouldHaveLink && c == '(') {
                    hasLink = true;
                    shouldHaveLink = false;
                    continue;
                } else if (shouldHaveLink) {
                    text = new StringBuilder();
                    shouldHaveLink = false;
                    continue;
                }

                if (hasLink && c == ')') {
                    //HERE
                    component.append(Component.text(text.toString()).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, link.toString())));

                    hasLink = false;
                    link = new StringBuilder();
                    text = new StringBuilder();
                    continue;
                }

                if (hasText) {
                    text.append(c);
                    continue;
                }

                if (hasLink) {
                    link.append(c);
                    continue;
                }
                component.append(Component.text(c));
            }
        } else {
            return component.append(Component.text(message));
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(component);
        });

        return component;
    }
}
