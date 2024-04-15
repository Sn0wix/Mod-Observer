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

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            ModObserverCommandArgs.HELP.execute(commandSender, command, label, args);
            return true;
        }
        System.out.println("_______________________________________________________________________");

        AtomicBoolean foundArg = new AtomicBoolean(false);
        ModObserverCommandArgs.COMMAND_ARGS.forEach(arg -> {
            if (!arg.getCommand().equals(args[0])) return;
            System.out.println("Found arg: " + arg.getCommand());
            foundArg.set(true);

            AtomicReference<ModObserverCommandArg> currentSubArg = new AtomicReference<>(arg);
            System.out.println("setting current subArg");
            for (int i = 0; i < args.length; i++) {
                if (currentSubArg.get().getSubCommands().isEmpty()) {
                    System.out.println("Sub arg " + currentSubArg.get().getCommand() + " is empty, EXECUTING");
                    currentSubArg.get().execute(commandSender, command, label, args);
                    //breaking in case there are any other arguments after the last sub command
                    break;
                } else {
                    //try, in case sender messed something up
                    try {
                        System.out.println("Sub arg " + currentSubArg.get().getCommand() + " is not empty, GETTING next subArg.");
                        int finalI = i + 1;
                        currentSubArg.get().getSubCommands().forEach(subArg -> {
                            System.out.println("Testing subArg " + subArg.getCommand() + " with " + args[finalI]);
                            if (subArg.getCommand().equals(args[finalI])) {
                                System.out.println("SubArg PASSED");
                                currentSubArg.set(subArg);
                            }
                        });
                    } catch (IndexOutOfBoundsException e) {
                        //yep, sender messed something up
                        System.out.println("Was not successful, executing usage for " + currentSubArg.get().getCommand());
                        currentSubArg.get().execute(commandSender, command, label, args);
                    }
                }
            }
        });

        return foundArg.get();
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        ArrayList<String> subCommands = new ArrayList<>();
        //fix
        ModObserverCommandArgs.COMMAND_ARGS.forEach(arg -> {
            if (!arg.getCommand().equals(args[0])) return;
            AtomicReference<ModObserverCommandArg> currentSubArg = new AtomicReference<>(arg);

            for (int i = 0; i < args.length; i++) {
                int finalI = i;
                AtomicBoolean wasSuccessful = new AtomicBoolean(false);
                currentSubArg.get().getSubCommands().forEach(subCommand -> {
                    if (args[finalI].equals(subCommand.getCommand())) {
                        currentSubArg.set(subCommand);
                        wasSuccessful.set(true);
                    }
                });

                if (!wasSuccessful.get()) {
                    currentSubArg.get().getSubCommands().forEach(subCommand -> {
                        subCommands.add(subCommand.getCommand());
                    });
                }
            }
        });

        return subCommands.isEmpty() ? null : subCommands;
    }
}
