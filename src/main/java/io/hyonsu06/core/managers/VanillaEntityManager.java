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
import static org.bukkit.Bukkit.getLogger;

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

    public static void initStat(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity e) {
            if (
                    e.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL) ||
                            e.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.BUILD_WITHER) ||
                            e.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN) ||
                            e.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM) ||
                            e.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER) ||
                            e.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER) ||
                            e.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.POTION_EFFECT) ||
                            e.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.SLIME_SPLIT)
            ) {
                EntityType type = e.getType();
                e.getPersistentDataContainer().set(getPDC("natural"), PersistentDataType.BOOLEAN, true);

                if (type.equals(EntityType.WITHER)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(500000);
                    e.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(1);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1000);
                }
                if (type.equals(EntityType.WARDEN)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2000000000d);
                    e.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(199);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(4000000);
                }
                if (type.equals(EntityType.ENDERMITE)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(50);
                }
                if (type.equals(EntityType.PIGLIN) || type.equals(EntityType.ZOMBIFIED_PIGLIN)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(600);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1000);
                }
                if (type.equals(EntityType.PIGLIN_BRUTE)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(800);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(5000);
                }
                if (type.equals(EntityType.HOGLIN) || type.equals(EntityType.ZOGLIN)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(800);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1500);
                }
                if (type.equals(EntityType.GHAST)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
                }
                if (type.equals(EntityType.MAGMA_CUBE)) {
                    if (((MagmaCube) e).getSize() == 4) {
                        e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(500);
                        e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(400);
                    } else if (((MagmaCube) e).getSize() == 3) {
                        e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(300);
                        e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(200);
                    } else if (((MagmaCube) e).getSize() == 2) {
                        e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(150);
                        e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(10);
                    } else if (((MagmaCube) e).getSize() == 1) {
                        e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                        e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(50);
                    }
                }
                if (type.equals(EntityType.STRIDER)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
                    e.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(19);
                }
                if (type.equals(EntityType.WITHER_SKELETON)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1000);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(600);
                }
                if (type.equals(EntityType.BLAZE)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(700);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(400);
                }
                if (type.equals(EntityType.SHULKER)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(8000);
                    e.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(99);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(6000);
                }
                if (type.equals(EntityType.ENDER_DRAGON)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2000000000);
                    e.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(2);
                }
                if (type.equals(EntityType.GUARDIAN)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2500000);
                    e.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(2);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(25000);
                }
                if (type.equals(EntityType.ELDER_GUARDIAN)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2500000);
                    e.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(2);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(25000);
                }
                if (type.equals(EntityType.ZOMBIE) || type.equals(EntityType.HUSK) || type.equals(EntityType.DROWNED) || type.equals(EntityType.ZOMBIE_VILLAGER)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                    e.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(25);
                }
                if (type.equals(EntityType.SKELETON) || type.equals(EntityType.STRAY) || type.equals(EntityType.BOGGED)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(50);
                }
                if (type.equals(EntityType.CREEPER)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(100);
                }
                if (type.equals(EntityType.SPIDER) || type.equals(EntityType.CAVE_SPIDER)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(80);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(30);
                }
                if (type.equals(EntityType.ENDERMAN)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(300);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(50);
                }
                if (type.equals(EntityType.PHANTOM)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(15);
                }
                if (type.equals(EntityType.SLIME)) {
                    if (((Slime) e).getSize() == 4) {
                        e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(250);
                        e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(40);
                    } else if (((Slime) e).getSize() == 3) {
                        e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(120);
                        e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(20);
                    } else if (((Slime) e).getSize() == 2) {
                        e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60);
                        e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(10);
                    } else if (((Slime) e).getSize() == 1) {
                        e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                        e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(5);
                    }
                }
                if (type.equals(EntityType.WOLF)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(50);
                }
                if (type.equals(EntityType.FOX)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(100);
                }
                if (type.equals(EntityType.LLAMA) || type.equals(EntityType.TRADER_LLAMA)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(120);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(70);
                }
                if (type.equals(EntityType.PANDA)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(250);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(200);
                }
                if (type.equals(EntityType.BEE)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(500);
                }
                if (type.equals(EntityType.HORSE) || type.equals(EntityType.SKELETON_HORSE) || type.equals(EntityType.DONKEY) || type.equals(EntityType.MULE)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(150);
                }
                if (type.equals(EntityType.GOAT)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(90);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(100);
                }
                if (type.equals(EntityType.COW) || type.equals(EntityType.MOOSHROOM) || type.equals(EntityType.SHEEP) || type.equals(EntityType.PIG)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
                }
                if (type.equals(EntityType.SQUID) || type.equals(EntityType.GLOW_SQUID)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(70);
                }
                if (type.equals(EntityType.TROPICAL_FISH)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                }
                if (type.equals(EntityType.CHICKEN) || type.equals(EntityType.RABBIT)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
                }
                if (type.equals(EntityType.POLAR_BEAR)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(70);
                }
                if (type.equals(EntityType.VILLAGER) || type.equals(EntityType.WANDERING_TRADER)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                }
                if (type.equals(EntityType.CAT) || type.equals(EntityType.OCELOT)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60);
                }
                if (type.equals(EntityType.VINDICATOR)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(150);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(125);
                }
                if (type.equals(EntityType.EVOKER)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(120);
                }
                if (type.equals(EntityType.ILLUSIONER)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(110);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(100);
                }
                if (type.equals(EntityType.VEX)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(40);
                }
                if (type.equals(EntityType.SILVERFISH)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(30);
                }
                if (type.equals(EntityType.BAT)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                }
                if (type.equals(EntityType.WITCH)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(666);
                }
                if (type.equals(EntityType.IRON_GOLEM)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(300);
                    e.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(1);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(125);
                }
                if (type.equals(EntityType.SNOW_GOLEM)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(80);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(30);
                }
                if (type.equals(EntityType.TURTLE)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
                    e.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(9);
                }
                if (type.equals(EntityType.COD) || type.equals(EntityType.SALMON)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                }
                if (type.equals(EntityType.DOLPHIN) || type.equals(EntityType.ALLAY)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                }
                if (type.equals(EntityType.AXOLOTL)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(70);
                }
                if (type.equals(EntityType.FROG)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
                }
                if (type.equals(EntityType.TADPOLE)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(15);
                }
                if (type.equals(EntityType.CAMEL)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(150);
                }
                if (type.equals(EntityType.SNIFFER)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(250);
                }
                if (type.equals(EntityType.BREEZE)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(150);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(100);
                }
                if (type.equals(EntityType.ENDERMAN)) {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(500);
                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(50);
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