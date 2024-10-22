package io.hyonsu06.item.items.weapons.melee.WitherSword;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import io.hyonsu06.item.skills.WitherSword.WitherDamageBonus;
import io.hyonsu06.item.skills.WitherSword.WitherRage;
import org.bukkit.Material;

@ItemTagged
@ItemMetadata(
        ID = "scylla",
        name = "Scylla",
        description = "Through we don't know who made this, wither used this sword must be kinda hot-tempered.",
        rarity = ItemRarity.LEGENDARY,
        type = ItemType.SWORD,
        material = Material.IRON_SWORD,
        durability = 1000000,
        skills = {
                WitherDamageBonus.class,
                WitherRage.class
        }
)
@ItemStats(
        damage = 275,
        strength = 70,
        critDamage = 150,
        intelligence = 50,
        ferocity = 100
)
public class Scylla {}
