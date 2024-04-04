package net.sn0wix_.modObserverPlugin.listeners;

import net.sn0wix_.modObserverPlugin.IncomingPlayers;
import net.sn0wix_.modObserverPlugin.config.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class Events implements Listener {
    public static final String MODS_KICK_REASON = "Your mods are not allowed on this server.";
    public static final String MOD_OBSERVER_NOT_INSTALLED = "Your must install ModObserver.";

    @EventHandler
    public void joinEvent(PlayerJoinEvent event) {
        if (!IncomingPlayers.hasSendPacket(event.getPlayer().getName())) {
            event.getPlayer().kickPlayer(Config.MOD_OBSERVER_REQUIRED_MESSAGE);
            event.setJoinMessage("");
        } else if (!IncomingPlayers.isApproved(event.getPlayer().getName())) {
            event.getPlayer().kickPlayer(Config.PROHIBITED_MODS_FOUND_MESSAGE.replace("<$MODS$>", IncomingPlayers.getNonApprovedMods(event.getPlayer().getName())));
            event.setJoinMessage("");
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
        if (event.getReason().equals(MODS_KICK_REASON) || event.getReason().equals(MOD_OBSERVER_NOT_INSTALLED)) {
            event.setLeaveMessage("");
            IncomingPlayers.removePlayer(event.getPlayer().getName());
        }
    }
}
