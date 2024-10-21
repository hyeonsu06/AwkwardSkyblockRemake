package io.hyonsu06.core.managers;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import io.hyonsu06.command.accessories.AccessoriesUtils;
import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.enums.Stats;
import io.papermc.paper.event.entity.EntityDamageItemEvent;
import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.TrialSpawner;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

import static io.hyonsu06.Main.*;
import static io.hyonsu06.core.Refresher.findClassById;
import static io.hyonsu06.core.functions.NumberTweaks.*;
import static io.hyonsu06.core.functions.customCauseAndAttacker.causeAndAttacker;
import static io.hyonsu06.core.functions.getClasses.getSkillClasses;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getItemID;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;
import static io.hyonsu06.core.functions.setImmuneTime.setNoDamageTicks;
import static io.hyonsu06.core.managers.VanillaEntityManager.modifyData;
import static org.bukkit.Bukkit.*;

public class EntityManager implements Listener {
    @Getter @Setter
    private static Map<UUID, Double> playerIntelligence = new HashMap<>();
    @Getter
    public static Map<UUID, Integer> playerTaskMap = new HashMap<>();
    @Getter @Setter
    private static Map<UUID, Integer> meleeHits = new HashMap<>();
    @Getter @Setter
    private static Map<UUID, Integer> rangedHits = new HashMap<>();

    final int FEROCITY_DELAY = 3;

    public EntityManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Players
                for (Player p : getOnlinePlayers()) {
                    for (UUID uuid : StatManager.getFinalStatMap().keySet()) {
                        Map<UUID, Map<Stats, Double>> allStatMap = StatManager.getFinalStatMap();
                        if (p.getUniqueId().equals(uuid)) {
                            Map<Stats, Double> statMap = allStatMap.get(p.getUniqueId());
                            double health = statMap.get(Stats.HEALTH);
                            double defense = statMap.get(Stats.DEFENSE);
                            double speed = statMap.get(Stats.SPEED);
                            double currentIntelligence;
                            if (Objects.isNull(playerIntelligence.get(p.getUniqueId()))) playerIntelligence.put(p.getUniqueId(), 100d);
                            currentIntelligence = playerIntelligence.get(p.getUniqueId());
                            double maxIntelligence = statMap.get(Stats.INTELLIGENCE);

                            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
                            p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed / 1000);

                            StringBuilder message = new StringBuilder();

                            message.append(ChatColor.RED).append(numberFormat(p.getHealth())).append("/").append(numberFormat(health)).append(" HP ❤    ");
                            if (defense != 0) message.append(ChatColor.GREEN).append(numberFormat(defense)).append(" Defense ❈    ");
                            message.append(ChatColor.AQUA).append(numberFormat(currentIntelligence)).append("/").append(numberFormat(maxIntelligence)).append(" Intelligence ✎");

                            p.sendActionBar(message.toString());
                        }
                    }
                }

                // Entities
                for (World w : getWorlds()) for (LivingEntity e : w.getLivingEntities()) {
                    if (!e.getType().equals(EntityType.PLAYER)) {
                        Map<UUID, Map<Stats, Double>> statMap = StatManager.getFinalStatMap();
                        for (UUID uuid : statMap.keySet()) {
                            if (statMap.containsKey(e.getUniqueId())) {
                                if (e.getUniqueId().equals(uuid)) {
                                    Map<Stats, Double> entityStatMap = statMap.get(e.getUniqueId());
                                    double health = entityStatMap.get(Stats.HEALTH);
                                    double damage = 0d;
                                    if (Objects.nonNull(entityStatMap.get(Stats.DAMAGE)))
                                        damage = entityStatMap.get(Stats.DAMAGE);
                                    double speed = entityStatMap.get(Stats.SPEED);

                                    if (e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null)
                                        e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
                                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
                                    e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed / 1000);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        if (!isReloading) loadData();

        if (StatManager.getBaseStatMap() == null || StatManager.getBaseStatMap().isEmpty()) {
            getLogger().info("Map is null, initializing...");
            StatManager.setBaseStatMap(new HashMap<>());
            for (World world : getWorlds()) for (Entity entity : world.getEntities()) {
                if (entity instanceof LivingEntity e) {
                    initEntity(e);
                }
            }
        }
        int i = 0;
        for (World world : getWorlds()) for (Entity ignored : world.getEntities()) i++;
        getLogger().info("entity count: " + i);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        for (Entity e : event.getWorld().getEntities()) {
            if (e instanceof LivingEntity le) {
                if (e.getPassengers().isEmpty()){
                    addDisplay(le);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity e) {
            if (!(e instanceof TextDisplay) && !(e instanceof Player)) {
                if (!e.getPersistentDataContainer().has(getPDC("displayAdded"), PersistentDataType.BYTE)) {
                    e.getPersistentDataContainer().set(getPDC("displayAdded"), PersistentDataType.BYTE, (byte) 1);

                    modifyData(event);

                    switch (event) {
                        case CreatureSpawnEvent ignored -> {
                            initEntity(e);
                            addDisplay(e);
                        }
                        case SpawnerSpawnEvent ignored -> {
                            if (e.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null)
                                e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(e.getHealth() * 30);
                            if (e.getAttribute(Attribute.GENERIC_ARMOR) != null)
                                e.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(1);
                            if (e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null)
                                e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(e.getType().getDefaultAttributes().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() * 10);
                            initEntity(e);
                            addDisplay(e);
                        }
                        case TrialSpawnerSpawnEvent ignored -> {
                            if (hasOminousTrialSpawner(getBlocksInRadius(e, 5))) {
                                if (e.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null)
                                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(e.getHealth() * 10000);
                                if (e.getAttribute(Attribute.GENERIC_ARMOR) != null)
                                    e.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(9);
                                if (e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null)
                                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(e.getType().getDefaultAttributes().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() * 2000);
                                e.getPersistentDataContainer().set(getPDC("ominous"), PersistentDataType.BOOLEAN, true);
                            } else {
                                e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(e.getHealth() * 1000);
                                if (e.getAttribute(Attribute.GENERIC_ARMOR) != null)
                                    e.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(4);
                                if (e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null)
                                    e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(e.getType().getDefaultAttributes().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() * 100);
                            }
                            initEntity(e);
                            addDisplay(e);
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + event);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        p.setHealthScale(20);
        double intelligence = 100d;
        Map<UUID, Map<Stats, Double>> map = StatManager.getBaseStatMap();
        Map<Stats, Double> statMap = map.get(p.getUniqueId());
        if (statMap == null || statMap.isEmpty()) {
            statMap = new HashMap<>();
            statMap.put(Stats.DAMAGE, 1d);
            statMap.put(Stats.STRENGTH, 0d);
            statMap.put(Stats.CRITCHANCE, 30d);
            statMap.put(Stats.CRITDAMAGE, 50d);

            statMap.put(Stats.FEROCITY, 0d);
            statMap.put(Stats.ATTACKSPEED, 0d);

            statMap.put(Stats.HEALTH, 100d);
            statMap.put(Stats.DEFENSE, 0d);
            statMap.put(Stats.SPEED, 100d);
            statMap.put(Stats.INTELLIGENCE, 100d);
            statMap.put(Stats.AGILITY, 0d);

            statMap.put(Stats.HEALTHREGEN, 100d);
            statMap.put(Stats.MANAREGEN, 100d);

            statMap.put(Stats.LUCK, 100d);

            StatManager.getBaseStatMap().put(p.getUniqueId(), statMap);
        }

        if (!StatManager.getSkillBonusMap().containsKey(p.getUniqueId())) {
            StatManager.getSkillBonusMap().put(p.getUniqueId(), new HashMap<>());
            for (Stats stat : Stats.values()) StatManager.getSkillBonusMap().get(p.getUniqueId()).put(stat, 0d);
            Map<UUID, Map<String, Integer>> map2 = SkillManager.getCooldownMap();
            if (!map2.containsKey(p.getUniqueId()))
                map2.put(p.getUniqueId(), new HashMap<>());  // Initialize if not present

            for (Class<?> clazz : getSkillClasses()) {
                Skill skill = clazz.getAnnotation(Skill.class);
                if (!map2.get(p.getUniqueId()).containsKey(skill.ID())) map2.get(p.getUniqueId()).put(skill.ID(), 0);
            }
            SkillManager.setCooldownMap(map2);
        }

        Map<UUID, ItemStack[]> accessories = AccessoriesUtils.getAccessories();
        if (accessories == null || accessories.isEmpty()) {
            accessories = new HashMap<>(); // Initialize if it's null
            AccessoriesUtils.setAccessories(accessories); // Assume you have a way to set it
            accessories.putIfAbsent(p.getUniqueId(), new ItemStack[]{});
        }

        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(statMap.get(Stats.HEALTH));
        p.setHealth(statMap.get(Stats.HEALTH));

        if (Objects.nonNull(StatManager.getFinalStatMap().get(p.getUniqueId()))) intelligence = StatManager.getFinalStatMap().get(p.getUniqueId()).get(Stats.INTELLIGENCE);
        playerIntelligence.put(p.getUniqueId(), intelligence);

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Map<Stats, Double> statMap = StatManager.getFinalStatMap().get(p.getUniqueId());
                double current = playerIntelligence.get(p.getUniqueId());
                double max, regen;
                try {
                    max = statMap.get(Stats.INTELLIGENCE);
                } catch (NullPointerException ignored) {
                    max = 100;
                }
                try {
                    regen = statMap.get(Stats.MANAREGEN);
                } catch (NullPointerException ignored) {
                    regen = 100;
                }

                double heal = (max / 25) * asPercentageMultiplier(regen);
                playerIntelligence.put(p.getUniqueId(), Math.min(max, current + heal));
            }
        };
        runnable.runTaskTimer(plugin, 0, 20);
        playerTaskMap.put(event.getPlayer().getUniqueId(), runnable.getTaskId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        getServer().getScheduler().cancelTask(playerTaskMap.get(event.getPlayer().getUniqueId()));
        playerTaskMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent event) {
        Map<Stats, Double> statMap = StatManager.getFinalStatMap().get(event.getEntity().getUniqueId());
        double health = statMap.get(Stats.HEALTH);
        double healthRegen = 100;
        if (event.getEntity() instanceof Player) healthRegen = statMap.get(Stats.HEALTHREGEN);
        double regen = health / 80 * asPercentageMultiplier(healthRegen);
        event.setAmount(regen);
        updateDisplay((LivingEntity) event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent damageEvent) {
        if (!damageEvent.isCancelled()) {
            if (damageEvent.getEntity() instanceof LivingEntity e) {
                if (damageEvent instanceof EntityDamageByEntityEvent event) {
                    if (!(event.getCause().equals(EntityDamageEvent.DamageCause.VOID) || event.getCause().equals(EntityDamageEvent.DamageCause.MAGIC) || event.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM))) {
                        Entity attacker = event.getDamager();
                        Entity entity = event.getEntity();

                        if (entity instanceof LivingEntity victim) {
                            if (victim.getFireTicks() != 0 || victim.getNoDamageTicks() == 0) {
                                boolean crit = false;
                                Map<Stats, Double> attackerMap = StatManager.getFinalStatMap().get(attacker.getUniqueId());
                                Map<Stats, Double> victimMap = StatManager.getFinalStatMap().get(victim.getUniqueId());

                                double ferocity = 0;
                                if (attacker instanceof Player) ferocity = attackerMap.get(Stats.FEROCITY);

                                double attackSpeed = 20;
                                if (attacker instanceof Player)
                                    attackSpeed = 20 - (15 * (attackerMap.get(Stats.ATTACKSPEED) / 100));
                                int guaranteed = (int) (ferocity / 100);
                                double remains = ferocity % 100;

                                double baseDamage = 0;
                                if (attacker instanceof Projectile proj) {
                                    if (!(proj.getShooter() == null || proj.getShooter() instanceof BlockProjectileSource)) {
                                        Pair<Double, Boolean> pair = getFinalDamage((LivingEntity) proj.getShooter());
                                        baseDamage = pair.key();
                                        crit = pair.value();
                                    } else {
                                        baseDamage = event.getDamage() * 5;
                                        crit = false;
                                    }
                                }
                                if (attacker instanceof LivingEntity) {
                                    Pair<Double, Boolean> pair = getFinalDamage(attacker);
                                    baseDamage = pair.key();
                                    crit = pair.value();
                                }

                                double defense = victimMap.get(Stats.DEFENSE);

                                if (!(attacker instanceof Player)) {
                                    if (attacker instanceof Warden) {
                                        if (event.getCause().equals(EntityDamageEvent.DamageCause.SONIC_BOOM)) {
                                            baseDamage = 1_000_000_000_000d * (defense / (defense + 1));
                                            if (victim instanceof Player)
                                                victim.sendMessage(message("Warden's Sonic Boom hitting your for {0} true damage.", new long[]{(long) baseDamage}));
                                            baseDamage = 1_000_000_000_000d;
                                        }
                                    }
                                    if (attacker instanceof AreaEffectCloud cloud) {
                                        if (getEntity(cloud.getOwnerUniqueId()) instanceof EnderDragon) {
                                            if (event.getCause().equals(EntityDamageEvent.DamageCause.DRAGON_BREATH)) {
                                                baseDamage = 100_000d;
                                                if (victim instanceof Player)
                                                    victim.sendMessage(message("Ender Dragon's Acid Breath hitting your for {0} damage.", new long[]{(long) baseDamage}));
                                            }
                                        }
                                    }
                                    if (attacker instanceof ElderGuardian) {
                                        if (event.getCause().equals(EntityDamageEvent.DamageCause.THORNS)) {
                                            baseDamage = 10_000d;
                                            if (victim instanceof Player)
                                                victim.sendMessage(message("Elder Guardian's Thorns hitting your for {0} damage.", new long[]{(long) baseDamage}));
                                        }
                                        if (event.getCause().equals(EntityDamageEvent.DamageCause.MAGIC)) {
                                            baseDamage = 150d;
                                            if (victim instanceof Player)
                                                victim.sendMessage(message("Elder Guardian's Thorns hitting your for {0} true damage.", new long[]{(long) baseDamage}));

                                        }
                                    }
                                    if (attacker instanceof Fireball) {
                                        if (((Fireball) attacker).getShooter() instanceof Ghast) {
                                            baseDamage = 25_000d;
                                            if (victim instanceof Player)
                                                victim.sendMessage(message("Ghast's Fireball hitting your for {0} damage.", new long[]{(long) baseDamage}));
                                        }
                                    }
                                    if (attacker instanceof Projectile) {
                                        if (((Projectile) attacker).getShooter() instanceof BlockProjectileSource) {
                                            baseDamage = 50_000d;
                                            if (victim instanceof Player)
                                                victim.sendMessage(message("Hidden Trap hit you for {0} damage!", new long[]{(long) baseDamage}));
                                        }
                                    }
                                }

                                double agility = 0;
                                if (victim instanceof Player) agility = victimMap.get(Stats.AGILITY);

                                baseDamage *= 1 - (defense / (defense + 1));
                                if (new Random().nextInt(100) + 1 <= agility) baseDamage *= (0.5 * agility) / 100;

                                double finalBaseDamage = baseDamage;
                                boolean finalCrit = crit;
                                if (guaranteed != 0) {
                                    new BukkitRunnable() {
                                        int hits = 0;

                                        @Override
                                        public void run() {
                                            if (victim.isDead() || hits == guaranteed) {
                                                this.cancel();

                                                new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        if (new Random().nextInt(100) + 1 <= remains) {
                                                            victim.damage(finalBaseDamage, causeAndAttacker(DamageType.OUT_OF_WORLD, attacker, victim));
                                                            victim.getWorld().playSound(victim.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1, 0);
                                                            victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 0);
                                                            victim.getWorld().spawnParticle(Particle.CRIT, victim.getLocation().add(0, 1, 0), 10, 0, 0, 0, 0.1);

                                                            int count;
                                                            UUID uuid = attacker.getUniqueId();
                                                            try {
                                                                count = meleeHits.get(uuid);
                                                            } catch (NullPointerException ignored) {
                                                                count = 0;
                                                                meleeHits.put(uuid, 0);
                                                            }
                                                            meleeHits.put(uuid, count + 1);
                                                        }
                                                    }
                                                }.runTaskLater(plugin, FEROCITY_DELAY);
                                            }

                                            victim.damage(finalBaseDamage, causeAndAttacker(DamageType.OUT_OF_WORLD, attacker, victim));
                                            victim.getWorld().playSound(victim.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1, 0);
                                            victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 0);
                                            victim.getWorld().spawnParticle(Particle.CRIT, victim.getLocation(), 10, 0, 0, 0, 0.1);

                                            setNoDamageTicks(victim, 1);

                                            int count;
                                            UUID uuid = attacker.getUniqueId();
                                            try {
                                                count = meleeHits.get(uuid);
                                            } catch (NullPointerException ignored) {
                                                count = 0;
                                                meleeHits.put(uuid, 0);
                                            }
                                            meleeHits.put(uuid, count + 1);
                                            hits++;
                                        }
                                    }.runTaskTimer(plugin, 0, FEROCITY_DELAY);
                                }

                                if (attacker instanceof Projectile proj) {
                                    if (!(proj.getShooter() == null || proj.getShooter() instanceof BlockProjectileSource)) {
                                        int count;
                                        UUID uuid = ((LivingEntity) proj.getShooter()).getUniqueId();
                                        try {
                                            count = rangedHits.get(uuid);
                                        } catch (NullPointerException ignored) {
                                            count = 0;
                                            rangedHits.put(uuid, 0);
                                        }
                                        rangedHits.put(uuid, count + 1);
                                    }
                                } else {
                                    int count;
                                    UUID uuid = attacker.getUniqueId();
                                    try {
                                        count = meleeHits.get(uuid);
                                    } catch (NullPointerException ignored) {
                                        count = 0;
                                        meleeHits.put(uuid, 0);
                                    }
                                    meleeHits.put(uuid, count + 1);
                                }
                                event.setDamage(baseDamage);
                                setNoDamageTicks(e, (int) attackSpeed);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        updateDisplay(victim);
                                    }
                                }.runTaskLater(plugin, 1);

                                showDisplay(finalBaseDamage, damageEvent.getEntity().getLocation(), event.getCause(), finalCrit);
                            } else event.setCancelled(true);
                        }
                    } else if (event.getCause() == EntityDamageEvent.DamageCause.MAGIC) {
                        showDisplay(event.getDamage(), damageEvent.getEntity().getLocation(), event.getCause(), false);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                updateDisplay(e);
                            }
                        }.runTaskLater(plugin, 1);
                    }
                    return;
                }
                if (damageEvent instanceof EntityDamageByBlockEvent event) {
                    if (isCauseNotIgnoreDefense(event.getCause())) {
                        double defense = 0;
                        try {
                            defense = StatManager.getFinalStatMap().get(e.getUniqueId()).get(Stats.DEFENSE);
                        } catch (NullPointerException ignored) {
                        }
                        double baseDamage = event.getDamage() * (1 - (defense / (defense + 1)));
                        event.setDamage(baseDamage);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            updateDisplay(e);
                        }
                    }.runTaskLater(plugin, 1);
                    showDisplay(event.getDamage(), damageEvent.getEntity().getLocation(), event.getCause(), false);
                }
                if (damageEvent instanceof EntityDamageEvent event) {
                    if (isCauseNotIgnoreDefense(event.getCause())) {
                        double defense = 0;
                        try {
                            defense = StatManager.getFinalStatMap().get(e.getUniqueId()).get(Stats.DEFENSE);
                        } catch (NullPointerException ignored) {
                        }
                        double baseDamage = event.getDamage() * (1 - (defense / (defense + 1)));
                        event.setDamage(baseDamage);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            updateDisplay(e);
                        }
                    }.runTaskLater(plugin, 1);
                    showDisplay(event.getDamage(), e.getLocation(), event.getCause(), false);
                }
            } else damageEvent.setDamage(Double.MAX_VALUE);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            StatManager.remove(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveFromWorldEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            StatManager.remove(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            StatManager.remove(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onItemHurt1(PlayerItemDamageEvent event) {
        event.setDamage(0);
        ItemStack i = event.getItem();
        if (getItemID(i) == null) return;
        Integer level = i.getPersistentDataContainer().get(getPDC("unbreaking"), PersistentDataType.INTEGER);
        if (level == null) level = 0;
        Player p = event.getPlayer();

        if (new Random().nextInt(100) > (20 * level)) {
            long damage = i.getItemMeta().getPersistentDataContainer().get(getPDC("damage"), PersistentDataType.LONG);
            long durability = findClassById(getItemID(i)).getAnnotation(ItemMetadata.class).durability() - damage;
            durability--;
            ItemMeta meta = i.getItemMeta();
            meta.getPersistentDataContainer().set(getPDC("damage"), PersistentDataType.LONG, damage + 1);
            i.setItemMeta(meta);

            if (durability <= 0) {
                ((Inventory) ((LivingEntity) p).getEquipment()).remove(i);
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                p.sendMessage(ChatColor.RED + "Your Item " + i.getItemMeta().getDisplayName() + ChatColor.RED + " broke!");
            }
        }
    }

    @EventHandler
    public void onItemHurt2(EntityDamageItemEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PLAYER)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDismount(EntityDismountEvent event) {
        if (event.getEntity() instanceof TextDisplay display) {
            display.remove();
        }
    }

    public static void initEntity(LivingEntity e) {
        if (!(e instanceof TextDisplay)) {
            UUID uuid = e.getUniqueId();
            Map<Stats, Double> statMap = new HashMap<>();
            for (Stats stat : Stats.values()) statMap.put(stat, 0d);

            if (Objects.nonNull(e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)))
                statMap.put(Stats.DAMAGE, e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue());
            if (Objects.nonNull(e.getAttribute(Attribute.GENERIC_MAX_HEALTH))) {
                statMap.put(Stats.HEALTH, e.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                e.setHealth(statMap.get(Stats.HEALTH));
            }
            if (Objects.nonNull(e.getAttribute(Attribute.GENERIC_ARMOR)) || e.getAttribute(Attribute.GENERIC_ARMOR).getBaseValue() == -1000)
                statMap.put(Stats.DEFENSE, e.getAttribute(Attribute.GENERIC_ARMOR).getBaseValue());
            if (Objects.nonNull(e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)))
                statMap.put(Stats.SPEED, e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 1000);

            StatManager.getBaseStatMap().put(uuid, statMap);
            e.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(-1000);
            e.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(-1000);
        }
    }

    public static Pair<Double, Boolean> getFinalDamage(Entity attacker) {
        Map<Stats, Double> attackerMap = StatManager.getFinalStatMap().get(attacker.getUniqueId());

        double damage = attackerMap.get(Stats.DAMAGE);
        double strength = 0;
        if (attacker instanceof Player) strength = attackerMap.get(Stats.STRENGTH);
        double critChance = 0;
        if (attacker instanceof Player) critChance = attackerMap.get(Stats.CRITCHANCE);
        double critDamage = 0;
        if (attacker instanceof Player) critDamage = attackerMap.get(Stats.CRITDAMAGE);

        double baseDamage = damage * (1 + (strength / 10));
        boolean crit = false;
        if (new Random().nextInt(100) + 1 <= critChance) {
            baseDamage *= asPercentageMultiplier(critDamage);
            crit = true;
        }

        return Pair.of(baseDamage, crit);
    }

    private void addDisplay(LivingEntity e) {
        if (!(e instanceof TextDisplay) && !(e instanceof Player)) {
            if (e.getPassengers().isEmpty()) {
                try {
                    e.getPassengers().removeFirst();
                } catch (NoSuchElementException ignored) {
                }

                TextDisplay display = e.getWorld().spawn(e.getLocation(), TextDisplay.class);
                display.setAlignment(TextDisplay.TextAlignment.CENTER);
                display.setSeeThrough(false);
                display.setBillboard(Display.Billboard.CENTER);

                String name;
                if (e.getCustomName() == null) {
                    name = e.getName();
                } else {
                    name = e.getCustomName();
                }

                e.getPersistentDataContainer().set(getPDC("name"), PersistentDataType.STRING, name);

                String text = name + " " + ChatColor.GREEN + shortNumber(e.getHealth()) + ChatColor.GRAY + "/" + ChatColor.GREEN + shortNumber(e.getMaxHealth()) + ChatColor.RED + "❤";
                display.setText(text);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        e.addPassenger(display);
                    }
                }.runTaskLater(plugin, 1);
            }
        }
    }

    private void updateDisplay(LivingEntity e) {
        if (!(e instanceof Player)) {
            if (e.getPassengers().isEmpty()) {
                e.remove();
                return;
            }
            if (e.getPassengers().getFirst() instanceof TextDisplay display) {
                String name = e.getPersistentDataContainer().get(getPDC("name"), PersistentDataType.STRING);
                String name2 = e.getCustomName();
                if (name2 == null) name2 = e.getName();
                if (name.equals(name2)) {
                    String text;
                    if (e.getHealth() / e.getMaxHealth() > 0.5) {
                        text = name + " " + ChatColor.GREEN + shortNumber(e.getHealth()) + ChatColor.GRAY + "/" + ChatColor.GREEN + shortNumber(e.getMaxHealth()) + ChatColor.RED + "❤";
                    } else {
                        text = name + " " + ChatColor.YELLOW + shortNumber(e.getHealth()) + ChatColor.GRAY + "/" + ChatColor.GREEN + shortNumber(e.getMaxHealth()) + ChatColor.RED + "❤";
                    }
                    display.setText(text);
                }
            }
        }
    }

    private void showDisplay(double damage, Location location, EntityDamageEvent.DamageCause cause, boolean crit) {
        double rand1 = Math.random() - 0.5;
        double rand2 = Math.random() - 0.5;
        double rand3 = Math.random() - 0.5 / 4;

        TextDisplay display = location.add(rand1, .67 + rand3, rand2).getWorld().spawn(location, TextDisplay.class);
        display.setAlignment(TextDisplay.TextAlignment.CENTER);
        display.setSeeThrough(true);
        display.setBillboard(Display.Billboard.CENTER);

        String color = ChatColor.GRAY + "";
        if (cause.equals(EntityDamageEvent.DamageCause.VOID)) color = ChatColor.DARK_PURPLE + "";
        if (cause.equals(EntityDamageEvent.DamageCause.MAGIC)) color = ChatColor.AQUA + "";
        if (cause.equals(EntityDamageEvent.DamageCause.DROWNING)) color = ChatColor.DARK_AQUA + "";

        String add = "";
        if (cause.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) add = " ✷";
        if (cause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) add = " ✷";

        String text = numberFormat(damage) + add;
        if (crit) text = ChatColor.WHITE + "✧" + createGradientText(text) + ChatColor.WHITE + "✧";
        else text = color + numberFormat(damage);

        display.setText(text);
        new BukkitRunnable() {
            @Override
            public void run() {
                display.remove();
            }
        }.runTaskLater(plugin, 20);
    }

    private String createGradientText(String text) {
        ChatColor[] gradientColors = new ChatColor[]{
                ChatColor.DARK_RED, ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.WHITE
        };

        StringBuilder gradientText = new StringBuilder();
        int colorStep = text.length() / gradientColors.length;
        if (colorStep == 0) colorStep = 1;
        int colorIndex = 0;

        for (int i = 0; i < text.length(); i++) {
            // Change color every few characters to create a gradient effect
            if (i % colorStep == 0 && colorIndex < gradientColors.length - 1) colorIndex++;
            gradientText.append(gradientColors[colorIndex]).append(text.charAt(i));
        }

        return gradientText.toString();
    }

    private String message(String template, long[] args) {
        String formatted = template;
        for (int i = 0; i < args.length; i++) {
            String placeholder = "{" + i + "}";
            String coloredArg = ChatColor.RED + numberFormat(args[i]) + ChatColor.GRAY;  // Apply the placeholder color
            formatted = formatted.replace(placeholder, coloredArg);
        }

        // Split the formatted string into words
        String[] words = formatted.split(" ");
        List<String> result = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        // Append words and collect into lines after a certain number of words
        int wordCount = 0;
        for (String word : words) {
            currentLine.append(ChatColor.GRAY).append(word).append(" ");
            wordCount++;

            // When we reach the word limit for a line, add the current line to the list
            if (wordCount == Integer.MAX_VALUE) {
                result.add(currentLine.toString().trim());
                currentLine.setLength(0);  // Clear the StringBuilder for the next line
                wordCount = 0;  // Reset the word count
            }
        }

        // Add the last line if any remaining words
        if (!currentLine.isEmpty()) {
            result.add(currentLine.toString().trim());
        }

        String value = "";
        for (String s : result) {
            value = String.join("", s);
        }
        return value;
    }

    private boolean isCauseNotIgnoreDefense(EntityDamageEvent.DamageCause cause) {
        return !(cause.equals(EntityDamageEvent.DamageCause.FALL) ||
                cause.equals(EntityDamageEvent.DamageCause.DROWNING) ||
                cause.equals(EntityDamageEvent.DamageCause.FIRE) ||
                cause.equals(EntityDamageEvent.DamageCause.FIRE_TICK) ||
                cause.equals(EntityDamageEvent.DamageCause.SUFFOCATION) ||
                cause.equals(EntityDamageEvent.DamageCause.MAGIC) ||
                cause.equals(EntityDamageEvent.DamageCause.WITHER) ||
                cause.equals(EntityDamageEvent.DamageCause.POISON) ||
                cause.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) ||
                cause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION));
    }

    private Set<Block> getBlocksInRadius(Entity entity, int radius) {
        Set<Block> blocks = new HashSet<>();
        Location loc = entity.getLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        // Loop through all blocks in the cubic area
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    blocks.add(loc.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                }
            }
        }
        return blocks;
    }

    public boolean hasOminousTrialSpawner(Set<Block> blocks) {
        for (Block block : blocks) {
            // Check if the block is a spawner (or your custom block type)
            if (block.getType() == Material.TRIAL_SPAWNER) {
                if (((TrialSpawner) block.getState()).isOminous()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void loadData() {
        StatManager.setBaseStatMap(dataMapManager1.loadStatsMap());
        AccessoriesUtils.setAccessories(dataMapManager2.loadItemStackMap());
        new File(plugin.getDataFolder(), "baseMap.yml").delete();
        new File(plugin.getDataFolder(), "accessories.yml").delete();
        getLogger().info("Data loaded successfully.");
    }
}
