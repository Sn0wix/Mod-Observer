package net.sn0wix_.modObserverPlugin.listeners;

import net.sn0wix_.modObserverPlugin.players.IncomingPlayers;
import net.sn0wix_.modObserverPlugin.Util;
import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.players.OnlinePlayersToCheck;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class Events implements Listener {
    //Lowest priority to keep the join message set to ""
    @EventHandler(priority = EventPriority.LOWEST)
    public void join(PlayerJoinEvent event) {
        if (!Util.checkIncomingPlayer(event.getPlayer())) {
            event.setJoinMessage("");
        } else {
            IncomingPlayers.removePlayer(event.getPlayer().getName());
            if (!Config.IGNORED_PLAYERS.contains(event.getPlayer().getName())) {
                OnlinePlayersToCheck.addPlayer(event.getPlayer().getName());
            }
        }
    }

    @EventHandler
    public void login(PlayerLoginEvent event) {
        if (event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) {
            IncomingPlayers.addPlayer(event.getPlayer().getName());

            if (Config.IGNORED_PLAYERS.contains(event.getPlayer().getName())) {
                IncomingPlayers.setApproved(event.getPlayer().getName());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void quit(PlayerQuitEvent event) {
        OnlinePlayersToCheck.removePlayer(event.getPlayer().getName());
        if (IncomingPlayers.containsPlayer(event.getPlayer().getName())) {
            IncomingPlayers.removePlayer(event.getPlayer().getName());
            event.setQuitMessage("");
        }
    }
}
