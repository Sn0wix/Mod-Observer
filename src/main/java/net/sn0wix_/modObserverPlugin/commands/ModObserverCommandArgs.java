package net.sn0wix_.modObserverPlugin.commands;

import java.util.ArrayList;
import java.util.List;

public class ModObserverCommandArgs {
    public static final List<ModObserverCommandArg> COMMAND_ARGS = new ArrayList<>();

    public static final ModObserverCommandArg HELP = registerCommandArg(new ModObserverCommandArg("help", ((sender, command, label, args) -> sender.sendMessage("Insert help message here."))));
    public static final ModObserverCommandArg CONFIG = registerCommandArg(new ModObserverCommandArg("config", List.of(
            new ModObserverCommandArg("reload", (sender, command, label, args) -> {
                sender.sendMessage("Config reloaded.");
            }),
            new ModObserverCommandArg("reset", (sender, command, label, args) -> {
                sender.sendMessage("Are you sure you want to reset config settings?");
            })
    ), ((sender, command, label, args) -> sender.sendMessage("Wrong usage. Use "))));


    public static ModObserverCommandArg registerCommandArg(ModObserverCommandArg arg) {
        COMMAND_ARGS.add(arg);
        return arg;
    }
}
