package io.hyonsu06.core.enums;

import lombok.Getter;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Enum representing different types of items.
 * <p>
 * Each item type has an associated display name and an indication of
 * whether it can be reforged.
 */
@Getter
public enum ItemType {
    NONE(false, "", null),

    // Weapons
    SWORD(true, "Sword", EquipmentSlot.HAND),

    // Armor
    HELMET(true, "Helmet", EquipmentSlot.HEAD),
    CHESTPLATE(true, "Chestplate", EquipmentSlot.CHEST),
    LEGGINGS(true, "Leggings", EquipmentSlot.LEGS),
    BOOTS(true, "Boots", EquipmentSlot.FEET),

    // Ranged Weapons
    ARROW(false, "Arrow", null),
    BOW(true, "Bow", EquipmentSlot.HAND),
    SHORTBOW(true, "Shortbow", EquipmentSlot.HAND),

    // Tools
    PICKAXE(true, "Pickaxe", EquipmentSlot.HAND),
    DRILL(true, "Drill", EquipmentSlot.HAND),
    SHOVEL(true, "Shovel", EquipmentSlot.HAND),
    AXE(true, "Axe", EquipmentSlot.HAND),
    HOE(true, "Hoe", EquipmentSlot.HAND),

    // Miscellaneous
    FISHING_ROD(true, "Fishing Rod", EquipmentSlot.HAND),
    PET(false, "Pet", null),
    POTION(false, "Potion", null),
    RUNE(false, "Rune", null),

    ACCESSORY(false, "Accessory", null),;

    private final boolean reforgable;
    private final String display;
    private final EquipmentSlot slot;

    ItemType(boolean reforgable, String display, EquipmentSlot slot) {
        this.reforgable = reforgable;
        this.display = display;
        this.slot = slot;
    }
}
