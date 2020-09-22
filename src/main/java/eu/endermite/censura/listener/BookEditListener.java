package eu.endermite.censura.listener;

import eu.endermite.censura.Filter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.BookMeta;

public class BookEditListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChatEvent(org.bukkit.event.player.PlayerEditBookEvent event) {

        if (event.getPreviousBookMeta() == event.getNewBookMeta()) {
            return;
        }

        BookMeta bookMeta = event.getNewBookMeta();

        try {
            for (String page : bookMeta.getPages()) {
                if (Filter.filter(page, event.getPlayer())) {
                    event.setCancelled(true);
                }
            }
        } catch (NullPointerException ignored) {}

        if (event.isSigning()) {
            if (Filter.filter(event.getNewBookMeta().getTitle(), event.getPlayer())) {
                event.setCancelled(true);
            }
        }

    }

}
