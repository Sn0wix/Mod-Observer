package net.sn0wix_.modObserverPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ConfigurationCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            ModObserverCommandArgs.HELP.execute(commandSender, command, label, args);
            return true;
        }

        AtomicBoolean foundArg = new AtomicBoolean(false);
        ModObserverCommandArgs.getRegisteredCommands().forEach(arg -> {
            if (!arg.getCommand().equals(args[0])) return;
            foundArg.set(true);

            AtomicReference<ModObserverCommandArg> currentSubArg = new AtomicReference<>(arg);
            for (int i = 0; i < args.length; i++) {
                if (currentSubArg.get().getSubCommands().isEmpty()) {
                    currentSubArg.get().execute(commandSender, command, label, args);
                    //breaking in case there are any other arguments after the last sub command
                    break;
                } else {
                    //try, in case sender messed something up
                    try {
                        int finalI = i + 1;
                        currentSubArg.get().getSubCommands().forEach(subArg -> {
                            if (subArg.getCommand().equals(args[finalI])) {
                                currentSubArg.set(subArg);
                            }
                        });
                    } catch (IndexOutOfBoundsException e) {
                        //yep, sender messed something up
                        currentSubArg.get().execute(commandSender, command, label, args);
                    }
                }
            }
        });

        return foundArg.get();
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        ArrayList<String> subCommandsStrings = new ArrayList<>();
        AtomicReference<ModObserverCommandArg> lastArg = new AtomicReference<>(null);

        for (int i = 0; i < args.length; i++) {
            String currentArgS = args[i];

            if (lastArg.get() == null) {
                ModObserverCommandArgs.getRegisteredCommands().forEach(registeredCommand -> {
                    if (currentArgS.equals(registeredCommand.getCommand())) {
                        lastArg.set(registeredCommand);
                    }
                });

                if (lastArg.get() == null) {
                    ModObserverCommandArgs.getRegisteredCommands().forEach(registeredCommand -> subCommandsStrings.add(registeredCommand.getCommand()));
                }
            } else {
                if (!lastArg.get().getSubCommands().isEmpty()) {
                    ArrayList<String> lastArgSubCommands = new ArrayList<>();
                    lastArg.get().getSubCommands().forEach(subCmd -> lastArgSubCommands.add(subCmd.getCommand()));

                    try {
                        if (lastArgSubCommands.contains(args[i])) {
                            int finalI = i;
                            lastArg.get().getSubCommands().forEach(subCommand -> {
                                if (subCommand.getCommand().equals(args[finalI])) {
                                    lastArg.set(subCommand);
                                }
                            });
                        } else {
                            if (args.length <= i + 1) {
                                lastArg.get().getSubCommands().forEach(subCommand -> subCommandsStrings.add(subCommand.getCommand()));
                            }

                            break;
                        }
                    } catch (IndexOutOfBoundsException ignored) {}
                }
            }
        }

        return subCommandsStrings;
    }
}
