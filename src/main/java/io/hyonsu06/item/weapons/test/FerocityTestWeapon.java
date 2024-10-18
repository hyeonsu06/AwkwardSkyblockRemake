package io.hyonsu06.item.weapons.test;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import org.bukkit.Material;

@ItemTagged
@ItemMetadata(
        ID = "ferocity_test_weapon",
        name = "Ferocity Test Sword",
        rarity = ItemRarity.SPECIAL,
        type = ItemType.SWORD,
        material = Material.IRON_SWORD,
        durability = 5
)
@ItemStats(
        damage = 50,
        critChance = 50,
        critDamage = 50,
        ferocity = 199
)
public class FerocityTestWeapon {}
