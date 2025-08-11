package net.sn0wix_.modObserverPlugin.commands;

import net.sn0wix_.modObserverPlugin.ModObserver;
import net.sn0wix_.modObserverPlugin.config.Config;
import org.bukkit.ChatColor;

import java.util.*;

public class ModObserverCommandArgs {
    private static final List<ModObserverCommandArg> REGISTERED_COMMANDS = new ArrayList<>();


    //Help
    public static final ModObserverCommandArg HELP = registerCommandArg(new ModObserverCommandArg("help", ((sender, command, label, args) ->
            sender.sendMessage("""
                            ModObserver lets you control which mods are used by the players.
                            It is recommended to use the config file located in plugins/ModObserver/config.yml instead of commands
                            /modObserver :
                            """,
                    ChatColor.BOLD + "ignoredPlayers: " + ChatColor.RESET + "Players in this list are not checked for mods, therefore they don't need ModObserver mod installed.\n" +
                    ChatColor.BOLD + "mode: " + ChatColor.RESET + "Switches between blacklist and whitelist.\n",
                    ChatColor.BOLD + "config reload: " + ChatColor.RESET + "Reloads the configuration file.\n",
                    ChatColor.BOLD + "help: " + ChatColor.RESET + "Shows this message.\n"))));

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
    ), ((sender, command, label, args) -> sender.sendMessage(ChatColor.RED + "Usage: /modObserver config reload"))));


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
            ), (sender, command, label, args) -> sender.sendMessage(ChatColor.RED + "Use blacklist or whitelist.")),
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Current mode is: " + Config.getMode().getName()))
    ), (sender, command, label, args) -> sender.sendMessage(ChatColor.RED + "Usage: /modObserver mode show\\switch whitelist\\blacklist")));


    /*//Whitelist
    public static final ModObserverCommandArg WHITELIST = registerCommandArg(new ModObserverCommandArg("whitelist", List.of(
            new ModObserverCommandArg("add", (sender, command, label, args) -> {
                if (args.length > 0) {
                    Config.WHITELISTED_MODS.addAll(List.of(args));
                    sender.sendMessage("Mods added to whitelist: " + Arrays.toString(args));
                } else {
                    sender.sendMessage(ChatColor.RED + "You need to add at least one value after \"add\"");
                }
            }),
            new ModObserverCommandArg("remove", (sender, command, label, args) -> {
                if (args.length > 0) {
                    Config.WHITELISTED_MODS.removeAll(List.of(args));
                    sender.sendMessage("Mods removed from whitelist: " + Arrays.toString(args));
                } else {
                    sender.sendMessage(ChatColor.RED + "You need to add at least one value after \"remove\"");
                }
            }),
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Whitelisted mods: " + Config.WHITELISTED_MODS)),
            new ModObserverCommandArg("addDefaults", (sender, command, label, args) -> {
                List<String> modsToAdd = List.copyOf(Objects.requireNonNull(Config.getDefaultWhitelist(ModObserverPlugin.CONFIG)));
                Config.WHITELISTED_MODS.removeAll(modsToAdd);
                Config.WHITELISTED_MODS.addAll(modsToAdd);
                sender.sendMessage("Added default entries to whitelist.");
            }),
            new ConfirmCommandArg("clear", 10, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Are you sure you want to clear the whitelist? All the entries in this list will be removed!\nProceed or by repeating the command.",
                    (sender, command, label, args) -> {
                        Iterator<String> iterator = Config.WHITELISTED_MODS.listIterator();
                        while (iterator.hasNext()) {
                            iterator.next();
                            iterator.remove();
                        }
                        sender.sendMessage("Whitelist was cleared.");
                    }),
            new ModObserverCommandArg("addAll", (sender, command, label, args) -> {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "You need to pass in the player who's mods you want to add.");
                }
                if (Util.checkIfOnline(args[0], sender)) {
                    sender.sendMessage("Waiting for response from " + args[0]);
                    WaitingForResponsePlayers.addPlayer(new WaitingForResponsePlayers.WaitingForResponsePlayer(Bukkit.getPlayerExact(args[0]).getName(), sender, modids -> {
                        Config.WHITELISTED_MODS.removeAll(List.of(modids));
                        Config.WHITELISTED_MODS.addAll(List.of(modids));
                        sender.sendMessage("Added all mods provided by " + args[0] + " which are: " + Arrays.toString(modids));
                    }));
                }
            }, (commandSender, command, label, argsAfterLastCommand) -> Util.getAllOnlinePlayers()),
            new ModObserverCommandArg("removeAll", (sender, command, label, args) -> {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "You need to pass in the player who's mods you want to remove.");
                }
                if (Util.checkIfOnline(args[0], sender)) {
                    sender.sendMessage("Waiting for response from " + args[0]);
                    WaitingForResponsePlayers.addPlayer(new WaitingForResponsePlayers.WaitingForResponsePlayer(Bukkit.getPlayerExact(args[0]).getName(), sender, modids -> {
                        Config.WHITELISTED_MODS.removeAll(List.of(modids));
                        sender.sendMessage("Removed all mods provided by " + args[0] + " which are: " + Arrays.toString(modids));
                    }));
                }
            }, (commandSender, command, label, argsAfterLastCommand) -> Util.getAllOnlinePlayers())
    ), (sender, command, label, args) -> sender.sendMessage(ChatColor.RED + "/modObserver whitelist show\\clear\\add modid modid ...\\remove modid modid ...\\addAll <playerName>\\removeAll <playerName>")));


    //Blacklist
    public static final ModObserverCommandArg BLACKLIST = registerCommandArg(new ModObserverCommandArg("blacklist", List.of(
            new ModObserverCommandArg("add", (sender, command, label, args) -> {
                if (args.length > 0) {
                    Config.BLACKLISTED_MODS.addAll(List.of(args));
                    sender.sendMessage("Mods added to blacklist: " + Arrays.toString(args));
                } else {
                    sender.sendMessage(ChatColor.RED + "You need to add at least one value after \"add\"");
                }
            }),
            new ModObserverCommandArg("remove", (sender, command, label, args) -> {
                if (args.length > 0) {
                    Config.BLACKLISTED_MODS.removeAll(List.of(args));
                    sender.sendMessage("Mods removed from blacklist: " + Arrays.toString(args));
                } else {
                    sender.sendMessage(ChatColor.RED + "You need to add at least one value after \"remove\"");
                }
            }),
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Blacklisted mods: " + Config.BLACKLISTED_MODS)),
            new ConfirmCommandArg("clear", 10, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Are you sure you want to clear the blacklist? All the entries in this list will be removed!\nProceed with by repeating the command.",
                    (sender, command, label, args) -> {
                        Iterator<String> iterator = Config.BLACKLISTED_MODS.listIterator();
                        while (iterator.hasNext()) {
                            iterator.next();
                            iterator.remove();
                        }
                        sender.sendMessage("Blacklist was cleared.");
                    }),
            new ModObserverCommandArg("addAll", (sender, command, label, args) -> {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "You need to pass in the player who's mods you want to add.");
                }
                if (Util.checkIfOnline(args[0], sender)) {
                    sender.sendMessage("Waiting for response from " + args[0]);
                    WaitingForResponsePlayers.addPlayer(new WaitingForResponsePlayers.WaitingForResponsePlayer(Bukkit.getPlayerExact(args[0]).getName(), sender, modids -> {
                        ArrayList<String> modidsArray = new ArrayList<>(List.of(modids));
                        modidsArray.removeAll(Config.getDefaultWhitelist(ModObserverPlugin.CONFIG));
                        modidsArray.trimToSize();
                        Config.BLACKLISTED_MODS.removeAll(modidsArray);
                        Config.BLACKLISTED_MODS.addAll(modidsArray);
                        sender.sendMessage("Added all mods provided by " + args[0] + " except defaulty whitelisted mods, which are: " + modidsArray);
                    }));
                }
            }, (commandSender, command, label, argsAfterLastCommand) -> Util.getAllOnlinePlayers()),
            new ModObserverCommandArg("removeAll", (sender, command, label, args) -> {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "You need to pass in the player who's mods you want to remove.");
                }
                if (Util.checkIfOnline(args[0], sender)) {
                    sender.sendMessage("Waiting for response from " + args[0]);
                    WaitingForResponsePlayers.addPlayer(new WaitingForResponsePlayers.WaitingForResponsePlayer(Bukkit.getPlayerExact(args[0]).getName(), sender, modids -> {
                        Config.BLACKLISTED_MODS.removeAll(List.of(modids));
                        sender.sendMessage("Removed all mods provided by " + args[0] + " which are: " + Arrays.toString(modids));
                    }));
                }
            }, (commandSender, command, label, argsAfterLastCommand) -> Util.getAllOnlinePlayers())
    ), (sender, command, label, args) -> sender.sendMessage(ChatColor.RED + "/modObserver blacklist show\\clear\\add modid modid ...\\remove modid modid ...\\addAll <playerName>\\removeAll <playerName>")));


    //Required mods
    public static final ModObserverCommandArg REQUIRED_MODS = registerCommandArg(new ModObserverCommandArg("requiredMods", List.of(
            new ModObserverCommandArg("add", (sender, command, label, args) -> {
                if (args.length > 0) {
                    Config.REQUIRED_MODS.addAll(List.of(args));
                    sender.sendMessage("Required mods added: " + Arrays.toString(args));
                } else {
                    sender.sendMessage(ChatColor.RED + "You need to add at least one value after \"add\"");
                }
            }),
            new ModObserverCommandArg("remove", (sender, command, label, args) -> {
                if (args.length > 0) {
                    Config.REQUIRED_MODS.removeAll(List.of(args));
                    sender.sendMessage("Required mods removed: " + Arrays.toString(args));
                } else {
                    sender.sendMessage(ChatColor.RED + "You need to add at least one value after \"remove\"");
                }
            }),
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Required mods: " + Config.REQUIRED_MODS)),
            new ConfirmCommandArg("clear", 10, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Are you sure you want to clear Required mods list? All the entries in this list will be removed!\nProceed by repeating the command.",
                    (sender, command, label, args) -> {
                        Iterator<String> iterator = Config.REQUIRED_MODS.listIterator();
                        while (iterator.hasNext()) {
                            iterator.next();
                            iterator.remove();
                        }
                        sender.sendMessage("Required mods list was cleared.");
                    }),
            new ModObserverCommandArg("addAll", (sender, command, label, args) -> {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "You need to pass in the player who's mods you want to add.");
                }
                if (Util.checkIfOnline(args[0], sender)) {
                    sender.sendMessage("Waiting for response from " + args[0]);
                    WaitingForResponsePlayers.addPlayer(new WaitingForResponsePlayers.WaitingForResponsePlayer(Bukkit.getPlayerExact(args[0]).getName(), sender, modids -> {
                        Config.REQUIRED_MODS.removeAll(List.of(modids));
                        Config.REQUIRED_MODS.addAll(List.of(modids));
                        sender.sendMessage("Added all mods provided by " + args[0] + " which are: " + Arrays.toString(modids));
                    }));
                }
            }, (commandSender, command, label, argsAfterLastCommand) -> Util.getAllOnlinePlayers()),
            new ModObserverCommandArg("removeAll", (sender, command, label, args) -> {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "You need to pass in the player who's mods you want to remove.");
                }
                if (Util.checkIfOnline(args[0], sender)) {
                    sender.sendMessage("Waiting for response from " + args[0]);
                    WaitingForResponsePlayers.addPlayer(new WaitingForResponsePlayers.WaitingForResponsePlayer(Bukkit.getPlayerExact(args[0]).getName(), sender, modids -> {
                        Config.REQUIRED_MODS.removeAll(List.of(modids));
                        sender.sendMessage("Removed all mods provided by " + args[0] + " which are: " + Arrays.toString(modids));
                    }));
                }
            }, (commandSender, command, label, argsAfterLastCommand) -> Util.getAllOnlinePlayers())
    ), (sender, command, label, args) -> sender.sendMessage(ChatColor.RED + "/modObserver requiredMods show\\add modid modid ...\\remove modid modid ...\\clear\\addAll <playerName>\\removeAll <playername>")));*/

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
                    sender.sendMessage(ChatColor.RED + "You need to add at least one value after \"add\"");
                }
            }),
            new ModObserverCommandArg("remove", (sender, command, label, args) -> {
                if (args.length > 0) {
                    List<String> list = Config.getIgnoredPlayers();
                    list.removeAll(List.of(args));
                    Config.setIgnoredPlayers(list);

                    sender.sendMessage("Removed " + Arrays.toString(args) + " from Ignored players list.");
                } else {
                    sender.sendMessage(ChatColor.RED + "You need to add at least one value after \"remove\"");
                }
            }),
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Ignored players list: " + Config.getIgnoredPlayers())),
            new ConfirmCommandArg("clear", 10, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Are you sure you want to clear Ignored players list? All the entries in this list will be removed!\nProceed by repeating the command.",
                    (sender, command, label, args) -> {
                        Config.setIgnoredPlayers(List.of());
                        sender.sendMessage("Ignored players list was cleared.");
                    })
    ), (sender, command, label, args) -> sender.sendMessage(ChatColor.RED + "/modObserver ignoredPlayers show\\add playername playername ...\\remove playername playername ...\\addAll\\removeAll\\reset")));


    public static ModObserverCommandArg registerCommandArg(ModObserverCommandArg arg) {
        REGISTERED_COMMANDS.add(arg);
        return arg;
    }

    public static List<ModObserverCommandArg> getRegisteredCommands() {
        return List.copyOf(REGISTERED_COMMANDS);
    }
}
