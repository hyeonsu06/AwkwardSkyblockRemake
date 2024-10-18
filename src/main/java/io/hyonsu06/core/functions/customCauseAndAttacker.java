package io.hyonsu06.core.functions;

import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;

public class customCauseAndAttacker {
    public static DamageSource causeAndAttacker(DamageType type, Entity attacker, Entity victim) {
        return DamageSource.builder(type).withCausingEntity(attacker).withDirectEntity(victim).build();
    }
}
