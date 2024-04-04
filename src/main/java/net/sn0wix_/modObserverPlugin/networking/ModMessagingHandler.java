package net.sn0wix_.modObserverPlugin.networking;

import net.sn0wix_.modObserverPlugin.IncomingPlayers;
import net.sn0wix_.modObserverPlugin.ModObserverPlugin;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ModMessagingHandler {
    public static final String MODS_FOR_APPROVAL_CHANNEL = ModObserverPlugin.MOD_ID + ":mods_for_approval";

    public static void receive(String channel, Player player, byte[] bytes) {
        if (!channel.equals(MODS_FOR_APPROVAL_CHANNEL)) return;
        IncomingPlayers.setHasSendPacket(player.getName());

        //decoding the packet
        String concatenatedString = new String(bytes);

        String delimiter = ",";
        String[] modids = concatenatedString.split(delimiter);

        ArrayList<String> notApprovedMods = ModObserverPlugin.getNonApprovedMods(modids);

        //checking for non-approved mods
        if (notApprovedMods.isEmpty()) {
            IncomingPlayers.setApproved(player.getName());
        } else {
            notApprovedMods.forEach(modid -> IncomingPlayers.addNonApprovedMod(player.getName(), modid));
        }
    }
}
