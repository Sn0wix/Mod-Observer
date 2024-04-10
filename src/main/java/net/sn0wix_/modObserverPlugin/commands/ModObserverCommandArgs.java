package net.sn0wix_.modObserverPlugin.commands;

import com.sun.tools.javac.Main;
import net.sn0wix_.modObserverPlugin.ModObserverPlugin;
import net.sn0wix_.modObserverPlugin.config.Config;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ModObserverCommandArgs {
    public static final List<ModObserverCommandArg> COMMAND_ARGS = new ArrayList<>();

    //Help
    public static final ModObserverCommandArg HELP = registerCommandArg(new ModObserverCommandArg("help", ((sender, command, label, args) -> sender.sendMessage("Insert help message here."))));



    //Config
    public static final ModObserverCommandArg CONFIG = registerCommandArg(new ModObserverCommandArg("config", List.of(
            new ModObserverCommandArg("reload", (sender, command, label, args) -> sender.sendMessage("Config reloaded.")),
            new ModObserverCommandArg("save", (sender, command, label, args) -> {
                Config.saveValues(ModObserverPlugin.CONFIG);
                sender.sendMessage("Config was saved.");
            }),
            new ModObserverCommandArg("reset", (sender, command, label, args) -> sender.sendMessage(ChatColor.DARK_RED + "Are you sure you want to reset config settings? Proceed with /modObserverConfirm."))
    ), ((sender, command, label, args) -> sender.sendMessage(ChatColor.DARK_RED + "Usage: /modObserver config reload/save/reset"))));



    //Mode
    public static final ModObserverCommandArg MODE = registerCommandArg(new ModObserverCommandArg("mode", List.of(
            new ModObserverCommandArg("switch", List.of(
                    new ModObserverCommandArg("blacklist", (sender, command, label, args) -> {
                        Config.MODE = Config.Mode.BLACKLIST;
                        sender.sendMessage("Mode was set to blacklist.");
                    }),
                    new ModObserverCommandArg("whitelist", (sender, command, label, args) -> {
                        Config.MODE = Config.Mode.WHITELIST;
                        sender.sendMessage("Mode was set to whitelist.");
                    })
            ), (sender, command, label, args) -> sender.sendMessage(ChatColor.DARK_RED + "Use blacklist or whitelist.")),
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Mode was set to: " + Config.MODE.getName()))
    ), (sender, command, label, args) -> sender.sendMessage(ChatColor.DARK_RED + "Usage: /modObserver mode show/switch whitelist/blacklist")));



    //Whitelist
    public static final ModObserverCommandArg WHITELIST = registerCommandArg(new ModObserverCommandArg("whitelist", List.of(
            new ModObserverCommandArg("add", (sender, command, label, args) -> {
                ArrayList<String> mods = new ArrayList<>();
                for (int i = 0; i < args.length; i++) {
                    if (i > 2) {
                        mods.add(args[i]);
                    }
                }
                Config.WHITELISTED_MODS.addAll(mods);
                sender.sendMessage("Mods added to whitelist: " + mods);
            }),
            new ModObserverCommandArg("remove", (sender, command, label, args) -> {
                ArrayList<String> mods = new ArrayList<>();
                for (int i = 0; i < args.length; i++) {
                    if (i > 2) {
                        mods.add(args[i]);
                    }
                }
                Config.WHITELISTED_MODS.removeAll(mods);
                sender.sendMessage("Mods removed from whitelist: " + mods);
            }), //add all, clear - yes, no
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Whitelisted mods: " + Config.WHITELISTED_MODS))
    ), (sender, command, label, args) -> sender.sendMessage(ChatColor.DARK_RED + "/modObserver whitelist show/add modid modid .../remove modid modid ...")));



    //Blacklist
    public static final ModObserverCommandArg BLACKLIST = registerCommandArg(new ModObserverCommandArg("blacklist", List.of(
            new ModObserverCommandArg("add", (sender, command, label, args) -> {
                ArrayList<String> mods = new ArrayList<>();
                for (int i = 0; i < args.length; i++) {
                    if (i > 2) {
                        mods.add(args[i]);
                    }
                }
                Config.BLACKLISTED_MODS.addAll(mods);
                sender.sendMessage("Mods added to blacklist: " + mods);
            }),
            new ModObserverCommandArg("remove", (sender, command, label, args) -> {
                ArrayList<String> mods = new ArrayList<>();
                for (int i = 0; i < args.length; i++) {
                    if (i > 2) {
                        mods.add(args[i]);
                    }
                }
                Config.BLACKLISTED_MODS.removeAll(mods);
                sender.sendMessage("Mods removed from blacklist: " + mods);
            }), //add all, clear - yes, no
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Blacklisted mods: " + Config.BLACKLISTED_MODS))
    ), (sender, command, label, args) -> sender.sendMessage(ChatColor.DARK_RED + "/modObserver blacklist show/add modid modid .../remove modid modid ...")));



    //Ignored players
    public static final ModObserverCommandArg IGNORED_PLAYERS = registerCommandArg(new ModObserverCommandArg("ignoredPlayers", List.of(
            new ModObserverCommandArg("add", (sender, command, label, args) -> {
                ArrayList<String> players = new ArrayList<>();
                for (int i = 0; i < args.length; i++) {
                    if (i > 2) {
                        players.add(args[i]);
                    }
                }
                Config.IGNORED_PLAYERS.addAll(players);
                sender.sendMessage("Added ignored players: " + players);
            }),
            new ModObserverCommandArg("remove", (sender, command, label, args) -> {
                ArrayList<String> players = new ArrayList<>();
                for (int i = 0; i < args.length; i++) {
                    if (i > 2) {
                        players.add(args[i]);
                    }
                }
                Config.IGNORED_PLAYERS.removeAll(players);
                sender.sendMessage("Removed ignored players: " + players);
            }), //add all, clear - yes, no
            new ModObserverCommandArg("show", (sender, command, label, args) -> sender.sendMessage("Ignored players list: " + Config.IGNORED_PLAYERS)),
            new ModObserverCommandArg("addAll", (sender, command, label, args) -> sender.sendMessage("Added all online players to ignored players list.")),
            new ModObserverCommandArg("removeAll", (sender, command, label, args) -> sender.sendMessage("Removed all online players from ignored players list.")),
            new ModObserverCommandArg("reset", (sender, command, label, args) -> sender.sendMessage(ChatColor.DARK_RED + "Are you sure you want to reset ignored players list? Proceed with /modObserverConfirm."))
    ), (sender, command, label, args) -> sender.sendMessage(ChatColor.DARK_RED + "/modObserver ignoredPlayers show/add playername playername .../remove playername playername .../addAll/removeAll/reset")));


    public static ModObserverCommandArg registerCommandArg(ModObserverCommandArg arg) {
        COMMAND_ARGS.add(arg);
        return arg;
    }
}
