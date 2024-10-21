package io.hyonsu06.item.items.test;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import org.bukkit.Material;

@ItemTagged
@ItemMetadata(
        ID = "critical_test_weapon",
        name = "Critical Hit Test Sword",
        rarity = ItemRarity.SPECIAL,
        type = ItemType.SWORD,
        material = Material.IRON_SWORD,
        durability = 5
)
@ItemStats(
        damage = 50,
        critChance = 100,
        critDamage = 1000
)
public class CriticalTestWeapon {}
