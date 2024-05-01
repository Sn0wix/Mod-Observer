package net.sn0wix_.modObserverPlugin.players;

import net.sn0wix_.modObserverPlugin.networking.PacketHandler;
import org.bukkit.command.CommandSender;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class WaitingForResponsePlayers {
    private static int responseDelayInSec = 3;
    private static final ArrayList<WaitingForResponsePlayer> PLAYERS = new ArrayList<>();

    public static void addPlayer(WaitingForResponsePlayer player) {
        PLAYERS.add(player);
    }

    public static boolean isEmpty() {
        return PLAYERS.isEmpty();
    }

    public static boolean containsPlayer(String playerName) {
        AtomicBoolean bl = new AtomicBoolean(false);
        PLAYERS.forEach(incomingPlayer -> {
            if (incomingPlayer.getName().equals(playerName)) {
                bl.set(true);
            }
        });

        return bl.get();
    }

    public static void handlePacket(String playerName, String[] modids) {
        PLAYERS.forEach(player -> {
            if (player.getName().equals(playerName)) {
                player.handlePacket(modids);
            }
        });
    }

    public static void removePlayer(String playerName) {
        PLAYERS.removeIf(player -> player.getName().equals(playerName));
    }

    public static ArrayList<WaitingForResponsePlayer> checkForTimedOuts() {
        ArrayList<WaitingForResponsePlayer> players = new ArrayList<>();
        PLAYERS.forEach(player -> {
            if ((Instant.now().getEpochSecond() - player.requestSentAt()) > getResponseDelayInSec()) {
                players.add(player);
            }
        });
        PLAYERS.removeAll(players);
        return players;
    }

    public static int getResponseDelayInSec() {
        return responseDelayInSec;
    }

    public static void setResponseDelayInSec(int responseDelayInSec) {
        WaitingForResponsePlayers.responseDelayInSec = responseDelayInSec;
    }

    public static class WaitingForResponsePlayer {
        private final String name;
        private final long requestSentAt;
        private final PacketHandler.ResponseHandler handler;
        private final CommandSender sender;

        public WaitingForResponsePlayer(String name, CommandSender sender, long requestSentAt, PacketHandler.ResponseHandler handler) {
            this.name = name;
            this.requestSentAt = requestSentAt;
            this.handler = handler;
            this.sender = sender;
        }

        public WaitingForResponsePlayer(String name, CommandSender sender, PacketHandler.ResponseHandler handler) {
            this.name = name;
            this.requestSentAt = Instant.now().getEpochSecond();
            this.handler = handler;
            this.sender = sender;
        }

        public CommandSender getSender() {
            return sender;
        }

        public String getName() {
            return name;
        }

        public long requestSentAt() {
            return requestSentAt;
        }

        public void handlePacket(String[] modis) {
            handler.execute(modis);
        }
    }
}
