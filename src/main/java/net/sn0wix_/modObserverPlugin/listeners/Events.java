package net.sn0wix_.modObserverPlugin.listeners;

import net.sn0wix_.modObserverPlugin.IncomingPlayers;
import net.sn0wix_.modObserverPlugin.ModObserverPlugin;
import net.sn0wix_.modObserverPlugin.config.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class Events implements Listener {
    @EventHandler
    public void joinEvent(PlayerJoinEvent event) {
        if (!ModObserverPlugin.checkPlayer(event.getPlayer())) {
            event.setJoinMessage("");
        } else {
            IncomingPlayers.removePlayer(event.getPlayer().getName());
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

    @EventHandler
    public void kick(PlayerKickEvent event) {
        if (IncomingPlayers.containsPlayer(event.getPlayer().getName())) {
            IncomingPlayers.removePlayer(event.getPlayer().getName());
            event.setLeaveMessage("");
        }
    }
}
