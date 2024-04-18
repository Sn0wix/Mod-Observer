package net.sn0wix_.modObserverPlugin.commands;

import net.sn0wix_.modObserverPlugin.ModObserverPlugin;
import net.sn0wix_.modObserverPlugin.Util;
import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.networking.PacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;

public class ModObserverCommandArgs {
    private static final List<ModObserverCommandArg> REGISTERED_COMMANDS = new ArrayList<>();


    //Help
    public static final ModObserverCommandArg HELP = registerCommandArg(new ModObserverCommandArg("help", ((sender, command, label, args) -> sender.sendMessage("Insert help message here."))));


    //Config
    public static final ModObserverCommandArg CONFIG = registerCommandArg(new ModObserverCommandArg("config", List.of(
            new ModObserverCommandArg("reload", (sender, command, label, args) -> {
                Config.loadValues(ModObserverPlugin.CONFIG);
                sender.sendMessage("Config was reloaded.");
            }),
            new ConfirmCommandArg("reset", 10, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Are you sure you want to reset config settings?\nProceed with /modObserverConfirm or by repeating the command.",
                    (sender, command, label, args) -> {
                        Config.loadDefaults(ModObserverPlugin.CONFIG);
                        sender.sendMessage("Config was reseted.");
                    })
    ), ((sender, command, label, args) -> sender.sendMessage(ChatColor.RED + "Usage: /modObserver config reload/save/reset"))));


    //Mode
    public static final ModObserverCommandArg MODE = registerCommandArg(new ModObserverCommandArg("mode", List.of(
            new ModObserverCommandArg("switch", List.of(
                    new ModObserverCommandArg("blacklist", (sender, command, label, args) -> {
                        Config.MODE = Config.Mode.BLACKLIST;
                        sender.sendMessage("Current mode was set to: blacklist");
                    }),
                    new ModObserverCommandArg("whitelist", (sender, command, label, args) -> {
                        Config.MODE = Config.Mode.WHITELIST;
                        sender.sendMessage("Current mode was set to: whitelist");
                    })
            ), (sender, command, label, args) -> sender.sendMessage(ChatColor.RED + "Use blacklist or whitelist.")),
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Current mode is: " + Config.MODE.getName()))
    ), (sender, command, label, args) -> sender.sendMessage(ChatColor.RED + "Usage: /modObserver mode show/switch whitelist/blacklist")));


    //Whitelist
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
            }), //add all, clear - yes, no
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Whitelisted mods: " + Config.WHITELISTED_MODS)),
            new ModObserverCommandArg("addDefaults", (sender, command, label, args) -> {
                List<String> modsToAdd = List.copyOf(Objects.requireNonNull(Config.getDefaultWhitelist(ModObserverPlugin.CONFIG)));
                Config.WHITELISTED_MODS.removeAll(modsToAdd);
                Config.WHITELISTED_MODS.addAll(modsToAdd);
                sender.sendMessage("Added default entries to whitelist.");
            }),
            new ConfirmCommandArg("clear", 10, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Are you sure you want to clear the whitelist? All the entries in this list will be removed!\nProceed with /modobserverconfirm or by repeating the command.",
                    (sender, command, label, args) -> {
                        Iterator<String> iterator = Config.WHITELISTED_MODS.listIterator();
                        while (iterator.hasNext()) {
                            iterator.next();
                            iterator.remove();
                        }
                        sender.sendMessage("Whitelist was cleared.");
                    }),
            new ModObserverCommandArg("addAll", (sender, command, label, args) -> {
                //TODO fix
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "You need to pass player who's mods you want to add.");
                } else if (Bukkit.getPlayerExact(args[0]) != null) {
                    PacketHandler.send(ModObserverPlugin.PLUGIN, Objects.requireNonNull(Bukkit.getPlayerExact(args[0])), new byte[0]);
                    sender.sendMessage("Waiting for response from " + args[0]);
                    Util.PLAYERS_WAITING_FOR_RESPONSE.put(args[0], modids -> {
                        if (modids.length == 0) {
                            ModObserverPlugin.LOGGER.info("Suspicious activity from " + args[0] + ". Kicking the player.");
                            ModObserverPlugin.LOGGER.info("Suspicious activity from " + args[0] + ". Kicking the player.");
                            Objects.requireNonNull(Bukkit.getPlayerExact(args[0])).kickPlayer("ModObserver didn't respond correctly, try reinstalling it.");
                        }

                        Config.WHITELISTED_MODS.removeAll(List.of(modids));
                        Config.WHITELISTED_MODS.addAll(List.of(modids));
                        sender.sendMessage("Added all mods provided by " + args[0] + " which are: " + Arrays.toString(modids));
                    });
                } else {
                    sender.sendMessage(ChatColor.RED + "Player " + args[0] + " is not online!");
                }
            }, (commandSender, command, label, argsAfterLastCommand) -> Util.getAllOnlinePlayers())
    ), (sender, command, label, args) -> sender.sendMessage(ChatColor.RED + "/modObserver whitelist show/add modid modid .../remove modid modid ...")));


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
            //TODO add all
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Blacklisted mods: " + Config.BLACKLISTED_MODS)),
            new ConfirmCommandArg("clear", 10, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Are you sure you want to clear the blacklist? All the entries in this list will be removed!\nProceed with /modobserverconfirm or by repeating the command.",
                    (sender, command, label, args) -> {
                        Iterator<String> iterator = Config.BLACKLISTED_MODS.listIterator();
                        while (iterator.hasNext()) {
                            iterator.next();
                            iterator.remove();
                        }
                        sender.sendMessage("Blacklist was cleared.");
                    })
    ), (sender, command, label, args) -> sender.sendMessage(ChatColor.RED + "/modObserver blacklist show/add modid modid .../remove modid modid ...")));


    //Ignored players
    public static final ModObserverCommandArg IGNORED_PLAYERS = registerCommandArg(new ModObserverCommandArg("ignoredPlayers", List.of(
            new ModObserverCommandArg("add", (sender, command, label, args) -> {
                if (args.length > 0) {
                    Config.IGNORED_PLAYERS.addAll(List.of(args));
                    sender.sendMessage("Added ignored players: " + Arrays.toString(args));
                } else {
                    sender.sendMessage(ChatColor.RED + "You need to add at least one value after \"add\"");
                }
            }, (commandSender, command, label, argsAfterLastCommand) -> Util.getAllOnlinePlayers()),
            new ModObserverCommandArg("remove", (sender, command, label, args) -> {
                if (args.length > 0) {
                    Config.IGNORED_PLAYERS.removeAll(List.of(args));
                    sender.sendMessage("Removed ignored players: " + Arrays.toString(args));
                } else {
                    sender.sendMessage(ChatColor.RED + "You need to add at least one value after \"remove\"");
                }
            }, (commandSender, command, label, argsAfterLastCommand) -> Util.getAllOnlinePlayers()), //add all, clear - yes, no
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Ignored players list: " + Config.IGNORED_PLAYERS)),
            new ModObserverCommandArg("addAll", (sender, command, label, args) -> {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (!Config.IGNORED_PLAYERS.contains(player.getName())) {
                        Config.IGNORED_PLAYERS.add(player.getName());
                    }
                });

                sender.sendMessage("Added all online players to ignored players list.");
            }),
            new ModObserverCommandArg("removeAll", (sender, command, label, args) -> {
                Bukkit.getOnlinePlayers().forEach(player -> Config.IGNORED_PLAYERS.remove(player.getName()));
                sender.sendMessage("Removed all online players from ignored players list.");
            }),
            new ConfirmCommandArg("clear", 10, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Are you sure you want to clear Ignored players list? All the entries in this list will be removed!\nProceed with /modobserverconfirm or by repeating the command.",
                    (sender, command, label, args) -> {
                        Iterator<String> iterator = Config.IGNORED_PLAYERS.listIterator();
                        while (iterator.hasNext()) {
                            iterator.next();
                            iterator.remove();
                        }
                        sender.sendMessage("Ignored players list was cleared.");
                    })
    ), (sender, command, label, args) -> sender.sendMessage(ChatColor.RED + "/modObserver ignoredPlayers show/add playername playername .../remove playername playername .../addAll/removeAll/reset")));


    public static ModObserverCommandArg registerCommandArg(ModObserverCommandArg arg) {
        REGISTERED_COMMANDS.add(arg);
        return arg;
    }

    public static List<ModObserverCommandArg> getRegisteredCommands() {
        return List.copyOf(REGISTERED_COMMANDS);
    }
}
