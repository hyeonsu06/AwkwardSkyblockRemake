package io.hyonsu06.item.items.vanilla.weapons.melee;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import org.bukkit.Material;

@ItemTagged
@ItemMetadata(
        ID = "wooden_sword",
        name = "Wooden Sword",
        rarity = ItemRarity.COMMON,
        type = ItemType.SWORD,
        material = Material.WOODEN_SWORD
)
@ItemStats(
        damage = 20
)
public class WoodenSword {
}
