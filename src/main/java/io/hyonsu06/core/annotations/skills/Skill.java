package io.hyonsu06.core.annotations.skills;

import io.hyonsu06.core.enums.DamageType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to define the properties of a skill in the game.
 * <p>
 * This annotation is used to specify various attributes associated with
 * skills, including their ID, name, description, cooldown, mana cost,
 * damage type, and additional arguments. It allows for a flexible way
 * to manage and retrieve skill data.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Skill {
    /**
     * Defines ID of skill.
     * Used for applying de-stackable to stat-granting abilities.
     *
     * @return String representing the skill's ID
     */
    String ID();

    /**
     * Defines the name of the skill.
     *
     * @return String representing the skill's name
     */
    String name() default "";

    /**
     * Defines the description of the skill.
     * Can have multiple lines.
     *
     * @return String representing the skill's description
     */
    String description();

    /**
     * Defines the cooldown period after casting the skill, in ticks.
     * Note: 20 ticks = 1 second.
     *
     * @return int representing the cooldown duration
     */
    int cooldown() default 0;

    /**
     * Defines the mana cost required to cast the skill.
     *
     * @return int representing the mana cost
     */
    int cost() default 0;

    /**
     * Defines the type of damage dealt by the skill.
     * <p>
     * For example, if damage is dealt as magic, it will only be affected by
     * magic defense, not generic defense.
     * <p>
     * True Damage (DamageType.TRUE) will ignore all defense stats.
     *
     * @return DamageType enum representing the damage type (GENERIC, MAGIC, TRUE)
     */
    DamageType damageType() default DamageType.GENERIC;

    /**
     * Defines various arguments required to form skills.
     * The first argument is typically used for damage.
     *
     * @return int[] representing the skill's arguments
     */
    long[] args() default {};

    /**
     * Defines whether the skill uses a shortened description.
     * If set to true, only the description() will be displayed in item lore.
     * <p>
     * This can be used in the following examples:
     * <p>
     * Deal +50% Damage to Withers.
     *
     * @return boolean indicating whether to use a shortened description
     */
    boolean simpleDescription() default false;
}
