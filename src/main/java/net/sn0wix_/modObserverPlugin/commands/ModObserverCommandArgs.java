package net.sn0wix_.modObserverPlugin.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.sn0wix_.modObserverPlugin.ModObserver;
import net.sn0wix_.modObserverPlugin.config.Config;
import org.bukkit.ChatColor;

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
                    .append(Component.text("ignoredPlayers: ", NamedTextColor.WHITE, TextDecoration.BOLD))
                    .append(Component.text("Players in this list are not checked for mods, therefore they don't need ModObserver mod installed.\n", NamedTextColor.WHITE))
                    .append(Component.text("mode: ", NamedTextColor.WHITE, TextDecoration.BOLD))
                    .append(Component.text("Switches between blacklist and whitelist.\n", NamedTextColor.WHITE))
                    .append(Component.text("config reload: ", NamedTextColor.WHITE, TextDecoration.BOLD))
                    .append(Component.text("Reloads the configuration file.\n", NamedTextColor.WHITE))
                    .append(Component.text("help: ", NamedTextColor.WHITE, TextDecoration.BOLD))
                    .append(Component.text("Shows this message.\n", NamedTextColor.WHITE))))));

    //Check player
    /*public static final ModObserverCommandArg CHECK_PLAYER = registerCommandArg(new ModObserverCommandArg("checkPlayer", List.of(
            new ModObserverCommandArg("kickIf", (sender, command, label, args) -> {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "You need to pass in the player name after \"kickIf\"");
                    return;
                }
                if (!Util.checkIfOnline(args[0], sender)) return;
                if (Util.checkPlayer(args[0], new String[0], false)) {
                    sender.sendMessage(ChatColor.GREEN + args[0] + " is in Ignored players list.");
                    return;
                }
                WaitingForResponsePlayers.addPlayer(new WaitingForResponsePlayers.WaitingForResponsePlayer(args[0], sender, modids -> {
                    boolean bl = Util.checkPlayer(args[0], modids, true);
                    if (!bl) {
                        sender.sendMessage(ChatColor.RED + args[0] + " did not pass the mod check. Kicking the player." + ChatColor.RESET +
                                "\nProhibited mods found: " + Util.getNonApprovedMods(modids) +
                                "\nMissing required mods: " + Util.getMissingRequiredMods(modids));
                        if (!Util.getNonApprovedMods(modids).isEmpty()) {
                            Bukkit.getPlayerExact(args[0]).kickPlayer(Config.PROHIBITED_MODS_FOUND_MESSAGE.replace("<$MODS$>", Arrays.toString(modids)));
                        } else {
                            Bukkit.getPlayerExact(args[0]).kickPlayer(Config.REQUIRED_MODS_MESSAGE.replace("<$MODS$>", Arrays.toString(modids)));
                        }
                    } else {
                        sender.sendMessage(ChatColor.GREEN + args[0] + " passed the mod check with mods " + ChatColor.RESET + Arrays.toString(modids));
                    }
                }, () -> {
                    sender.sendMessage(ChatColor.RED + "No ModObserver installation found on " + args[0] + "\nKicking the player.");
                    Bukkit.getPlayerExact(args[0]).kickPlayer(Config.MOD_OBSERVER_REQUIRED_MESSAGE);
                }));
            }, (commandSender, command, label, argsAfterLastCommand) -> Util.getAllOnlinePlayers()),

            new ModObserverCommandArg("dontKickIf", (sender, command, label, args) -> {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "You need to pass in the player name after \"dontKickIf\"");
                    return;
                }
                if (!Util.checkIfOnline(args[0], sender)) return;
                if (Util.checkPlayer(args[0], new String[0], false)) {
                    sender.sendMessage(ChatColor.GREEN + args[0] + " is in Ignored players list.");
                    return;
                }
                WaitingForResponsePlayers.addPlayer(new WaitingForResponsePlayers.WaitingForResponsePlayer(args[0], sender, modids -> {
                    boolean bl = Util.checkPlayer(args[0], modids, false);
                    if (!bl) {
                        sender.sendMessage(ChatColor.RED + args[0] + " did not the pass mod check." + ChatColor.RESET +
                                "\nProhibited mods found: " + Util.getNonApprovedMods(modids) +
                                "\nMissing required mods: " + Util.getMissingRequiredMods(modids));
                    } else {
                        sender.sendMessage(ChatColor.GREEN + args[0] + " passed the mod check with mods " + ChatColor.RESET + Arrays.toString(modids));
                    }
                }, () -> sender.sendMessage(ChatColor.RED + "No ModObserver installation found on " + args[0])));
            }, (commandSender, command, label, argsAfterLastCommand) -> Util.getAllOnlinePlayers())),
            (sender, command, label, args) -> sender.sendMessage(ChatColor.RED + "Usage: /modObserver checkPlayer kickIf\\dontKickIf <playername>")));*/

    //Config
    public static final ModObserverCommandArg CONFIG = registerCommandArg(new ModObserverCommandArg("config", List.of(
            new ModObserverCommandArg("reload", (sender, command, label, args) -> {
                ModObserver.getInstance().reloadConfig();
                Config.init(ModObserver.getInstance().getConfig());
                sender.sendMessage("Config was reloaded.");
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
            ), (sender, command, label, args) -> sender.sendMessage(Component.text("Use blacklist or whitelist.", NamedTextColor.RED))),
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

                    sender.sendMessage("Added " + Arrays.toString(args) + " to Ignored players list.");
                } else {
                    sender.sendMessage(Component.text("You need to add at least one value after \"add\"", NamedTextColor.RED));
                }
            }),
            new ModObserverCommandArg("remove", (sender, command, label, args) -> {
                if (args.length > 0) {
                    List<String> list = Config.getIgnoredPlayers();
                    list.removeAll(List.of(args));
                    Config.setIgnoredPlayers(list);

                    sender.sendMessage("Removed " + Arrays.toString(args) + " from Ignored players list.");
                } else {
                    sender.sendMessage(Component.text("You need to add at least one value after \"remove\"", NamedTextColor.RED));
                }
            }),
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Ignored players list: " + Config.getIgnoredPlayers())),
            new ConfirmCommandArg("clear", 10,Component.text("Are you sure you want to clear Ignored players list? All the entries in this list will be removed!\n" +
                    "Proceed by repeating the command.", NamedTextColor.RED) ,
                    (sender, command, label, args) -> {
                        Config.setIgnoredPlayers(List.of());
                        sender.sendMessage("Ignored players list was cleared.");
                    })
    ), (sender, command, label, args) -> sender.sendMessage(Component.text("/modObserver ignoredPlayers show\\add {playername} ...\\remove {playername} ...\\addAll\\removeAll\\reset", NamedTextColor.RED))));


    public static ModObserverCommandArg registerCommandArg(ModObserverCommandArg arg) {
        REGISTERED_COMMANDS.add(arg);
        return arg;
    }

    public static List<ModObserverCommandArg> getRegisteredCommands() {
        return List.copyOf(REGISTERED_COMMANDS);
    }
}
