package net.sn0wix_.modObserverPlugin.networking;

import net.sn0wix_.modObserverPlugin.players.IncomingPlayers;
import net.sn0wix_.modObserverPlugin.ModObserverPlugin;
import net.sn0wix_.modObserverPlugin.Util;
import net.sn0wix_.modObserverPlugin.players.WaitingForResponsePlayers;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class PacketHandler {
    public static final String MOD_REQUEST_CHANNEL = ModObserverPlugin.MOD_ID + ":request_mods";
    public static final String MODS_FOR_APPROVAL_CHANNEL = ModObserverPlugin.MOD_ID + ":mods_for_approval";

    public static void send(Plugin plugin, Player player, byte[] byteArray) {
        player.sendPluginMessage(plugin, MOD_REQUEST_CHANNEL, byteArray);
    }


    public static void receive(String channel, Player player, byte[] bytes) {
        if (!channel.equals(MODS_FOR_APPROVAL_CHANNEL)) return;

        IncomingPlayers.setHasSendPacket(player.getName());

        //decoding the packet
        String concatenatedString = new String(bytes);

        String delimiter = ",";
        String[] modids = concatenatedString.split(delimiter);

        //Checking for possible cheaters
        if (modids.length == 0) {
            ModObserverPlugin.LOGGER.info("Suspicious activity from " + player.getName() + ". When asked for mods, the response was empty. Kicking the player.");
            player.kickPlayer("ModObserver didn't respond correctly, try reinstalling it.");
        }

        //Check, if the response is from a joining player
        if (WaitingForResponsePlayers.containsPlayer(player.getName())) {
            WaitingForResponsePlayers.handlePacket(player.getName(), modids);
            WaitingForResponsePlayers.removePlayer(player.getName());
            return;
        }

        if (IncomingPlayers.containsPlayer(player.getName())) {

            ArrayList<String> notApprovedMods = Util.getNonApprovedMods(modids);
            ArrayList<String> missingRequiredMods = Util.getMissingRequiredMods(modids);

            boolean shouldBeKicked = false;
            boolean hasOnlyAllowedMods = false;

            //checking for non-approved mods
            if (notApprovedMods.isEmpty()) {
                hasOnlyAllowedMods = true;
            } else {
                notApprovedMods.forEach(modid -> IncomingPlayers.addNonApprovedMod(player.getName(), modid));
                shouldBeKicked = true;
            }

            //checking for missing required mods
            if (!missingRequiredMods.isEmpty()) {
                missingRequiredMods.forEach(modid -> IncomingPlayers.addMissingRequiredMod(player.getName(), modid));
                shouldBeKicked = true;
            } else if (hasOnlyAllowedMods) {
                IncomingPlayers.setApproved(player.getName());
            }

            if (shouldBeKicked) {
                Util.checkIncomingPlayer(player);
            }
        }
    }

    @FunctionalInterface
    public interface ResponseHandler {
        void execute(String[] modids);
    }
}
