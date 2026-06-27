package eu.endermite.censura.notification;

import eu.endermite.censura.Censura;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StaffNotification {
    private final Set<Player> staffNotify;

    public StaffNotification(Censura plugin) {
        staffNotify = ConcurrentHashMap.newKeySet();
        refreshStaffCache();

        plugin.getServer().getPluginManager().registerEvents(new NotificationListener(this), plugin);
    }

    public void addStaff(Player player) {
        staffNotify.add(player);
    }

    public void removeStaff(Player player) {
        staffNotify.remove(player);
    }

    public void clearStaff() {
        staffNotify.clear();
    }

    public boolean isStaffNotified(Player player) {
        return staffNotify.contains(player);
    }

    public void refreshStaffCache() {
        staffNotify.clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("censura.notifications")) {
                staffNotify.add(player);
            }
        }
    }

    public void sendNotification(String message) {
        if (!Censura.getCachedConfig().shouldNotifyDetections()) return;

        for (Player player : staffNotify) {
            player.sendMessage(message);
        }
    }

}
