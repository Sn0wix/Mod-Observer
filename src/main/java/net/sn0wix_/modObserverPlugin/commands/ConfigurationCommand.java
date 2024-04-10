package net.sn0wix_.modObserverPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ConfigurationCommand implements CommandExecutor, TabCompleter {
    public static final String COMMAND = "modObserver";
    public static final String USAGE = "modObserver usage";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            ModObserverCommandArgs.HELP.execute(commandSender, command, label, args);
            System.out.println("Help message no args");
            return false;
        }
        AtomicBoolean wasSuccessful = new AtomicBoolean(false);

        ModObserverCommandArgs.COMMAND_ARGS.forEach(arg -> {
            if (!arg.getCommand().equals(args[0])) return;
            wasSuccessful.set(true);

            AtomicReference<ModObserverCommandArg> currentSubArg = new AtomicReference<>(arg);
            for (int currentDepth = 0; currentDepth < args.length; currentDepth++) {
                //Command executing
                if (currentSubArg.get().getSubCommands().isEmpty()) {
                    currentSubArg.get().execute(commandSender, command, label, args);
                    break;
                } else {
                    //Next subCommand setting
                    try {
                        int finalCurrentDepth = currentDepth + 1;
                        arg.getSubCommands().forEach(subCommand -> {
                            if (subCommand.getCommand().equals(args[finalCurrentDepth])) {
                                currentSubArg.set(subCommand);
                            }
                        });
                    } catch (IndexOutOfBoundsException e) {
                        currentSubArg.get().execute(commandSender, command, label, args);
                        break;
                    }
                }
            }
        });

        return wasSuccessful.get();
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        ArrayList<String> subCommands = new ArrayList<>();

        ModObserverCommandArgs.COMMAND_ARGS.forEach(arg -> {
            AtomicReference<ModObserverCommandArg> currentSubArg = new AtomicReference<>(arg);
            for (int currentDepth = 0; currentDepth < args.length; currentDepth++) {
                //Command listing
                if (arg.getSubCommands().isEmpty()) {
                    currentSubArg.get().getSubCommands().forEach(subCommand -> {
                        subCommands.add(subCommand.getCommand());
                    });
                    break;
                } else {
                    //Next subCommand setting
                    int finalCurrentDepth = currentDepth;
                    arg.getSubCommands().forEach(subCommand -> {
                        if (subCommand.getCommand().equals(args[finalCurrentDepth])) {
                            currentSubArg.set(subCommand);
                        }
                    });
                }
            }
        });

        return subCommands.isEmpty() ? null : subCommands;
    }
}
