package io.hyonsu06.item.items.armors.PureDiamondArmor;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.items.items.ItemAdditiveBonus;
import io.hyonsu06.core.annotations.items.items.ItemMultiplicativeBonus;
import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import org.bukkit.Material;

@ItemTagged
@ItemMetadata(
        ID = "pure_boots",
        name = "Pure Diamond Boots",
        rarity = ItemRarity.LEGENDARY,
        type = ItemType.BOOTS,
        material = Material.DIAMOND_BOOTS,
        durability = 1200000
)
@ItemStats(
        defense = 150
)
@ItemAdditiveBonus(
        add_health = 50
)
@ItemMultiplicativeBonus
public class PureDiamondBoots {
}