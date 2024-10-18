package io.hyonsu06.core.annotations.items.items;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to define various statistics associated with an item.
 * <p>
 * This annotation allows items to have multiple attributes that affect gameplay,
 * such as damage, health, and speed. Each parameter represents a specific stat
 * that can be used in item mechanics.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemStats {
    /**
     * The base damage of the item.
     *
     * @return double representing the item's damage
     */
    double damage() default 0;

    /**
     * The base multiplier of base damage.
     *
     * @return double representing the item's strength
     */
    double strength() default 0;

    /**
     * The chance of landing a critical hit.
     *
     * @return double representing the item's critical chance
     */
    double critChance() default 0;

    /**
     * The damage multiplier when a critical hit occurs.
     *
     * @return double representing the item's critical damage
     */
    double critDamage() default 0;

    /**
     * The ferocity attribute, influencing more damage trigger(s).
     * <p>
     * This stat capped at 400.
     *
     * @return double representing the item's ferocity
     */
    double ferocity() default 0;

    /**
     * The attack speed of the item.
     * <p>
     * Every melee attack has seconds of cooldown for the same enemy, and this reduces
     * this cooldown by tick per 10 points.
     * <p>
     * This stat capped at 100.
     * <p>
     * For shortbows, this affects as cooldown between each shot.
     *
     * @return double representing the item's attack speed
     */
    double attackSpeed() default 0;

    /**
     * The extra health provided by the item.
     *
     * @return double representing the item's health
     */
    double health() default 0;

    /**
     * The extra defense provided by the item.
     *
     * @return double representing the item's defense
     */
    double defense() default 0;

    /**
     * The extra speed attribute affecting movement speed.
     * <p>
     * This stat capped at 500.
     *
     * @return double representing the item's speed
     */
    double speed() default 0;

    /**
     * The intelligence attribute, often affecting magic power.
     *
     * @return double representing the item's intelligence
     */
    double intelligence() default 0;

    /**
     * The agility attribute influencing dodging.
     * <p>
     * Every point grants a 1 % chance to reduce damage by 0.5%.
     * <p>
     * This stat capped at 100.
     *
     * @return double representing the item's agility
     */
    double agility() default 0;

    /**
     * The rate of health regeneration.
     * <p>
     * For example, 200 points grants double healing.
     *
     * @return double representing the item's health regeneration
     */
    double healthRegen() default 0;

    /**
     * The rate of mana regeneration.
     * <p>
     * For example, 300 points grants tripled mana regen.
     *
     * @return double representing the item's mana regeneration
     */
    double manaRegen() default 0;

    /**
     * The luck attribute affecting loot and other random outcomes.
     *
     * @return double representing the item's luck
     */
    double luck() default 0;
}
