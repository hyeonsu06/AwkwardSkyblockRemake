package io.hyonsu06.core.managers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

import static io.hyonsu06.core.functions.NumberTweaks.shortNumber;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;
import static io.hyonsu06.core.functions.setImmuneTime.setNoDamageTicks;

public class VanillaEntityManager implements Listener {
    private static Map<Location, Boolean> split = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHit1(EntityDamageEvent e) {
        EntityType type = e.getEntity().getType();
        if (type.equals(EntityType.WITHER) || type.equals(EntityType.ENDER_DRAGON) || type.equals(EntityType.GUARDIAN) || type.equals(EntityType.ELDER_GUARDIAN) || type.equals(EntityType.WARDEN)) {
            setNoDamageTicks((LivingEntity) e.getEntity(), 0);
        }
        if (type.equals(EntityType.PLAYER)) {
            setNoDamageTicks((LivingEntity) e.getEntity(), 10);
        }
        if (e.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER)) {
            setNoDamageTicks((LivingEntity) e.getEntity(), 0);
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
                e.setDamage(e.getDamage() / 100);
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

    @EventHandler
    public void slimeSplit(SlimeSplitEvent event) {
        if (event.getEntity().getPersistentDataContainer().has(getPDC("ominous"), PersistentDataType.BOOLEAN)) {
            split.put(event.getEntity().getLocation(), true);
        }
    }
}