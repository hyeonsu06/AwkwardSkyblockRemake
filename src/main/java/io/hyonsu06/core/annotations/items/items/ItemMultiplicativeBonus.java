package io.hyonsu06.core.annotations.items.items;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to define additive bonus of various statistics associated with an item.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemMultiplicativeBonus {
    double mul_damage() default 1;
    double mul_strength() default 1;
    double mul_critChance() default 1;
    double mul_critDamage() default 1;

    double mul_ferocity() default 1;
    double mul_attackSpeed() default 1;

    double mul_health() default 1;
    double mul_defense() default 1;
    double mul_speed() default 1;
    double mul_intelligence() default 1;
    double mul_agility() default 1;

    double mul_healthRegen() default 1;
    double mul_manaRegen() default 1;

    double mul_luck() default 1;
}
