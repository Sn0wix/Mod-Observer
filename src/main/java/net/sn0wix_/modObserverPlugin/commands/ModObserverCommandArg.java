package net.sn0wix_.modObserverPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ModObserverCommandArg {
    private final String command;
    private final List<ModObserverCommandArg> subCommands;
    private final ModObserverCommandExecutor executor;

    public ModObserverCommandArg(String command, List<ModObserverCommandArg> subCommands, ModObserverCommandExecutor executor) {
        this.command = command;
        this.subCommands = subCommands;
        this.executor = executor;
    }

    public ModObserverCommandArg(String command, ModObserverCommandExecutor executor) {
        this.command = command;
        this.executor = executor;
        subCommands = List.of();
    }

    public String getCommand() {
        return command;
    }

    public List<ModObserverCommandArg> getSubCommands() {
        return subCommands;
    }

    public void execute(CommandSender sender, Command command, String label, String[] args) {
        executor.execute(sender, command, label, args);
    }

    @FunctionalInterface
    public interface ModObserverCommandExecutor {
        void execute(CommandSender sender, Command command, String label, String[] args);
    }
}
