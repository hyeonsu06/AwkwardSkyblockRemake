package io.hyonsu06.item.items.armors.FancyTuxedo;

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
        ID = "fancy_tuxedo",
        name = "Fancy Tuxedo",
        rarity = ItemRarity.MYTHIC,
        type = ItemType.CHESTPLATE,
        material = Material.LEATHER_CHESTPLATE,
        durability = 500
)
@ItemStats(
        defense = 25,
        speed = 35
)
@ItemAdditiveBonus(
        add_speed = 200
)
@ItemMultiplicativeBonus(
        mul_damage = 1.5
)
public class FancyTuxedo {}
