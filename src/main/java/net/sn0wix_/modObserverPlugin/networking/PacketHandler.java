package net.sn0wix_.modObserverPlugin.networking;

import net.sn0wix_.modObserverPlugin.IncomingPlayers;
import net.sn0wix_.modObserverPlugin.ModObserverPlugin;
import net.sn0wix_.modObserverPlugin.Util;
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

        //This response is not from joining player
        if (Util.PLAYERS_WAITING_FOR_RESPONSE.containsKey(player.getName())) {
            Util.PLAYERS_WAITING_FOR_RESPONSE.get(player.getName()).execute(modids);
            return;
        }

        ArrayList<String> notApprovedMods = Util.getNonApprovedMods(modids);
        ArrayList<String> missingRequiredMods = Util.getMissingRequiredMods(modids);

        boolean shouldBeKicked = false;

        //checking for non-approved mods
        if (notApprovedMods.isEmpty()) {
            IncomingPlayers.setApproved(player.getName());
        } else {
            notApprovedMods.forEach(modid -> IncomingPlayers.addNonApprovedMod(player.getName(), modid));
            shouldBeKicked = true;
        }

        //checking for missing required mods
        if (!missingRequiredMods.isEmpty()) {
            missingRequiredMods.forEach(modid -> IncomingPlayers.addMissingRequiredMod(player.getName(), modid));
            shouldBeKicked = true;
        }

        if (shouldBeKicked) {
            Util.checkPlayer(player);
        }
    }

    @FunctionalInterface
    public interface ResponseHandler {
        void execute(String[] modids);
    }
}
