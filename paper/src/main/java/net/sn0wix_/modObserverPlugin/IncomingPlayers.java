package net.sn0wix_.modObserverPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class IncomingPlayers {
    /**
     * This array list represents all the players that have not yet joined in and still are in process of mod checking.
     */
    private static final ArrayList<IncomingPlayer> PLAYERS = new ArrayList<>();

    public static boolean containsPlayer(String playerName) {
        AtomicBoolean bl = new AtomicBoolean(false);
        PLAYERS.forEach(incomingPlayer -> {
            if (incomingPlayer.getName().equals(playerName)) {
                bl.set(true);
            }
        });

        return bl.get();
    }

    public static void removePlayer(String playerName) {
        PLAYERS.removeIf(player -> player.getName().equals(playerName));
    }

    public static void addPlayer(String playerName) {
        PLAYERS.add(new IncomingPlayer(playerName));
    }

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

        return getModString(modsList.get());
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

        return getModString(modsList.get());
    }

    public static void addNonApprovedMod(String playerName, String modid) {
        PLAYERS.forEach(incomingPlayer -> {
            if (incomingPlayer.getName().equals(playerName)) {
                incomingPlayer.addMissingRequiredMod(modid);
            }
        });
    }


    private static String getModString(List<String> modsList) {
        StringBuilder builder = new StringBuilder();

        if (!modsList.isEmpty()) {
            for (int i = 0; i < modsList.size(); i++) {
                builder.append(modsList.get(i)).append(i != modsList.size() - 1 ? ", " : "");
            }
        }

        return builder.toString();
    }


    public static class IncomingPlayer {
        private final String name;
        private boolean isApproved = false;
        private boolean hasSendPacket = false;
        private final ArrayList<String> notApprovedMods = new ArrayList<>();
        private final ArrayList<String> missingRequiredMods = new ArrayList<>();

        public IncomingPlayer(String name) {
            this.name = name;
        }

        public void setHasSendPacket(boolean send) {
            hasSendPacket = send;
        }

        public boolean hasSendPacket() {
            return hasSendPacket;
        }

        public void addMissingRequiredMod(String modid) {
            missingRequiredMods.add(modid);
        }

        public List<String> getMissingRequiredMods() {
            return missingRequiredMods;
        }

        public void addNonApprovedMod(String modid) {
            notApprovedMods.add(modid);
        }

        public List<String> getNonApprovedMods() {
            return notApprovedMods;
        }

        public String getName() {
            return name;
        }

        public boolean isApproved() {
            return isApproved;
        }

        public void setApproved(boolean approved) {
            isApproved = approved;
        }
    }
}
