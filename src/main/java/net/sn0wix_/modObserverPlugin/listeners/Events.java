package net.sn0wix_.modObserverPlugin.listeners;

import io.papermc.paper.event.connection.PlayerConnectionValidateLoginEvent;
import net.sn0wix_.modObserverPlugin.config.Config;
import net.sn0wix_.modObserverPlugin.utils.Connections;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            Connections.add(event.getConnection(), event.getPlayerProfile().getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConnectionValidate(PlayerConnectionValidateLoginEvent event) {
        if (event.isAllowed() && Connections.get(event.getConnection()).canBeChecked()) {
            Connections.Connection connection = Connections.get(event.getConnection());

            if (!connection.isApproved()) {
                if (!connection.hasSentPacket()) {
                    event.kickMessage(Config.getModObserverRequiredMessage());
                    Connections.remove(event.getConnection());
                } else {
                    //event.kickMessage(connection.getKickMessage());
                    //Connections.remove(event.getConnection());
                }
            }
        } else if (!event.isAllowed()) {
            Connections.remove(event.getConnection());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Connections.update();
    }


      /*------------------------------------------------------
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
