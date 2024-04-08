package net.sn0wix_.modObserverPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class ConfigurationCommand implements CommandExecutor, TabCompleter {
    public static final String USAGE = "modObserver ";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings[0] == null) {
            commandSender.sendMessage(USAGE);
            return true;
        } else if (strings[0].equals("help")) {
            commandSender.sendMessage(USAGE);
            return true;
        }

        switch (strings[0]) {
            default:
                commandSender.sendMessage(USAGE);
                break;
            case "help":
                commandSender.sendMessage(USAGE);
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
