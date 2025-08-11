package net.sn0wix_.modObserverPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

public class ModObserverCommandArg {
    private final String command;
    private final List<ModObserverCommandArg> subCommands;
    private final ModObserverCommandExecutor executor;
    private Optional<ModObserverCommandTabCompleter> tabCompleter = Optional.empty();


    public ModObserverCommandArg(String command, List<ModObserverCommandArg> subCommands, ModObserverCommandExecutor executor) {
        this.command = command;
        this.subCommands = subCommands;
        this.executor = executor;
    }

    public ModObserverCommandArg(String command, List<ModObserverCommandArg> subCommands, ModObserverCommandExecutor executor, ModObserverCommandTabCompleter tabCompleter) {
        this.command = command;
        this.executor = executor;
        this.tabCompleter = Optional.of(tabCompleter);
        this.subCommands = subCommands;
    }

    public ModObserverCommandArg(String command, ModObserverCommandExecutor executor, ModObserverCommandTabCompleter tabCompleter) {
        this.command = command;
        this.executor = executor;
        this.tabCompleter = Optional.of(tabCompleter);
        subCommands = List.of();
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

    public void execute(CommandSender sender, Command command, String label, String[] argsAfterLastCommand) {
        executor.execute(sender, command, label, argsAfterLastCommand);
    }

    public List<String> onTabCompleted(CommandSender commandSender, Command command, String label, String[] argsAfterLastCommand) {
        if (tabCompleter.isPresent()) {
            return tabCompleter.get().execute(commandSender, command, label, argsAfterLastCommand);
        }

        return List.of();
    }

    @FunctionalInterface
    public interface ModObserverCommandExecutor {
        void execute(CommandSender sender, Command command, String label, String[] args);
    }

    @FunctionalInterface
    public interface ModObserverCommandTabCompleter {
        List<String> execute(CommandSender commandSender, Command command, String label, String[] argsAfterLastCommand);
    }
}
