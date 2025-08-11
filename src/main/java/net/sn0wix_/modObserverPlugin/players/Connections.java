package net.sn0wix_.modObserverPlugin.players;

import com.google.common.collect.ImmutableList;
import io.papermc.paper.connection.PlayerConnection;
import net.kyori.adventure.text.Component;
import net.sn0wix_.modObserverPlugin.ModObserverPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public class Connections {
    /**
     * All the players represented by their client ip and port that have not yet joined the game and still are in process of mod checking.
     */
    private static final ArrayList<Connection> CONNECTIONS = new ArrayList<>();

    public static void update() {
        ImmutableList.copyOf(ModObserverPlugin.getInstance().getServer().getOnlinePlayers()).forEach(onlinePlayer ->
                CONNECTIONS.removeIf(connection -> connection.equals(onlinePlayer.getConnection())));
    }

    public static boolean contains(PlayerConnection connection) {
        AtomicBoolean bl = new AtomicBoolean(false);
        CONNECTIONS.forEach(cn -> {
            if (cn.equals(connection)) {
                bl.set(true);
            }
        });

        return bl.get();
    }

    public static boolean containsAndAdd(PlayerConnection connection) {
        boolean bl = contains(connection);
        if (!bl) add(connection);
        return bl;
    }

    public static void remove(PlayerConnection connection) {
        CONNECTIONS.removeIf(cn -> cn.equals(connection));
    }

    public static void add(PlayerConnection connection) {
        CONNECTIONS.add(new Connection(connection));
    }

    public static Connection get(PlayerConnection connection) {
        Iterator<Connection> iterator = CONNECTIONS.stream().iterator();

        while (iterator.hasNext()) {
            Connection cn = iterator.next();

            if (cn.equals(connection)) {
                return cn;
            }
        }

        return null;
    }

    public static void approve(PlayerConnection connection) {
        get(connection).approve();
    }

    public static String getIpPort(PlayerConnection connection) {
        return connection.getClientAddress().getHostString() + ":" + connection.getClientAddress().getPort();
    }

    public static void setSentPacket(PlayerConnection connection) {
        get(connection).setSentPacket();
    }

/*
    public static void setApproved(String playerName) {
        PLAYERS.forEach(incomingPlayer -> {
            if (incomingPlayer.getName().equals(playerName)) {
                incomingPlayer.setApproved(true);
            }
        });
    }

    public static void setHasSendPacket(String playerName) {
        PLAYERS.forEach(incomingPlayer -> {
            if (incomingPlayer.getName().equals(playerName)) {
                incomingPlayer.setHasSendPacket(true);
            }
        });
    }

    public static boolean hasSendPacket(String playerName) {
        AtomicBoolean bl = new AtomicBoolean(false);
        PLAYERS.forEach(incomingPlayer -> {
            if (incomingPlayer.getName().equals(playerName) && incomingPlayer.hasSendPacket()) {
                bl.set(true);
            }
        });

        return bl.get();
    }

    public static boolean isApproved(String playerName) {
        AtomicBoolean bl = new AtomicBoolean(false);
        PLAYERS.forEach(incomingPlayer -> {
            if (incomingPlayer.getName().equals(playerName) && incomingPlayer.isApproved()) {
                bl.set(true);
            }
        });

        return bl.get();
    }

    public static String getMissingRequiredMods(String playerName) {
        AtomicReference<List<String>> modsList = new AtomicReference<>(new ArrayList<>());
        PLAYERS.forEach(incomingPlayer -> {
            if (incomingPlayer.getName().equals(playerName)) {
                modsList.set(incomingPlayer.getNonApprovedMods());
            }
        });

        return Util.getModString(modsList.get());
    }

    public static void addMissingRequiredMod(String playerName, String modid) {
        PLAYERS.forEach(incomingPlayer -> {
            if (incomingPlayer.getName().equals(playerName)) {
                incomingPlayer.addNonApprovedMod(modid);
            }
        });
    }

    public static String getNonApprovedMods(String playerName) {
        AtomicReference<List<String>> modsList = new AtomicReference<>();
        PLAYERS.forEach(incomingPlayer -> {
            if (incomingPlayer.getName().equals(playerName)) {
                modsList.set(incomingPlayer.getMissingRequiredMods());
            }
        });

        return Util.getModString(modsList.get());
    }

    public static void addNonApprovedMod(String playerName, String modid) {
        PLAYERS.forEach(incomingPlayer -> {
            if (incomingPlayer.getName().equals(playerName)) {
                incomingPlayer.addMissingRequiredMod(modid);
            }
        });
    }*/



    public static class Connection {
        private final String ipPort;
        private boolean isApproved = false;
        private boolean hasSentPacket = false;

        public Connection(PlayerConnection connection) {
            this.ipPort = Connections.getIpPort(connection);
        }

        public String getIpPort() {
            return ipPort;
        }

        public void setSentPacket() {
            hasSentPacket = true;
        }

        public boolean hasSentPacket() {
            return hasSentPacket;
        }

        public boolean isApproved() {
            return isApproved;
        }

        public void approve() {
            isApproved = true;
        }

        public boolean equals(PlayerConnection connection) {
            return ipPort.equals(Connections.getIpPort(connection));
        }

        public boolean equals(String ipPort) {
            return ipPort.equals(getIpPort());
        }

        public Component getKickMessage() {
            return Component.empty();
        }
    }
}
