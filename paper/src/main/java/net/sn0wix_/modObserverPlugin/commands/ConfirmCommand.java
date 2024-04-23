package net.sn0wix_.modObserverPlugin.commands;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ConfirmCommand {
    private static final ArrayList<ConfirmCommandArg> CONFIRM_COMMAND_SENDERS = new ArrayList<>();

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
