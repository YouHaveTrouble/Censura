package eu.endermite.censura.listener;

import eu.endermite.censura.Censura;
import eu.endermite.censura.Filter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CommandListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteractEvent(org.bukkit.event.player.PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase();
        for (String cmd : Censura.getCachedConfig().getCommandsToFilter()) {
            if (msg.startsWith("/" + cmd + " ") && Filter.filter(msg, event.getPlayer()))
                event.setCancelled(true);
        }
    }
}

