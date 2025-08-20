package net.sn0wix_.modObserverPlugin.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.sn0wix_.modObserverPlugin.ModObserver;
import net.sn0wix_.modObserverPlugin.Util;
import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.config.JsonLoader;
import net.sn0wix_.modObserverPlugin.utils.OnlinePlayers;

import java.util.*;

public class ModObserverCommandArgs {
    private static final List<ModObserverCommandArg> REGISTERED_COMMANDS = new ArrayList<>();


    //Help
    public static final ModObserverCommandArg HELP = registerCommandArg(new ModObserverCommandArg("help", ((sender, command, label, args) ->
            sender.sendMessage(Component.text("""
                            ModObserver lets you control which mods are used by the players.
                            It is recommended to use the config file located in plugins/ModObserver/config.yml instead of commands
                            /modObserver :
                            """, NamedTextColor.WHITE)
                    .append(Component.text("ignoredPlayers: ", NamedTextColor.YELLOW))
                    .append(Component.text("Players in this list are not checked for mods, therefore they don't need to have ModObserver installed.\n", NamedTextColor.WHITE))
                    .append(Component.text("mode: ", NamedTextColor.YELLOW))
                    .append(Component.text("Switches between blacklist and whitelist.\n", NamedTextColor.WHITE))
                    .append(Component.text("blacklistedPlayers: ", NamedTextColor.YELLOW))
                    .append(Component.text("Players in this list need to have ModObserver installed if modobserver-required option is set to false.\n", NamedTextColor.WHITE))
                    .append(Component.text("config reload: ", NamedTextColor.YELLOW))
                    .append(Component.text("Reloads the configuration file.\n", NamedTextColor.WHITE))
                    .append(Component.text("getMods: ", NamedTextColor.YELLOW))
                    .append(Component.text("Displays the mods used by a player.\n", NamedTextColor.WHITE))
                    .append(Component.text("getModsRaw: ", NamedTextColor.YELLOW))
                    .append(Component.text("Displays the raw mod packet sent by a player.\n", NamedTextColor.WHITE))
                    .append(Component.text("help: ", NamedTextColor.YELLOW))
                    .append(Component.text("Shows this message.\n", NamedTextColor.WHITE))))));

    // Get player mods
    public static final ModObserverCommandArg GET_MODS = registerCommandArg(new ModObserverCommandArg("getMods", ((sender, command, label, args) -> {
        try {
            String rawPacket = OnlinePlayers.getRawPacket(args[0]);

            if (rawPacket != null) {
                if (rawPacket.isEmpty()) {
                    sender.sendMessage(args[0] + " didn't send mods info");
                } else {
                    sender.sendMessage(JsonLoader.loadJsonFromString(rawPacket).keySet().toString());
                }
            } else {
                sender.sendMessage(Component.text(args[0] + " is offline!"));
            }
        } catch (IndexOutOfBoundsException e) {
            sender.sendMessage("You need to specify a player!");
        }
    }), ((commandSender, command, label, argsAfterLastCommand) -> Util.getOnlinePlayers())));

    public static final ModObserverCommandArg GET_MODS_RAW = registerCommandArg(new ModObserverCommandArg("getModsRaw", ((sender, command, label, args) -> {
        try {
            String rawPacket = OnlinePlayers.getRawPacket(args[0]);

            if (rawPacket != null) {
                if (rawPacket.isEmpty()) {
                    sender.sendMessage(args[0] + " didn't send mods info");
                } else {
                    sender.sendMessage(rawPacket);
                }
            } else {
                sender.sendMessage(Component.text(args[0] + " is offline!"));
            }
        } catch (IndexOutOfBoundsException e) {
            sender.sendMessage("You need to specify a player!");
        }
    }), ((commandSender, command, label, argsAfterLastCommand) -> Util.getOnlinePlayers())));


    //Config
    public static final ModObserverCommandArg CONFIG = registerCommandArg(new ModObserverCommandArg("config", List.of(
            new ModObserverCommandArg("reload", (sender, command, label, args) -> {
                ModObserver.getInstance().reloadConfig();
                Config.init(ModObserver.getInstance().getConfig());
                sender.sendMessage("Config was reloaded");
            })
    ), ((sender, command, label, args) -> sender.sendMessage(Component.text("Usage: /modObserver config reload", NamedTextColor.RED)))));


    //Mode
    public static final ModObserverCommandArg MODE = registerCommandArg(new ModObserverCommandArg("mode", List.of(
            new ModObserverCommandArg("switch", List.of(
                    new ModObserverCommandArg("blacklist", (sender, command, label, args) -> {
                        Config.setMode(Config.Mode.BLACKLIST);
                        sender.sendMessage("Current mode was set to: blacklist");
                    }),
                    new ModObserverCommandArg("whitelist", (sender, command, label, args) -> {
                        Config.setMode(Config.Mode.WHITELIST);
                        sender.sendMessage("Current mode was set to: whitelist");
                    })
            ), (sender, command, label, args) -> sender.sendMessage(Component.text("Use blacklist or whitelist", NamedTextColor.RED))),
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Current mode is: " + Config.getMode().getName()))
    ), (sender, command, label, args) -> sender.sendMessage(Component.text("Usage: /modObserver mode show\\switch whitelist\\blacklist", NamedTextColor.RED))));

    //Ignored players
    public static final ModObserverCommandArg IGNORED_PLAYERS = registerCommandArg(new ModObserverCommandArg("ignoredPlayers", List.of(
            new ModObserverCommandArg("add", (sender, command, label, args) -> {
                if (args.length > 0) {
                    List<String> list = Config.getIgnoredPlayers();
                    list.removeAll(List.of(args));
                    list.addAll(List.of(args));
                    Config.setIgnoredPlayers(list);

                    sender.sendMessage("Added " + Arrays.toString(args) + " to Ignored players list");
                } else {
                    sender.sendMessage(Component.text("You need to add at least one value after \"add\"", NamedTextColor.RED));
                }
            }),
            new ModObserverCommandArg("remove", (sender, command, label, args) -> {
                if (args.length > 0) {
                    List<String> list = Config.getIgnoredPlayers();
                    list.removeAll(List.of(args));
                    Config.setIgnoredPlayers(list);

                    sender.sendMessage("Removed " + Arrays.toString(args) + " from Ignored players list");
                } else {
                    sender.sendMessage(Component.text("You need to add at least one value after \"remove\"", NamedTextColor.RED));
                }
            }),
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Ignored players list: " + Config.getIgnoredPlayers())),
            new ConfirmCommandArg("reset", 10, Component.text("Are you sure you want to clear Ignored players list? All the entries in this list will be removed!\n" +
                    "Proceed by repeating the command", NamedTextColor.RED),
                    (sender, command, label, args) -> {
                        Config.setIgnoredPlayers(List.of());
                        sender.sendMessage("Ignored players list was cleared");
                    })
    ), (sender, command, label, args) -> sender.sendMessage(Component.text("/modObserver ignoredPlayers show\\add {playername} ...\\remove {playername} ...\\addAll\\removeAll\\reset", NamedTextColor.RED))));


    //Blacklisted players
    public static final ModObserverCommandArg BLACKLISTED_PLAYERS = registerCommandArg(new ModObserverCommandArg("blacklistedPlayers", List.of(
            new ModObserverCommandArg("add", (sender, command, label, args) -> {
                if (args.length > 0) {
                    List<String> list = Config.getBlacklistedPlayers();
                    list.removeAll(List.of(args));
                    list.addAll(List.of(args));
                    Config.setBlacklistedPlayers(list);

                    sender.sendMessage("Added " + Arrays.toString(args) + " to Blacklist");
                } else {
                    sender.sendMessage(Component.text("You need to add at least one value after \"add\"", NamedTextColor.RED));
                }
            }),
            new ModObserverCommandArg("remove", (sender, command, label, args) -> {
                if (args.length > 0) {
                    List<String> list = Config.getBlacklistedPlayers();
                    list.removeAll(List.of(args));
                    Config.setBlacklistedPlayers(list);

                    sender.sendMessage("Removed " + Arrays.toString(args) + " from Blacklist");
                } else {
                    sender.sendMessage(Component.text("You need to add at least one value after \"remove\"", NamedTextColor.RED));
                }
            }),
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Blacklisted players: " + Config.getBlacklistedPlayers())),
            new ConfirmCommandArg("reset", 10, Component.text("Are you sure you want to clear the Blacklist? All the entries in this list will be removed!\n" +
                    "Proceed by repeating the command", NamedTextColor.RED),
                    (sender, command, label, args) -> {
                        Config.setIgnoredPlayers(List.of());
                        sender.sendMessage("Blacklist was cleared");
                    })
    ), (sender, command, label, args) -> sender.sendMessage(Component.text("/modObserver blacklistedPlayers show\\add {playername} ...\\remove {playername} ...\\addAll\\removeAll\\reset", NamedTextColor.RED))));


    public static ModObserverCommandArg registerCommandArg(ModObserverCommandArg arg) {
        REGISTERED_COMMANDS.add(arg);
        return arg;
    }

    public static List<ModObserverCommandArg> getRegisteredCommands() {
        return List.copyOf(REGISTERED_COMMANDS);
    }
}
