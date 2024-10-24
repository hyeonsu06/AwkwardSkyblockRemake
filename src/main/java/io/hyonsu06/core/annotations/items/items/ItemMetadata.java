package io.hyonsu06.core.annotations.items.items;

import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import io.hyonsu06.core.interfaces.SkillMethods;
import org.bukkit.Material;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to define metadata for items in the game.
 * This includes properties like ID, name, description, rarity, type, material, and skills.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemMetadata {
    /**
     * Define the unique ID of the item.
     *
     * @return String representing the item's ID
     */
    String ID();

    /**
     * Define the display name of the item.
     *
     * @return String representing the item's display name
     */
    String name();

    /**
     * Define lore for the item. This can have multiple lines.
     *
     * @return String[] representing the item's lore
     */
    String description() default "";

    /**
     * Define the rarity of the item.
     * <p>
     * This currently does nothing but will affect reforge stats later.
     * Rarer items receive more bonuses.
     *
     * @return ItemRarity representing the item's rarity
     */
    ItemRarity rarity();

    /**
     * Define the type of the item.
     * <p>
     * This currently does nothing but will influence the game later
     * For reforging, determining which can be applied.
     *
     * @return ItemType representing the item's type
     */
    ItemType type();

    /**
     * Define the display material for the item using Bukkit Material.
     *
     * @return Material representing the item's visual representation
     */
    Material material();

    /**
     * This value is only for when material is player head.
     * Defines texture of item.
     *
     * @return String representing texture's URL
     */
    String texture() default "";

    /**
     * This value is only for when material is able to dye.
     * Defines red from RGB.
     *
     * @return int representing red of RGB
     */
    int red() default 0;

    /**
     * This value is only for when material is able to dye.
     * Defines green from RGB.
     *
     * @return int representing red of RGB
     */
    int green() default 0;

    /**
     * This value is only for when material is able to dye.
     * Defines blue from RGB.
     *
     * @return int representing blue of RGB
     */
    int blue() default 0;

    /**
     * Defines durability of item.
     * Long.MIN_VALUE means unbreakable.
     *
     * @return int representing durability
     */
    long durability() default Long.MIN_VALUE;

    /**
     * Defines how much item can be stacked.
     * DISCLAIMER: WILL NOT WORK OVER 99
     *
     * @return int representing max items stackable
     */
    int stack() default 1;

    /**
     * Defines whether item has glint without enchantments.
     *
     * @return boolean representing can have glint
     */
    boolean hasGlow() default false;

    /**
     * Define the skill classes associated with the item.
     *
     * @return Class<? extends SkillMethods>[]
     * representing the item's skills
     */
    Class<? extends SkillMethods>[] skills() default {};
}
