package io.hyonsu06.core.annotations.items.items;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to define additive bonus of various statistics associated with an item.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemMultiplicativeBonus {
    double damage() default 1;
    double strength() default 1;
    double critChance() default 1;
    double critDamage() default 1;

    double ferocity() default 1;
    double attackSpeed() default 1;

    double health() default 1;
    double defense() default 1;
    double speed() default 1;
    double intelligence() default 1;
    double agility() default 1;

    double healthRegen() default 1;
    double manaRegen() default 1;

    double luck() default 1;
}
