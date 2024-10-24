package io.hyonsu06.core.managers.crafting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;

public class CraftingGUI {
    private static Inventory inv3x3 = null;
    private static Inventory inv5x5 = null;

    public static Inventory get3x3Inventory() {
        if (inv3x3 == null) {
            inv3x3 = Bukkit.createInventory(null, 54, "Crafting Table");

            ItemStack fill = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta1 = fill.getItemMeta();
            meta1.getPersistentDataContainer().set(getPDC("no"), PersistentDataType.BOOLEAN, true);
            meta1.setHideTooltip(true);
            fill.setItemMeta(meta1);

            ItemStack validator = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta2 = validator.getItemMeta();
            meta2.getPersistentDataContainer().set(getPDC("no"), PersistentDataType.BOOLEAN, true);
            meta2.setHideTooltip(true);
            validator.setItemMeta(meta2);

            ItemStack close = new ItemStack(Material.BARRIER);
            ItemMeta meta3 = close.getItemMeta();
            meta3.getPersistentDataContainer().set(getPDC("no"), PersistentDataType.BOOLEAN, true);
            meta3.setDisplayName(ChatColor.RED + "Close");
            close.setItemMeta(meta3);

            ItemStack change = new ItemStack(Material.CRAFTING_TABLE);
            ItemMeta meta4 = change.getItemMeta();
            meta4.getPersistentDataContainer().set(getPDC("no"), PersistentDataType.BOOLEAN, true);
            meta4.setDisplayName(ChatColor.GREEN + "Change to 5x5 Grid");
            change.setItemMeta(meta4);

            ItemStack resultItem = new ItemStack(Material.BARRIER);
            ItemMeta meta5 = resultItem.getItemMeta();
            meta5.getPersistentDataContainer().set(getPDC("no"), PersistentDataType.BOOLEAN, true);
            meta5.setHideTooltip(true);
            resultItem.setItemMeta(meta5);

            for (int i = 0; i < inv3x3.getSize(); i++) inv3x3.setItem(i, fill);
            for (int i : new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30, 25}) inv3x3.setItem(i, new ItemStack(Material.AIR));
            for (int i = 45; i < 54; i++) if (i != 49) inv3x3.setItem(i, validator);
            inv3x3.setItem(25, resultItem);
            inv3x3.setItem(43, change);
            inv3x3.setItem(49, close);
        }
        return inv3x3;
    }

    public static Inventory get5x5Inventory() {
        if (inv5x5 == null) {
            inv5x5 = Bukkit.createInventory(null, 54, "5x5 Crafting Table");

            ItemStack fill = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta1 = fill.getItemMeta();
            meta1.getPersistentDataContainer().set(getPDC("no"), PersistentDataType.BOOLEAN, true);
            meta1.setHideTooltip(true);
            fill.setItemMeta(meta1);

            ItemStack validator = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta2 = validator.getItemMeta();
            meta2.getPersistentDataContainer().set(getPDC("no"), PersistentDataType.BOOLEAN, true);
            meta2.setHideTooltip(true);
            validator.setItemMeta(meta2);

            ItemStack close = new ItemStack(Material.BARRIER);
            ItemMeta meta3 = close.getItemMeta();
            meta3.getPersistentDataContainer().set(getPDC("no"), PersistentDataType.BOOLEAN, true);
            meta3.setDisplayName(ChatColor.RED + "Close");
            close.setItemMeta(meta3);

            ItemStack change = new ItemStack(Material.CRAFTING_TABLE);
            ItemMeta meta4 = change.getItemMeta();
            meta4.getPersistentDataContainer().set(getPDC("no"), PersistentDataType.BOOLEAN, true);
            meta4.setDisplayName(ChatColor.GREEN + "Back to 3x3 Grid");
            change.setItemMeta(meta4);

            ItemStack resultItem = new ItemStack(Material.BARRIER);
            ItemMeta meta5 = resultItem.getItemMeta();
            meta5.getPersistentDataContainer().set(getPDC("no"), PersistentDataType.BOOLEAN, true);
            meta5.setHideTooltip(true);
            resultItem.setItemMeta(meta5);

            for (int i = 0; i < inv5x5.getSize(); i++) inv5x5.setItem(i, fill);
            for (int i : new int[]{0, 1, 2, 3, 4, 9, 10, 11, 12, 13, 18, 19, 20, 21, 22, 27, 28, 29, 30, 31, 36, 37, 38, 39, 40, 25}) inv5x5.setItem(i, new ItemStack(Material.AIR));
            for (int i = 45; i < 54; i++) inv5x5.setItem(i, validator);
            inv3x3.setItem(25, resultItem);
            inv5x5.setItem(43, change);
            inv5x5.setItem(49, close);
        }
        return inv5x5;
    }
}
