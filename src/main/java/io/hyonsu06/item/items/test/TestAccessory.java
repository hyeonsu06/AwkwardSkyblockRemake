package io.hyonsu06.item.items.test;

import io.hyonsu06.core.annotations.items.items.ItemAdditiveBonus;
import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemMultiplicativeBonus;
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
        material = Material.EMERALD
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
        swingRange = 1,
        luck = 1
)
@ItemAdditiveBonus(
        add_damage = 1,
        add_strength = 1,
        add_critChance = 1,
        add_critDamage = 1,
        add_ferocity = 1,
        add_attackSpeed = 1,
        add_health = 1,
        add_defense = 1,
        add_speed = 1,
        add_intelligence = 1,
        add_agility = 1,
        add_healthRegen = 1,
        add_manaRegen = 1,
        add_swingRange = 1,
        add_luck = 1
)
@ItemMultiplicativeBonus(
        mul_damage = 2,
        mul_strength = 2,
        mul_critChance = 2,
        mul_critDamage = 2,
        mul_ferocity = 2,
        mul_attackSpeed = 2,
        mul_health = 2,
        mul_defense = 2,
        mul_speed = 2,
        mul_intelligence = 2,
        mul_agility = 2,
        mul_healthRegen = 2,
        mul_manaRegen = 2,
        mul_swingRange = 2,
        mul_luck = 0.5)
public class TestAccessory {
}
