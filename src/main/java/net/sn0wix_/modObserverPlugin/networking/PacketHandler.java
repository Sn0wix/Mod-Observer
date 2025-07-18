package net.sn0wix_.modObserverPlugin.networking;

import net.sn0wix_.modObserverPlugin.ModObserverPlugin;
import net.sn0wix_.modObserverPlugin.Util;
import net.sn0wix_.modObserverPlugin.players.IncomingPlayers;
import net.sn0wix_.modObserverPlugin.players.WaitingForResponsePlayers;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class PacketHandler {
    public static final String MODS_FOR_APPROVAL_CHANNEL = ModObserverPlugin.MOD_ID + ":mods_for_approval";

    public static void send(Plugin plugin, Player player, byte[] byteArray) {
        player.sendPluginMessage(plugin, MODS_FOR_APPROVAL_CHANNEL, byteArray);
    }


    public static void receive(String channel, Player player, byte[] payload) {
        if (!channel.equals(MODS_FOR_APPROVAL_CHANNEL)) return;
        if (Util.checkForSusActivity(player.getName(), payload))return;

        byte[] hash = Arrays.copyOfRange(payload, 0, 32);
        byte[] encryptedContent = Arrays.copyOfRange(payload, 32, payload.length);

        String concatenatedString;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            String playerName = player.getName();

            String key = String.format("%-32s", playerName).substring(0, 32); // padding/truncating to 32 chars
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decryptedData = cipher.doFinal(encryptedContent);
            byte[] calculatedHash = MessageDigest.getInstance("SHA-256").digest(decryptedData);

            if (!Arrays.equals(calculatedHash, hash)) {
                ModObserverPlugin.LOGGER.warning("Hash mismatch from " + player.getName() + ". Discarding packet.");
                return;
            }

            if (!Arrays.equals(MessageDigest.getInstance("SHA-256").digest(decryptedData), hash)) {
                ModObserverPlugin.LOGGER.warning("Packet hash mismatch! Packet hash from" + player.getName() + " does not match the expected value. Discarding the packet.");
                return;
            }

            concatenatedString = new String(decryptedData);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            ModObserverPlugin.LOGGER.severe("Wrong data padding in packet sent by " + player.getName());
            throw new RuntimeException(e);
        }


        String delimiter = ",";
        String[] modids = concatenatedString.split(delimiter);

        if (Util.checkForSusActivity(player.getName(), modids.length == 0 ? new byte[1] : new byte[]{1}))return;

        IncomingPlayers.setHasSendPacket(player.getName());

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

            if (notApprovedMods.isEmpty()) {
                hasOnlyAllowedMods = true;
            } else {
                notApprovedMods.forEach(modid -> IncomingPlayers.addNonApprovedMod(player.getName(), modid));
                shouldBeKicked = true;
            }

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
