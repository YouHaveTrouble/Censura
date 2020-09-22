package eu.endermite.censura.listener;

import eu.endermite.censura.Filter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


public class SignChangeListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSignChangeEvent(org.bukkit.event.block.SignChangeEvent event) {
        Player player = event.getPlayer();

        for (String line : event.getLines()) {
            if (Filter.filter(line, player)) {
                event.setCancelled(true);
                return;
            }
        }
    }

}
