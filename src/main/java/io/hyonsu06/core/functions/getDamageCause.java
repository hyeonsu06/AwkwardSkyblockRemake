package io.hyonsu06.core.functions;

import io.hyonsu06.core.enums.DamageType;

public class getDamageCause {
    public static org.bukkit.damage.DamageType getCause(DamageType type) {
        return switch (type) {
            case GENERIC -> org.bukkit.damage.DamageType.MOB_ATTACK;
            case MAGIC -> org.bukkit.damage.DamageType.MAGIC;
            case TRUE -> org.bukkit.damage.DamageType.OUT_OF_WORLD;
        };
    }
}
