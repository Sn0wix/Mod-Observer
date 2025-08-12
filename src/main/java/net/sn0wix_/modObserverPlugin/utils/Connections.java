package net.sn0wix_.modObserverPlugin.utils;

import com.google.common.collect.ImmutableList;
import io.papermc.paper.connection.PlayerConnection;
import net.kyori.adventure.text.Component;
import net.sn0wix_.modObserverPlugin.ModObserver;
import net.sn0wix_.modObserverPlugin.config.Config;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Connections {
    /**
     * All the players represented by their client ip and port that have not yet joined the game and still are in process of mod checking.
     */
    private static final List<Connection> CONNECTIONS = new CopyOnWriteArrayList<>(); //Thread safe list

    public static void update() {
        ImmutableList.copyOf(ModObserver.getInstance().getServer().getOnlinePlayers()).forEach(onlinePlayer ->
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

    public static void remove(PlayerConnection connection) {
        CONNECTIONS.removeIf(cn -> cn.equals(connection));
    }

    public static void add(PlayerConnection connection, String playerName) {
        CONNECTIONS.add(new Connection(connection, playerName));
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

    public static void approve(PlayerConnection connection, boolean bl) {
        get(connection).approve(bl);
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
        private final String playerName;
        private boolean canBeChecked = false;
        private OnJoin onJoin = null;

        public Connection(@NotNull PlayerConnection connection, @NotNull String playerName) {
            this.ipPort = Connections.getIpPort(connection);
            this.playerName = playerName;
        }

        //PlayerConnectionValidateLoginEvent gets fired twice
        public boolean canBeChecked() {
            boolean wasChecked = canBeChecked;
            canBeChecked = true;
            return wasChecked;
        }

        public void setOnJoin(OnJoin runnable) {
            onJoin = runnable;
        }

        public void onJoin(Player player) {
            if (onJoin != null) {
                onJoin.execute(player);
            }
        }

        public String getPlayerName() {
            return playerName;
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
            return isApproved || Config.getIgnoredPlayers().contains(playerName);
        }

        public void approve(boolean bl) {
            isApproved = bl;
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

        @FunctionalInterface
        public interface OnJoin {
            void execute(Player player);
        }
    }
}
