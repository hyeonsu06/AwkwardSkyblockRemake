package io.hyonsu06.core;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import static io.hyonsu06.core.functions.setImmuneTime.setNoDamageTicks;

public class VanillaManager implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onHit(EntityDamageEvent e) {
        EntityType type = e.getEntity().getType();
        if (type.equals(EntityType.WITHER) || type.equals(EntityType.ENDER_DRAGON) || type.equals(EntityType.ELDER_GUARDIAN) || type.equals(EntityType.WARDEN)) {
            setNoDamageTicks((LivingEntity) e.getEntity(), 0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSpawn(EntitySpawnEvent e) {
        EntityType type = e.getEntity().getType();
        if (type.equals(EntityType.WITHER)) {
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200000);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(3);
        }
        if (type.equals(EntityType.ENDER_DRAGON)) {
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(500000);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(9);
        }
        if (type.equals(EntityType.ELDER_GUARDIAN)) {
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50000);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(1);
        }
        if (type.equals(EntityType.WARDEN)) {
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20000000);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(999);
        }
    }
}
