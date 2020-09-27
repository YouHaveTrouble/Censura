package eu.endermite.censura.listener;

import eu.endermite.censura.Censura;
import eu.endermite.censura.Filter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.AsyncPlayerPreLoginEvent event) {
        String name = event.getPlayerProfile().getName();
        if (Filter.filterNoActions(name)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Censura.getCachedConfig().getKickBadName());
            event.setKickMessage(Censura.getCachedConfig().getKickBadName());
        }
    }

}
