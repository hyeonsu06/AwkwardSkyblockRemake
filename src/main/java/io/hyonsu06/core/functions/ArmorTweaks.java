package io.hyonsu06.core.functions;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class ArmorTweaks {
    public static ItemStack[] getItems(LivingEntity e) {
        ItemStack[] items = new ItemStack[5];
        try {
            items[0] = e.getEquipment().getHelmet();
        } catch (Exception ignored) {}
        try {
            items[1] = e.getEquipment().getChestplate();
        } catch (Exception ignored) {}
        try {
            items[2] = e.getEquipment().getLeggings();
        } catch (Exception ignored) {}
        try {
            items[3] = e.getEquipment().getBoots();
        } catch (Exception ignored) {}
        try {
            items[4] = e.getEquipment().getItemInMainHand();
        } catch (Exception ignored) {}

        return items;
    }
    public static boolean isArmor(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false; // Null or air item is not armor
        }

        // Check if the item is an armor type
        return switch (item.getType()) {
            case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, CHAINMAIL_HELMET,
                 CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS, IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS,
                 IRON_BOOTS, GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS, DIAMOND_HELMET,
                 DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS, NETHERITE_HELMET, NETHERITE_CHESTPLATE,
                 NETHERITE_LEGGINGS, NETHERITE_BOOTS -> true; // It's armor
            default -> false; // Not armor
        };
    }
}
