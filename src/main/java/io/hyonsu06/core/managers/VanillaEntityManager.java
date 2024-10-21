package io.hyonsu06.core.managers;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

import static io.hyonsu06.core.functions.NumberTweaks.shortNumber;
import static io.hyonsu06.core.functions.setImmuneTime.setNoDamageTicks;

public class VanillaEntityManager implements Listener {
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

    public static void modifyData(EntitySpawnEvent e) {
        if (e.getEntity() instanceof LivingEntity){
            if (
                    e.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL) ||
                    e.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER) ||
                    e.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER) ||
                    e.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.POTION_EFFECT) ||
                    e.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.SLIME_SPLIT)
            ) {
                EntityType type = e.getEntity().getType();
                if (type.equals(EntityType.WITHER)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10000000);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(2);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(30000);
                }
                if (type.equals(EntityType.WARDEN)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200000000000000d);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(19);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(400000);
                }
                if (type.equals(EntityType.ENDERMITE)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(50);
                }
                if (type.equals(EntityType.PIGLIN) || type.equals(EntityType.ZOMBIFIED_PIGLIN)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(600);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1000);
                }
                if (type.equals(EntityType.PIGLIN_BRUTE)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(800);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(5000);
                }
                if (type.equals(EntityType.HOGLIN) || type.equals(EntityType.ZOGLIN)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(800);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1500);
                }
                if (type.equals(EntityType.GHAST)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
                }
                if (type.equals(EntityType.MAGMA_CUBE)) {
                    if (((MagmaCube) e.getEntity()).getSize() == 4) {
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(500);
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(400);
                    } else if (((MagmaCube) e.getEntity()).getSize() == 3) {
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(300);
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(200);
                    } else if (((MagmaCube) e.getEntity()).getSize() == 2) {
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(150);
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(10);
                    } else if (((MagmaCube) e.getEntity()).getSize() == 1) {
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(50);
                    }
                }
                if (type.equals(EntityType.STRIDER)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(9);
                }
                if (type.equals(EntityType.WITHER_SKELETON)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1000);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(600);
                }
                if (type.equals(EntityType.BLAZE)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(700);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(400);
                }
                if (type.equals(EntityType.SHULKER)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(8000);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(99);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(6000);
                }
                if (type.equals(EntityType.ENDER_DRAGON)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2000000000);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(2);
                }
                if (type.equals(EntityType.GUARDIAN)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2500000);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(2);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(25000);
                }
                if (type.equals(EntityType.ELDER_GUARDIAN)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2500000);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(2);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(25000);
                }
                if (type.equals(EntityType.ZOMBIE) || type.equals(EntityType.HUSK) || type.equals(EntityType.DROWNED) || type.equals(EntityType.ZOMBIE_VILLAGER)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(25);
                }
                if (type.equals(EntityType.SKELETON) || type.equals(EntityType.STRAY) || type.equals(EntityType.BOGGED)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(50);
                }
                if (type.equals(EntityType.CREEPER)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(100);
                }
                if (type.equals(EntityType.SPIDER) || type.equals(EntityType.CAVE_SPIDER)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(80);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(30);
                }
                if (type.equals(EntityType.ENDERMAN)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(300);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(50);
                }
                if (type.equals(EntityType.PHANTOM)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(15);
                }
                if (type.equals(EntityType.SLIME)) {
                    if (((Slime) e.getEntity()).getSize() == 4) {
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(250);
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(40);
                    } else if (((Slime) e.getEntity()).getSize() == 3) {
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(120);
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(20);
                    } else if (((Slime) e.getEntity()).getSize() == 2) {
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60);
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(10);
                    } else if (((Slime) e.getEntity()).getSize() == 1) {
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(5);
                    }
                }
                if (type.equals(EntityType.WOLF)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(50);
                }
                if (type.equals(EntityType.FOX)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(100);
                }
                if (type.equals(EntityType.LLAMA) || type.equals(EntityType.TRADER_LLAMA)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(120);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(70);
                }
                if (type.equals(EntityType.PANDA)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(250);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(200);
                }
                if (type.equals(EntityType.BEE)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(500);
                }
                if (type.equals(EntityType.HORSE) || type.equals(EntityType.SKELETON_HORSE) || type.equals(EntityType.DONKEY) || type.equals(EntityType.MULE)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(150);
                }
                if (type.equals(EntityType.GOAT)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(90);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(100);
                }
                if (type.equals(EntityType.COW) || type.equals(EntityType.MOOSHROOM) || type.equals(EntityType.SHEEP) || type.equals(EntityType.PIG)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
                }
                if (type.equals(EntityType.SQUID) || type.equals(EntityType.GLOW_SQUID)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(70);
                }
                if (type.equals(EntityType.TROPICAL_FISH)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                }
                if (type.equals(EntityType.CHICKEN) || type.equals(EntityType.RABBIT)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
                }
                if (type.equals(EntityType.POLAR_BEAR)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(70);
                }
                if (type.equals(EntityType.VILLAGER) || type.equals(EntityType.WANDERING_TRADER)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                }
                if (type.equals(EntityType.CAT) || type.equals(EntityType.OCELOT)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60);
                }
                if (type.equals(EntityType.VINDICATOR)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(150);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(125);
                }
                if (type.equals(EntityType.EVOKER)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(120);
                }
                if (type.equals(EntityType.ILLUSIONER)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(110);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(100);
                }
                if (type.equals(EntityType.VEX)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(40);
                }
                if (type.equals(EntityType.SILVERFISH)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(30);
                }
                if (type.equals(EntityType.BAT)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                }
                if (type.equals(EntityType.WITCH)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(666);
                }
                if (type.equals(EntityType.IRON_GOLEM)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(300);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(1);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(125);
                }
                if (type.equals(EntityType.SNOW_GOLEM)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(80);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(30);
                }
                if (type.equals(EntityType.TURTLE)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(9);
                }
                if (type.equals(EntityType.COD) || type.equals(EntityType.SALMON)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                }
                if (type.equals(EntityType.DOLPHIN) || type.equals(EntityType.ALLAY)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                }
                if (type.equals(EntityType.AXOLOTL)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(70);
                }
                if (type.equals(EntityType.FROG)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
                }
                if (type.equals(EntityType.TADPOLE)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(15);
                }
                if (type.equals(EntityType.CAMEL)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(150);
                }
                if (type.equals(EntityType.SNIFFER)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(250);
                }
                if (type.equals(EntityType.BREEZE)) {
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(150);
                    ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(100);
                }
                if (type.equals(EntityType.ENDERMAN)) {
                    if (e.getLocation().getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(500);
                        ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(50);
                    }
                }
            }
        }
    }
}