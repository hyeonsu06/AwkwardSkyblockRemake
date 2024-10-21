package io.hyonsu06.item.items.weapons.melee;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemMultiplicativeBonus;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import org.bukkit.Material;

@ItemTagged
@ItemMetadata(
        ID = "evil_claymore",
        name = "Evil Claymore",
        description = "Too heavy to hold.",
        rarity = ItemRarity.MYTHIC,
        type = ItemType.SWORD,
        material = Material.DIAMOND_SWORD,
        durability = 100000000
)
@ItemStats(
        damage = 1000,
        strength = 50,
        critDamage = 500,
        attackSpeed = 100,
        luck = 5
)
@ItemMultiplicativeBonus(
        mul_strength = 1.5
)
public class EvilClaymore {}
