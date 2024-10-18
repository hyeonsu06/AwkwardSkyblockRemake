package io.hyonsu06.item.accessories;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import org.bukkit.Material;

@ItemTagged
@ItemMetadata(
        ID = "test_accessory",
        name = "Test Accessory",
        rarity = ItemRarity.ADMIN,
        type = ItemType.ACCESSORY,
        material = Material.NETHERITE_INGOT
)
@ItemStats(
        damage = 1,
        strength = 1,
        critChance = 1,
        critDamage = 1,
        ferocity = 1,
        attackSpeed = 1,
        health = 1,
        defense = 1,
        speed = 1,
        intelligence = 1,
        agility = 1,
        healthRegen = 1,
        manaRegen = 1,
        luck = 1
)
public class TestAccessory {
}
