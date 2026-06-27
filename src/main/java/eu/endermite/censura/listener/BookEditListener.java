package eu.endermite.censura.listener;

import eu.endermite.censura.Censura;
import eu.endermite.censura.filter.Filter;
import eu.endermite.censura.notification.CheckType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.BookMeta;

public class BookEditListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBookEdit(org.bukkit.event.player.PlayerEditBookEvent event) {
        if (event.getPreviousBookMeta() == event.getNewBookMeta()) return;
        BookMeta bookMeta = event.getNewBookMeta();
        Player suspect = event.getPlayer();

        try {
            for (String page : bookMeta.getPages()) {

                if (!Filter.preFilter(page)) {
                    event.getPlayer().sendMessage(Censura.getCachedConfig().getPrefilterFailed());
                    event.setCancelled(true);
                }
                if (Filter.filter(page, suspect, CheckType.BOOK)) {
                    event.setCancelled(true);
                    return;
                }
            }
        } catch (NullPointerException ignored) {}
        if (!event.isSigning()) return;
        if (!Filter.preFilter(event.getNewBookMeta().getTitle())) {
            event.getPlayer().sendMessage(Censura.getCachedConfig().getPrefilterFailed());
            event.setCancelled(true);
            return;
        }
        if (Filter.filter(event.getNewBookMeta().getTitle(), suspect, CheckType.BOOK))
            event.setCancelled(true);
    }
}
