package eu.endermite.censura.listener;

import eu.endermite.censura.Censura;
import eu.endermite.censura.filter.Filter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;

public class ItemRenameListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteractEvent(org.bukkit.event.inventory.InventoryClickEvent event) {

        Inventory inv = event.getClickedInventory();

        if (inv == null) return;

        if (!inv.getType().equals(InventoryType.ANVIL)) return;

        if (event.getSlot() != 2) return;

        AnvilInventory anvil = (AnvilInventory) inv;
        Player player = (Player) event.getWhoClicked();
        if (anvil.getRenameText() == null) return;

        if (!Filter.preFilter(anvil.getRenameText())) {
            event.getWhoClicked().sendMessage(Censura.getCachedConfig().getPrefilterFailed());
            event.setCancelled(true);
            return;
        }

        if (Filter.filter(anvil.getRenameText(), player))
            event.setCancelled(true);


    }

}
