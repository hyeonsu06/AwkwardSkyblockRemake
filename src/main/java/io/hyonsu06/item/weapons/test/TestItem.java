package io.hyonsu06.item.weapons.test;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.items.items.ItemAdditiveBonus;
import io.hyonsu06.core.annotations.items.items.ItemMultiplicativeBonus;
import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import io.hyonsu06.item.skills.test.*;
import org.bukkit.Material;

@ItemTagged
@ItemMetadata(
        ID = "test_item",
        name = "Test Item",
        rarity = ItemRarity.ADMIN,
        type = ItemType.NONE,
        material = Material.DIAMOND,
        skills = {
                TestSkill1.class,
                TestSkill2.class,
                TestSkill3.class,
                TestSkill4.class,
                TestSkill5.class,
                TestSkill6.class,
                TestSkill7.class
        }
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
        luck = 1
)
@ItemAdditiveBonus(
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
        luck = 1
)
@ItemMultiplicativeBonus(
        damage = 2,
        strength = 2,
        critChance = 2,
        critDamage = 2,
        ferocity = 2,
        attackSpeed = 2,
        health = 2,
        defense = 2,
        speed = 2,
        intelligence = 2,
        agility = 2,
        healthRegen = 2,
        manaRegen = 2,
        luck = 0.5)
public class TestItem {
}
