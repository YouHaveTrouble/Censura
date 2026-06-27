package eu.endermite.censura.listener;

import eu.endermite.censura.Censura;
import eu.endermite.censura.filter.Filter;
import eu.endermite.censura.notification.CheckType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChatEvent(AsyncPlayerChatEvent event) {
        Player suspect = event.getPlayer();

        if (!Filter.preFilter(event.getMessage())) {
            suspect.sendMessage(Censura.getCachedConfig().getPrefilterFailed());
            event.setCancelled(true);
            return;
        }
        if (Filter.filter(event.getMessage(), suspect, CheckType.CHAT))
            event.setCancelled(true);
    }

}
