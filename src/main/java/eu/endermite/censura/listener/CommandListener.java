package eu.endermite.censura.listener;

import eu.endermite.censura.Censura;
import eu.endermite.censura.filter.Filter;
import eu.endermite.censura.notification.CheckType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteractEvent(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase();
        for (String cmd : Censura.getCachedConfig().getCommandsToFilter()) {
            if (msg.startsWith("/" + cmd + " ")) {
                if (!Filter.preFilter(msg)) {
                    event.getPlayer().sendMessage(Censura.getCachedConfig().getPrefilterFailed());
                    event.setCancelled(true);
                    return;
                }
                if (Filter.filter(msg, event.getPlayer(), CheckType.COMMAND)) {
                    event.setCancelled(true);
                    return;
                }

            }
        }
    }
}
