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

        ModObserverCommandArgs.COMMAND_ARGS.forEach(arg -> {
            if (!arg.getCommand().equals(args[0])) return;


        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        ArrayList<String> subCommands = new ArrayList<>();
        int depth = args.length - 1;

        if (depth > 0) {
            ModObserverCommandArgs.COMMAND_ARGS.forEach(arg -> {
                if (!arg.getCommand().equals(args[0])) return;

                AtomicReference<ModObserverCommandArg> currentSubArg = new AtomicReference<>(arg);

                for (int i = 0; i < depth; i++) {
                    if (i == depth - 1) {
                        currentSubArg.get().getSubCommands().forEach(subCommand -> {
                            subCommands.add(subCommand.getCommand());
                        });
                    }

                    if (!currentSubArg.get().getSubCommands().isEmpty()) {
                        int finalI = i;
                        currentSubArg.get().getSubCommands().forEach(subCommand -> {
                            if (subCommand.getCommand().equals(args[finalI])) {
                                currentSubArg.set(subCommand);
                            }
                        });
                    }
                }

            });
        } else {
            ModObserverCommandArgs.COMMAND_ARGS.forEach(arg -> {
                subCommands.add(arg.getCommand());
            });
        }

        return subCommands.isEmpty() ? null : subCommands;
    }

    /*AtomicReference<ModObserverCommandArg> currentSubArg = new AtomicReference<>(arg);
            for (int currentDepth = 0; currentDepth < args.length; currentDepth++) {
        System.out.println("inside for, depth " + currentDepth);
        System.out.println("Current subArg" + currentSubArg.get().getCommand());

        //Command listing
        if (!currentSubArg.get().getSubCommands().isEmpty()) {
            currentSubArg.get().getSubCommands().forEach(subCommand -> {
                subCommands.add(subCommand.getCommand());
            });
        } else {
            //Next subCommand setting
            int finalCurrentDepth = currentDepth;
            arg.getSubCommands().forEach(subCommand -> {
                if (subCommand.getCommand().equals(args[finalCurrentDepth])) {
                    currentSubArg.set(subCommand);
                }
            });
        }
    }*/
}
