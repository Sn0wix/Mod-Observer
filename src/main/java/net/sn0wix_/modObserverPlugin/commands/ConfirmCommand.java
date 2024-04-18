package net.sn0wix_.modObserverPlugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ConfirmCommand implements CommandExecutor {
    private static final ArrayList<ConfirmCommandArg> CONFIRM_COMMAND_SENDERS = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Iterator<ConfirmCommandArg> iterator = CONFIRM_COMMAND_SENDERS.listIterator();

        if (CONFIRM_COMMAND_SENDERS.isEmpty()) {
            commandSender.sendMessage(ChatColor.RED + "There is nothing to be confirmed.");
            return true;
        }

        while (iterator.hasNext()) {
            ConfirmCommandArg confirmCommandArg = iterator.next();

            if (confirmCommandArg.getSender().equals(commandSender.getName())) {
                if (confirmCommandArg.checkForValidResponseTime()) {
                    confirmCommandArg.execute(commandSender, command, s, strings);
                } else {
                    commandSender.sendMessage(ChatColor.RED + "You confirmed the command too late.");
                }
                try {
                    iterator.remove();
                }catch (ConcurrentModificationException ignored) {}


                break;
            }

            if (!confirmCommandArg.checkForValidResponseTime()) {
                iterator.remove();
            }

            if (!iterator.hasNext()) {
                commandSender.sendMessage(ChatColor.RED + "There is nothing to be confirmed.");
                break;
            }
        }

        return true;
    }


    public static void addSenderToQueue(ConfirmCommandArg sender) {
        CONFIRM_COMMAND_SENDERS.add(sender);
    }

    public static boolean containsSender(String name) {
        AtomicBoolean bl = new AtomicBoolean(false);

        CONFIRM_COMMAND_SENDERS.forEach(confirmCommandArg -> {
            if (confirmCommandArg.getSender().equals(name)) {
                bl.set(true);
            }
        });

        return bl.get();
    }

    public static boolean isLate(String name) {
        AtomicBoolean bl = new AtomicBoolean(false);
        CONFIRM_COMMAND_SENDERS.forEach(confirmCommandArg -> {
            if (confirmCommandArg.getSender().equals(name) && !confirmCommandArg.checkForValidResponseTime()) {
                bl.set(true);
            }
        });
        return bl.get();
    }

    public static void remove(String name) {
        AtomicReference<ConfirmCommandArg> senderToRemove = new AtomicReference<>(null);
        CONFIRM_COMMAND_SENDERS.forEach(confirmCommandArg -> {
            if (confirmCommandArg.getSender().equals(name)) {
                senderToRemove.set(confirmCommandArg);
            }
        });

        if (senderToRemove.get() != null) {
            CONFIRM_COMMAND_SENDERS.remove(senderToRemove.get());
        }
    }

    public static int getRemainingTime(String name) {
        AtomicInteger i = new AtomicInteger(0);
        CONFIRM_COMMAND_SENDERS.forEach(confirmCommandArg -> {
            if (confirmCommandArg.getSender().equals(name)) {
                i.set(confirmCommandArg.getRemainingTime());
            }
        });

        return i.get();
    }
}
