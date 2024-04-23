package net.sn0wix_.modObserverPlugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.time.Instant;

public class ConfirmCommandArg extends ModObserverCommandArg {
    private long timeOfParentExecution = 0;
    private final int timeToReact;
    private boolean wasExecuted = false;
    private String sender = "";
    private final String warningMessage;

    public ConfirmCommandArg(String command, int secondsToReact, String warningMessage, ModObserverCommandExecutor executor) {
        super(command, executor);
        this.timeToReact = secondsToReact;
        this.warningMessage = warningMessage;
    }

    public boolean checkForValidResponseTime() {
        return Instant.now().getEpochSecond() - timeToReact <= timeOfParentExecution;
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] argsAfterLastCommand) {
        if (!wasExecuted) {
            if (ConfirmCommand.containsSender(sender.getName())) {
                if (ConfirmCommand.isLate(sender.getName())) {
                    ConfirmCommand.remove(sender.getName());
                } else {
                    sender.sendMessage(ChatColor.RED + "You have already a command to confirm. Please wait for: " + ConfirmCommand.getRemainingTime(sender.getName()) + "s");
                    return;
                }
            }

            wasExecuted = true;
            this.sender = sender.getName();
            timeOfParentExecution = Instant.now().getEpochSecond();
            sender.sendMessage(warningMessage);
            ConfirmCommand.addSenderToQueue(this);
        } else {
            wasExecuted = false;
            super.execute(sender, command, label, argsAfterLastCommand);
            ConfirmCommand.remove(sender.getName());
        }
    }

    public String getSender() {
        return sender;
    }

    public int getRemainingTime() {
        return (int) Math.abs(Instant.now().getEpochSecond() - timeOfParentExecution - timeToReact);
    }
}
