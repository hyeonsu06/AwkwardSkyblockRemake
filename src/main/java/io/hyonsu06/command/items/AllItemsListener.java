package io.hyonsu06.command.items;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;

import static io.hyonsu06.command.items.AllItemsPageInstance.nextPage;
import static io.hyonsu06.command.items.AllItemsPageInstance.previousPage;
import static io.hyonsu06.command.items.ShowAllItemsGUI.open;

public class AllItemsListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (
                e.getView().getTopInventory().getHolder() == null &&
                e.getView().getTitle().equalsIgnoreCase("All Items") &&
                !(e.getClickedInventory() instanceof PlayerInventory)
        ) {
            if (e.getSlot() == 0) {
                previousPage((Player) e.getWhoClicked());
                return;
            }
            if (e.getSlot() == 8) {
                nextPage((Player) e.getWhoClicked());
                return;
            }
            if (e.getSlot() == 4) {
                e.getWhoClicked().closeInventory();
                return;
            }
            if (e.getSlot() == 1 || e.getSlot() == 2 || e.getSlot() == 3 || e.getSlot() == 5 || e.getSlot() == 6 || e.getSlot() == 7) {
                e.setCancelled(true);
                return;
            }
            if (e.getCurrentItem() != null) e.getWhoClicked().getInventory().addItem(e.getCurrentItem());
            open((Player) e.getWhoClicked());
        }
    }
}
