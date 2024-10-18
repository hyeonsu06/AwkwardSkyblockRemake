package io.hyonsu06.core;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import static io.hyonsu06.core.functions.NumberTweaks.shortNumber;
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
        if (type.equals(EntityType.WITHER) || type.equals(EntityType.ENDER_DRAGON)) {
            Entity entity = e.getEntity();
            LivingEntity victim = (LivingEntity) entity;
            EntityType type2 = e.getEntity().getType();
            if (type2.equals(EntityType.WITHER) || type2.equals(EntityType.ENDER_DRAGON)) {
                String name = victim.getName();
                if (victim.getCustomName() != null) name = victim.getCustomName();
                if (victim.getHealth() / victim.getMaxHealth() > 0.5) {
                    ((Boss) victim).getBossBar().setTitle(name + " " + ChatColor.GREEN + shortNumber(victim.getHealth()) + ChatColor.GRAY + "/" + ChatColor.GREEN + shortNumber(victim.getMaxHealth()) + ChatColor.RED + "❤");
                } else {
                    ((Boss) victim).getBossBar().setTitle(name + " " + ChatColor.YELLOW + shortNumber(victim.getHealth()) + ChatColor.GRAY + "/" + ChatColor.GREEN + shortNumber(victim.getMaxHealth()) + ChatColor.RED + "❤");
                }
            }
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
        if (type.equals(EntityType.WITHER) || type.equals(EntityType.ENDER_DRAGON)) {
            Entity entity = e.getEntity();
            LivingEntity victim = (LivingEntity) entity;
            EntityType type2 = e.getEntity().getType();
            if (type2.equals(EntityType.WITHER) || type2.equals(EntityType.ENDER_DRAGON)) {
                String name = victim.getName();
                if (victim.getCustomName() != null) name = victim.getCustomName();
                if (victim.getHealth() / victim.getMaxHealth() > 0.5) {
                    ((Boss) victim).getBossBar().setTitle(name + " " + ChatColor.GREEN + shortNumber(victim.getHealth()) + ChatColor.GRAY + "/" + ChatColor.GREEN + shortNumber(victim.getMaxHealth()) + ChatColor.RED + "❤");
                } else {
                    ((Boss) victim).getBossBar().setTitle(name + " " + ChatColor.YELLOW + shortNumber(victim.getHealth()) + ChatColor.GRAY + "/" + ChatColor.GREEN + shortNumber(victim.getMaxHealth()) + ChatColor.RED + "❤");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage2(EntityDamageEvent e) {
        if (e.getEntity() instanceof LivingEntity victim) {
            EntityType type = e.getEntity().getType();
            if (type.equals(EntityType.WITHER) || type.equals(EntityType.ENDER_DRAGON)) {
                String name = victim.getName();
                if (victim.getCustomName() != null) name = victim.getCustomName();
                if (victim.getHealth() / victim.getMaxHealth() > 0.5) {
                    ((Boss) victim).getBossBar().setTitle(name + " " + ChatColor.GREEN + shortNumber(victim.getHealth()) + ChatColor.GRAY + "/" + ChatColor.GREEN + shortNumber(victim.getMaxHealth()) + ChatColor.RED + "❤");
                } else {
                    ((Boss) victim).getBossBar().setTitle(name + " " + ChatColor.YELLOW + shortNumber(victim.getHealth()) + ChatColor.GRAY + "/" + ChatColor.GREEN + shortNumber(victim.getMaxHealth()) + ChatColor.RED + "❤");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onSpawn(EntitySpawnEvent e) {
        EntityType type = e.getEntity().getType();
        if (type.equals(EntityType.WITHER)) {
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200000);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(2);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(112000);
        }
        if (type.equals(EntityType.ENDER_DRAGON)) {
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10000000);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(2);
        }
        if (type.equals(EntityType.ELDER_GUARDIAN)) {
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50000);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(2);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(75000);
        }
        if (type.equals(EntityType.WARDEN)) {
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(750000000);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(19);
            ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(800000);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawnHighest(EntitySpawnEvent e) {
        EntityType type = e.getEntityType();
        if (type.equals(EntityType.WITHER) || type.equals(EntityType.ENDER_DRAGON)) {
            Entity entity = e.getEntity();
            LivingEntity victim = (LivingEntity) entity;
            EntityType type2 = e.getEntity().getType();
            if (type2.equals(EntityType.WITHER) || type2.equals(EntityType.ENDER_DRAGON)) {
                String name = victim.getName();
                if (victim.getCustomName() != null) name = victim.getCustomName();
                if (victim.getHealth() / victim.getMaxHealth() > 0.5) {
                    ((Boss) victim).getBossBar().setTitle(name + " " + ChatColor.GREEN + shortNumber(victim.getHealth()) + ChatColor.GRAY + "/" + ChatColor.GREEN + shortNumber(victim.getMaxHealth()) + ChatColor.RED + "❤");
                } else {
                    ((Boss) victim).getBossBar().setTitle(name + " " + ChatColor.YELLOW + shortNumber(victim.getHealth()) + ChatColor.GRAY + "/" + ChatColor.GREEN + shortNumber(victim.getMaxHealth()) + ChatColor.RED + "❤");
                }
            }
        }
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        EntityType type = e.getEntityType();
        if (type.equals(EntityType.WITHER) || type.equals(EntityType.ENDER_DRAGON)) {
            Entity entity = e.getEntity();
            LivingEntity victim = (LivingEntity) entity;
            EntityType type2 = e.getEntity().getType();
            if (type2.equals(EntityType.WITHER) || type2.equals(EntityType.ENDER_DRAGON)) {
                String name = victim.getName();
                if (victim.getCustomName() != null) name = victim.getCustomName();
                if (victim.getHealth() / victim.getMaxHealth() > 0.5) {
                    ((Boss) victim).getBossBar().setTitle(name + " " + ChatColor.GREEN + shortNumber(victim.getHealth()) + ChatColor.GRAY + "/" + ChatColor.GREEN + shortNumber(victim.getMaxHealth()) + ChatColor.RED + "❤");
                } else {
                    ((Boss) victim).getBossBar().setTitle(name + " " + ChatColor.YELLOW + shortNumber(victim.getHealth()) + ChatColor.GRAY + "/" + ChatColor.GREEN + shortNumber(victim.getMaxHealth()) + ChatColor.RED + "❤");
                }
            }
        }}
}