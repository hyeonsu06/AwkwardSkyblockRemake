package io.hyonsu06.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Enum representing different types of damage.
 * <p>
 * Each damage type is associated with a specific {@link EntityDamageEvent.DamageCause}.
 */
@AllArgsConstructor
@Getter
public enum DamageType {
    /**
     * Represents generic physical damage.
     * <p>
     * This includes melee attacks or ranged attacks from entities.
     */
    GENERIC(EntityDamageEvent.DamageCause.ENTITY_ATTACK),

    /**
     * Represents magical damage.
     * <p>
     * This includes damage from spells or other magic-based effects.
     */
    MAGIC(EntityDamageEvent.DamageCause.MAGIC),

    /**
     * Represents true damage.
     * <p>
     * This type of damage ignores all defense or resistance.
     */
    TRUE(EntityDamageEvent.DamageCause.VOID);

    private final EntityDamageEvent.DamageCause cause;
}
