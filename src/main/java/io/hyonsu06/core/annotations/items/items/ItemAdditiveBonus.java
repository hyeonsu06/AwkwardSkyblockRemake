package io.hyonsu06.core.annotations.items.items;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to define additive bonus of various statistics associated with an item.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemAdditiveBonus {
    double add_damage() default 0;
    double add_strength() default 0;
    double add_critChance() default 0;
    double add_critDamage() default 0;

    double add_ferocity() default 0;
    double add_attackSpeed() default 0;

    double add_health() default 0;
    double add_defense() default 0;
    double add_speed() default 0;
    double add_intelligence() default 0;
    double add_agility() default 0;

    double add_healthRegen() default 0;
    double add_manaRegen() default 0;

    double add_swingRange() default 0;

    double add_luck() default 0;
}
