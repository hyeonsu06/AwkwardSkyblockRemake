package io.hyonsu06.core.annotations.items.items;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to define additive bonus of various statistics associated with an item.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemAdditiveBonus {
    double damage() default 0;
    double strength() default 0;
    double critChance() default 0;
    double critDamage() default 0;

    double ferocity() default 0;
    double attackSpeed() default 0;

    double health() default 0;
    double defense() default 0;
    double speed() default 0;
    double intelligence() default 0;
    double agility() default 0;

    double healthRegen() default 0;
    double manaRegen() default 0;

    double luck() default 0;
}
