package io.hyonsu06.command.accessories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static io.hyonsu06.command.accessories.AccessoriesUtils.accessories;
import static io.hyonsu06.command.items.AllItemsPageInstance.getPage;

public class ShowAccessoriesGUI {
    public static void open(Player p) {
        // Previous Page Icon
        ItemStack prevPage = Bukkit.getItemFactory().createItemStack("minecraft:arrow");
        ItemMeta prevPageMeta = prevPage.getItemMeta();
        prevPageMeta.setDisplayName(ChatColor.WHITE + "Previous page");
        prevPageMeta.setLore(List.of(ChatColor.DARK_GRAY + "Click this to go previous page."));
        prevPage.setItemMeta(prevPageMeta);

        // Next Page Icon
        ItemStack nextPage = Bukkit.getItemFactory().createItemStack("minecraft:arrow");
        ItemMeta nextPageMeta = nextPage.getItemMeta();
        nextPageMeta.setDisplayName(ChatColor.WHITE + "Next page");
        nextPageMeta.setLore(List.of(ChatColor.DARK_GRAY + "Click this to go next page."));
        nextPage.setItemMeta(nextPageMeta);

        // Close Icon
        ItemStack close = Bukkit.getItemFactory().createItemStack("minecraft:barrier");
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Close page");
        closeMeta.setLore(List.of(ChatColor.DARK_GRAY + "Click this to close page."));
        close.setItemMeta(closeMeta);

        // Fills
        ItemStack pane = Bukkit.getItemFactory().createItemStack("minecraft:black_stained_glass_pane");
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName(" ");
        pane.setItemMeta(paneMeta);

        // Actual Inventory GUI
        Inventory inv = Bukkit.getServer().createInventory(null, 54, "Accessory bag");
        inv.setItem(0, prevPage);
        inv.setItem(1, pane);
        inv.setItem(2, pane);
        inv.setItem(3, pane);
        inv.setItem(4, close);
        inv.setItem(5, pane);
        inv.setItem(6, pane);
        inv.setItem(7, pane);
        inv.setItem(8, nextPage);
        for (int i = 1; i < 45; i++) {
            int index = i + (getPage(p) * 45) - 1;
            try {
                inv.setItem(i + 8, accessories.get(p.getUniqueId())[index]);
            } catch (IndexOutOfBoundsException e) {
                inv.setItem(i + 8, Bukkit.getItemFactory().createItemStack("minecraft:air"));
            }
        }
        p.openInventory(inv);
    }
}
