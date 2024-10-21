package io.hyonsu06.item.items.test;

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
        damage = 10,
        strength = 10,
        critChance = 10,
        critDamage = 10,
        ferocity = 10,
        attackSpeed = 10,
        health = 10,
        defense = 10,
        speed = 10,
        intelligence = 10,
        agility = 10,
        healthRegen = 10,
        manaRegen = 10,
        luck = 10
)
@ItemAdditiveBonus(
        add_damage = 20,
        add_strength = 20,
        add_critChance = 20,
        add_critDamage = 20,
        add_ferocity = 20,
        add_attackSpeed = 20,
        add_health = 20,
        add_defense = 20,
        add_speed = 20,
        add_intelligence = 20,
        add_agility = 20,
        add_healthRegen = 20,
        add_manaRegen = 20,
        add_luck = 20
)
@ItemMultiplicativeBonus(
        mul_damage = 2,
        mul_strength = 2,
        mul_critChance = 2,
        mul_critDamage = 2,
        mul_ferocity = 2,
        mul_attackSpeed = 2,
        mul_health = 2,
        mul_defense = 2,
        mul_speed = 2,
        mul_intelligence = 2,
        mul_agility = 2,
        mul_healthRegen = 2,
        mul_manaRegen = 2,
        mul_luck = 0.5)
public class TestItem {
}
