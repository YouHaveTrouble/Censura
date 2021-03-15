package eu.endermite.censura.listener;

import eu.endermite.censura.filter.Filter;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class EntityRenameListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityRename(org.bukkit.event.player.PlayerInteractEntityEvent event) {

        ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();

        if (handItem.getType() != Material.NAME_TAG)
            return;

        if (Filter.filter(handItem.getItemMeta().getDisplayName(), event.getPlayer()))
            event.setCancelled(true);

    }

}
