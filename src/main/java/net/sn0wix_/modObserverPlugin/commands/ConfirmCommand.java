package net.sn0wix_.modObserverPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;

public class ConfirmCommand implements CommandExecutor {
    private static final ArrayList<PendingConfirmPlayers> CONFIRM_COMMAND_PLAYERS = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        CONFIRM_COMMAND_PLAYERS.forEach(pendingConfirmPlayer -> {
            if (pendingConfirmPlayer.getPlayerName().equals(commandSender.getName())) {
                if (pendingConfirmPlayer.checkForValidResponseTime()) {
                    pendingConfirmPlayer.execute(commandSender, command, s, strings);
                } else {
                    commandSender.sendMessage("You don't have anything to confirm.");
                }
            }
        });

        Iterator<PendingConfirmPlayers> iterator = CONFIRM_COMMAND_PLAYERS.stream().iterator();

        while (iterator.hasNext()) {
            if (!iterator.next().checkForValidResponseTime()) {
                iterator.remove();
            }
        }

        return true;
    }


    public static void addPlayerToList(String name, int timeToReact, ModObserverCommandArg.ModObserverCommandExecutor executor) {
        CONFIRM_COMMAND_PLAYERS.add(new PendingConfirmPlayers(name, Instant.now().getEpochSecond(), timeToReact, executor));
    }

    public static void removePlayer(String name) {
        CONFIRM_COMMAND_PLAYERS.removeIf(pendingConfirmPlayer -> pendingConfirmPlayer.getPlayerName().equals(name));
    }

    public static class PendingConfirmPlayers {
        private final long timeOfParentExecution;
        private final int timeToReact;
        private final String playerName;
        private final ModObserverCommandArg.ModObserverCommandExecutor executor;

        public PendingConfirmPlayers(String playerName, long timeOfParentExecution, int timeToReact, ModObserverCommandArg.ModObserverCommandExecutor executor) {
            this.timeOfParentExecution = timeOfParentExecution;
            this.timeToReact = timeToReact;
            this.playerName = playerName;
            this.executor = executor;
        }

        public boolean checkForValidResponseTime() {
            return Instant.now().getEpochSecond() - timeToReact <= timeOfParentExecution;
        }

        public void execute(CommandSender sender, Command command, String label, String[] args) {
            executor.execute(sender, command, label, args);
        }

        public String getPlayerName() {
            return playerName;
        }
    }
}
