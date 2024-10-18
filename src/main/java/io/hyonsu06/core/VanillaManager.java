package io.hyonsu06.core;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.projectiles.BlockProjectileSource;

import static io.hyonsu06.core.Refresher.addSkillDescription;
import static io.hyonsu06.core.functions.setImmuneTime.setNoDamageTicks;

public class VanillaManager implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onHit(EntityDamageEvent e) {
        EntityType type = e.getEntity().getType();
        if (type.equals(EntityType.WITHER) || type.equals(EntityType.ENDER_DRAGON) || type.equals(EntityType.ELDER_GUARDIAN) || type.equals(EntityType.WARDEN)) {
            setNoDamageTicks((LivingEntity) e.getEntity(), 0);
        }
        if (type.equals(EntityType.PLAYER)) {
            setNoDamageTicks((LivingEntity) e.getEntity(), 5);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage1(EntityDamageByEntityEvent e) {
        EntityType type = e.getEntity().getType();
        if (type.equals(EntityType.WITHER) || type.equals(EntityType.ENDER_DRAGON) || type.equals(EntityType.ELDER_GUARDIAN) || type.equals(EntityType.WARDEN) || type.equals(EntityType.PLAYER)) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.MAGIC)) {
                e.setDamage(e.getDamage() / 200);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage2(EntityDamageByEntityEvent e) {
        LivingEntity attacker = (LivingEntity) e.getDamager();
        LivingEntity victim = (LivingEntity) e.getEntity();
        if (attacker instanceof Warden) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.SONIC_BOOM)) {
                e.setDamage(1_000_000_000_000d);
                if (victim instanceof Player) victim.sendMessage(String.valueOf(addSkillDescription("Warden's Sonic Boom hitting your for {0} damage.", Integer.MAX_VALUE, new long[]{(long) e.getDamage()}, ChatColor.GRAY, ChatColor.RED)));
            }
        }
        if (attacker instanceof EnderDragon) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.DRAGON_BREATH))
                e.setDamage(1_000_000d);
            if (victim instanceof Player) victim.sendMessage(String.valueOf(addSkillDescription("Ender Dragon's Acid Breath hitting your for {0} damage.", Integer.MAX_VALUE, new long[]{(long) e.getDamage()}, ChatColor.GRAY, ChatColor.RED)));
        }
        if (attacker instanceof ElderGuardian) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.DRAGON_BREATH)) {
                e.setDamage(1_000_000d);
                if (victim instanceof Player)
                    victim.sendMessage(String.valueOf(addSkillDescription("Elder Guardian's Thorns hitting your for {0} damage.", Integer.MAX_VALUE, new long[]{(long) e.getDamage()}, ChatColor.GRAY, ChatColor.RED)));
            }
        }
        if (attacker instanceof Fireball) {
            if (((Fireball) attacker).getShooter() instanceof Ghast) {
                e.setDamage(250_000d);
                if (victim instanceof Player)
                    victim.sendMessage(String.valueOf(addSkillDescription("Ghast's Fireball hitting your for {0} damage.", Integer.MAX_VALUE, new long[]{(long) e.getDamage()}, ChatColor.GRAY, ChatColor.RED)));
            }
        }
        if (attacker instanceof Arrow) {
            if (((Arrow) attacker).getShooter() instanceof BlockProjectileSource) {
                e.setDamage(5_000_000d);
                if (victim instanceof Player)
                    victim.sendMessage(String.valueOf(addSkillDescription("Hidden Arrow Trap hit you for {0} damage!", Integer.MAX_VALUE, new long[]{(long) e.getDamage()}, ChatColor.GRAY, ChatColor.RED)));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onSpawn(EntitySpawnEvent e) {
        EntityType type = e.getEntity().getType();
        if (type.equals(EntityType.WITHER)) {
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200000);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(3);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(112000);
        }
        if (type.equals(EntityType.ENDER_DRAGON)) {
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10000000);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(9);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(400000);
        }
        if (type.equals(EntityType.ELDER_GUARDIAN)) {
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50000);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(1);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(75000);
        }
        if (type.equals(EntityType.WARDEN)) {
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20000000);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(19);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(800000);
        }
    }
}
