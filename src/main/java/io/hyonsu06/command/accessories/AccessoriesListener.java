package io.hyonsu06.command.accessories;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static io.hyonsu06.command.accessories.AccessoriesUtils.saveInventory;
import static io.hyonsu06.command.accessories.AccessoriesPageInstance.nextPage;
import static io.hyonsu06.command.accessories.AccessoriesPageInstance.previousPage;
import static io.hyonsu06.command.accessories.ShowAccessoriesGUI.open;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;

public class AccessoriesListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (
                e.getView().getTopInventory().getHolder() == null &&
                        e.getView().getTitle().equalsIgnoreCase("Accessory bag") &&
                        !(e.getClickedInventory() instanceof PlayerInventory)
        ) {
            if (e.getSlot() == 0) {
                previousPage((Player) e.getWhoClicked());
                open((Player) e.getWhoClicked());
                return;
            }
            if (e.getSlot() == 8) {
                nextPage((Player) e.getWhoClicked());
                open((Player) e.getWhoClicked());
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
        }
        if (
                e.getView().getTopInventory().getHolder() == null &&
                        e.getView().getTitle().equalsIgnoreCase("Accessory bag")
        ) {
            if (containsItemWithId(e.getView().getTopInventory(), e.getCurrentItem())) {
                if (!e.getClickedInventory().equals(e.getWhoClicked().getInventory())) return;
                e.setCancelled(true);
                e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot put same accessory!");
                return;
            }
            saveInventory((Player) e.getWhoClicked());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        saveInventory((Player) e.getPlayer());
    }

    private boolean containsItemWithId(Inventory inventory, ItemStack targetItem) {
        if (targetItem == null) return false;
        ItemMeta targetMeta = targetItem.getItemMeta();
        if (targetMeta == null) return false;

        // Retrieve the custom ID from the target item's PDC
        String targetId = targetMeta.getPersistentDataContainer().get(getPDC("id"), PersistentDataType.STRING);

        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            // Check if the current item's PDC contains the same 'id'
            String itemId = meta.getPersistentDataContainer().get(getPDC("id"), PersistentDataType.STRING);
            if (targetId != null && targetId.equals(itemId)) {
                return true; // Matching item with the same ID found
            }
        }
        return false; // No matching item found
    }
}
