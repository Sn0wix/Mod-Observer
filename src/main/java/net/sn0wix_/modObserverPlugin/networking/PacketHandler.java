package net.sn0wix_.modObserverPlugin.networking;

import io.papermc.paper.connection.PlayerConnection;
import net.sn0wix_.modObserverPlugin.ModObserver;
import net.sn0wix_.modObserverPlugin.utils.Connections;
import net.sn0wix_.modObserverPlugin.modChecking.ModChecker;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class PacketHandler implements PluginMessageListener {
    public static final String MODS_CHANNEL = ModObserver.MOD_ID + ":mods";

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull PlayerConnection connection, byte @NotNull [] message) {
        try {
            if (!channel.equals(MODS_CHANNEL)) return;
            if (message.length == 0) {
                ModObserver.LOGGER.warning("Mod packet from " + Objects.requireNonNull(Connections.get(connection)).getPlayerName() + " is empty!");
                return;
            }

            //Packet decoding
            int compressedLength = message.length - 32;
            byte[] compressedData = new byte[compressedLength];
            byte[] hash = new byte[32];

            System.arraycopy(message, 0, compressedData, 0, compressedLength);
            System.arraycopy(message, compressedLength, hash, 0, 32);

            byte[] jsonData = decompress(compressedData);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] computedHash = digest.digest(jsonData);

            if (!Arrays.equals(hash, computedHash)) {
                throw new RuntimeException("Hash verification failed! Provided hash: " + new String(hash) + " Calculated hash: " + new String(computedHash));
            }

            //Mod checking
            Connections.setSentPacket(connection);
            ModChecker.handle(new String(jsonData) ,connection);
        } catch (Exception e) {
            ModObserver.LOGGER.severe("There was an error while decoding packet from " + connection.getClientAddress());
            ModObserver.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static byte[] decompress(byte[] data) throws IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];

        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
        } catch (DataFormatException e) {
            throw new IOException("Data format exception while decompressing", e);
        } finally {
            inflater.end();
        }
        return outputStream.toByteArray();
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        ModObserver.LOGGER.warning("Received mod packet from " + player.getName() + " without requesting it!\n" + player.getName() + " may be using cracked version of ModObserver!");
    }
}
