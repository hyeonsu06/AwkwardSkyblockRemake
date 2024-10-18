package io.hyonsu06.command.accessories;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AccessoriesUtils {
    @Getter @Setter
    public static Map<UUID, ItemStack[]> accessories = new HashMap<>();

    // Save the player's inventory as a string in the PersistentDataContainer
    public static void saveInventory(Player p) {
        if (p.getOpenInventory().getTopInventory().getHolder() == null && p.getOpenInventory().getTitle().equalsIgnoreCase("Accessory bag")) {
            Inventory inventory = p.getOpenInventory().getTopInventory();
            ItemStack[] items = new ItemStack[]{};

            // Serialize the inventory contents
            for (int i = 9; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null) {
                    items = addItemStack(items, item);
                }
            }
            accessories.put(p.getUniqueId(), items);
        }
    }

    private static ItemStack[] addItemStack(ItemStack[] originalArray, ItemStack newItemStack) {
        // Create a new array with one additional slot
        ItemStack[] newArray = new ItemStack[originalArray.length + 1];

        // Copy the original array into the new array
        System.arraycopy(originalArray, 0, newArray, 0, originalArray.length);

        // Add the new ItemStack to the last position of the new array
        newArray[originalArray.length] = newItemStack;

        return newArray;
    }
}
