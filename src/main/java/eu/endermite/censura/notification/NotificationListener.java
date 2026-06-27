package eu.endermite.censura.notification;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NotificationListener implements Listener {
    private final StaffNotification staffNotification;

    public NotificationListener(StaffNotification staffNotification) {
        this.staffNotification = staffNotification;
    }

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("censura.notifications")) {
            staffNotification.addStaff(event.getPlayer());
        }
    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent event) {
        staffNotification.removeStaff(event.getPlayer());
    }
}
