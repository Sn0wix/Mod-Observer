package net.sn0wix_.modObserverPlugin.listeners;

import io.papermc.paper.event.connection.PlayerConnectionValidateLoginEvent;
import io.papermc.paper.event.connection.configuration.PlayerConnectionInitialConfigureEvent;
import io.papermc.paper.event.player.PlayerServerFullCheckEvent;
import net.sn0wix_.modObserverPlugin.ModObserverPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Events implements Listener {
    /*@EventHandler
    public void testEvent(PlayerConnectionValidateLoginEvent event) {
        ModObserverPlugin.LOGGER.info("------------------------------------------------------");
        ModObserverPlugin.LOGGER.info("PlayerConnectionValidateLoginEvent");
    }

    @EventHandler
    public void testEvent(PlayerConnectionInitialConfigureEvent event) {
        ModObserverPlugin.LOGGER.info("------------------------------------------------------");
        ModObserverPlugin.LOGGER.info("PlayerConnectionInitialConfigureEvent");
    }

    @EventHandler
    public void testEvent(PlayerServerFullCheckEvent event) {
        ModObserverPlugin.LOGGER.info("------------------------------------------------------");
        ModObserverPlugin.LOGGER.info("PlayerServerFullCheckEvent");
    }*/

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

    /*
      ------------------------------------------------------
      PlayerHandshakeEvent
      ------------------------------------------------------
      AsyncPlayerPreLoginEvent
      r404 is d1fadcd5-f8ee-389b-ae76-2112e34d591e
      ------------------------------------------------------
      PlayerServerFullCheckEvent
      ------------------------------------------------------
      PlayerConnectionValidateLoginEvent
      ------------------------------------------------------
      PlayerConnectionInitialConfigureEvent
      RECEIVED WITH CONNECTION
      ------------------------------------------------------
      PlayerServerFullCheckEvent
      ------------------------------------------------------
      PlayerConnectionValidateLoginEvent
      ------------------------------------------------------
      PlayerJoinEvent
       game
      :62643] logged in with entity id 98 at ([world]-76.84721942411383, 67.0, -22.17944021409161)
      ------------------------------------------------------
      PlayerRegisterChannelEvent
      fabric:attachment_sync_v1
      ------------------------------------------------------
      PlayerChannelEvent
      fabric:attachment_sync_v1
      ------------------------------------------------------
      PlayerRegisterChannelEvent
      fabric-screen-handler-api-v1:open_screen
      ------------------------------------------------------
      PlayerChannelEvent
      fabric-screen-handler-api-v1:open_screen
    */
}
