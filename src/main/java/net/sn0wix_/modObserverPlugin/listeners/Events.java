package net.sn0wix_.modObserverPlugin.listeners;

import io.papermc.paper.event.connection.configuration.PlayerConnectionInitialConfigureEvent;
import net.kyori.adventure.text.Component;
import net.sn0wix_.modObserverPlugin.Util;
import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.players.IncomingPlayers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {
    //Lowest priority to keep the join message set to an empty string
    /*@EventHandler(priority = EventPriority.LOWEST)
    public void join(PlayerJoinEvent event) {
        if (!Util.checkIncomingPlayer(event.getPlayer())) {
            event.joinMessage(Component.empty());
        } else {
            IncomingPlayers.removePlayer(event.getPlayer().getName());
        }
    }*/


    /*@EventHandler
    public void login(PlayerLoginEvent event) {
        IncomingPlayers.addPlayer(event.getConnection().getProfile().getName());

        if (Config.IGNORED_PLAYERS.contains(event.getConnection().getProfile().getName())) {
            IncomingPlayers.setApproved(event.getConnection().getProfile().getName());
        }
    }*/

    /*@EventHandler(priority = EventPriority.LOWEST)
    public void quit(PlayerQuitEvent event) {
        if (IncomingPlayers.containsPlayer(event.getPlayer().getName())) {
            IncomingPlayers.removePlayer(event.getPlayer().getName());
            event.quitMessage(Component.empty());
        }
    }*/
}
