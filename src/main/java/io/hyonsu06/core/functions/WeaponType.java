package io.hyonsu06.core.functions;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;

public class WeaponType {
     public static EquipmentSlot itemTypeFromItemStack(ItemStack i) {
         if (i == null) return EquipmentSlot.OFF_HAND;
         String s = i.getPersistentDataContainer().get(getPDC("type"), PersistentDataType.STRING);
         if (s == null) return EquipmentSlot.OFF_HAND;
         return switch (s.toLowerCase()) {
            case "sword",
                 "bow", "shortbow",
                 "pickaxe", "shovel", "hoe", "axe", "drill",
                 "fishing_rod",
                 "" -> EquipmentSlot.HAND;
            case "helmet" -> EquipmentSlot.HEAD;
            case "chestplate" -> EquipmentSlot.BODY;
            case "leggings" -> EquipmentSlot.LEGS;
            case "boots" -> EquipmentSlot.FEET;
            case "accessory",
                 "potion", "rune", "pet" -> EquipmentSlot.OFF_HAND;
            default -> throw new IllegalStateException("Unexpected value: " + s.toLowerCase());
        };
    }
}
