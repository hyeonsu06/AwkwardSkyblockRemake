package io.hyonsu06.core.interfaces;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Interface defining methods for skill actions.
 * This class is template, which must be used for each skill trigger.
 */
public interface SkillMethods {

    /**
     * Triggered when the player right-clicks.
     *
     * @param player The player performing the action.
     */
    default void onRightClick(Player player) {
    }

    /**
     * Triggered when the player left-clicks.
     *
     * @param player The player performing the action.
     */
    default void onLeftClick(Player player) {
    }

    /**
     * Triggered when the player right-clicks while sneaking.
     *
     * @param player The player performing the action.
     */
    default void onSneakRightClick(Player player) {
        if (player.isSneaking()) {}
    }

    /**
     * Triggered when the player left-clicks while sneaking.
     *
     * @param player The player performing the action.
     */
    default void onSneakLeftClick(Player player) {
        if (player.isSneaking()) {}
    }

    /**
     * Triggered when the player sneaks.
     *
     * @param player The player performing the action.
     */
    default void onSneak(Player player) {
        if (player.isSneaking()) {}
    }

    /**
     * Triggered when the player jumps.
     *
     * @param player The player performing the action.
     */
    default void onJump(Player player) {
    }

    /**
     * Triggered when the player hits an entity.
     *
     * @param player The player performing the action.
     * @param target The entity that is being hit.
     *
     * @return double representing modified damage. returning -1 means no modification made.
     */
    default double onHit(Player player, Entity target, double damage) {
        return -1;
    }

    /**
     * Applies passive skills every tick.
     *
     * @param player The player to which the passive skills are applied.
     */
    default void passive(Player player) {
    }
}
