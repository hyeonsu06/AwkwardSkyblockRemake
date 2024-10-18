package io.hyonsu06.item.weapons.ranged;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.items.items.ItemMultiplicativeBonus;
import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import io.hyonsu06.item.skills.InstantShoot;
import io.hyonsu06.item.skills.Salvation;
import org.bukkit.Material;

@ItemTagged
@ItemMetadata(
        ID = "terminator",
        name = "Terminator",
        rarity = ItemRarity.LEGENDARY,
        type = ItemType.SHORTBOW,
        material = Material.BOW,
        durability = 300000,
        skills = {
                InstantShoot.class,
                Salvation.class
        }
)
@ItemStats(
        damage = 420,
        strength = 30,
        attackSpeed = 50
)
@ItemMultiplicativeBonus(
        critChance = 0.25
)
public class Terminator {
}
