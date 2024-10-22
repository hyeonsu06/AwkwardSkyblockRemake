package io.hyonsu06.item.items.weapons.melee.WitherSword;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import io.hyonsu06.item.skills.WitherSword.WitherDamageBonus;
import io.hyonsu06.item.skills.WitherSword.WitherImpact;
import org.bukkit.Material;

@ItemTagged
@ItemMetadata(
        ID = "hyperion",
        name = "Hyperion",
        description = "This ancient sword is forged by... who it was?",
        rarity = ItemRarity.LEGENDARY,
        type = ItemType.SWORD,
        material = Material.IRON_SWORD,
        durability = 1000000,
        skills = {
                WitherDamageBonus.class,
                WitherImpact.class
        }
)
@ItemStats(
        damage = 200,
        strength = 100,
        intelligence = 400
)
public class Hyperion {}
