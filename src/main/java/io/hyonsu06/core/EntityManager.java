package io.hyonsu06.core;

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
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static io.hyonsu06.Main.plugin;
import static io.hyonsu06.core.functions.NumberTweaks.*;
import static io.hyonsu06.core.functions.customCauseAndAttacker.causeAndAttacker;
import static io.hyonsu06.core.functions.getClasses.getItemClasses;
import static io.hyonsu06.core.functions.getClasses.getSkillClasses;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getItemID;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;
import static io.hyonsu06.core.functions.setImmuneTime.setNoDamageTicks;
import static org.bukkit.Bukkit.*;

public class EntityManager implements Listener {
    @Getter @Setter
    private static Map<UUID, Double> playerIntelligence = new HashMap<>();
    @Getter
    private static Map<UUID, Integer> playerTaskMap = new HashMap<>();
    @Getter @Setter
    private static Map<UUID, Integer> meleeHits = new HashMap<>();
    @Getter @Setter
    private static Map<UUID, Integer> rangedHits = new HashMap<>();

    final int FEROCITY_DELAY = 5;

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
    public void onWorldLoad(WorldLoadEvent event) {
        for (Entity entity : event.getWorld().getEntities()) {
            entity.remove();
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity e) {
            if (!(e instanceof TextDisplay) || !(e instanceof Player)) {
                initEntity(e);
                addDisplay(e);
            }
        }
    }

    @EventHandler
    public void onSpawn(SpawnerSpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity e) {
            if (!(e instanceof TextDisplay) || !(e instanceof Player)) {
                initEntity(e);
                addDisplay(e);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        p.setHealthScale(40);
        double intelligence = 100d;
        Map<Stats, Double> statMap = StatManager.getBaseStatMap().get(p.getUniqueId());
        if (statMap == null) {
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

            StatManager.getBaseStatMap().put(event.getPlayer().getUniqueId(), statMap);

            StatManager.getSkillBonusMap().put(p.getUniqueId(), new HashMap<>());
            for (Stats stat : Stats.values()) StatManager.getSkillBonusMap().get(p.getUniqueId()).put(stat, 0d);

            // Ensure the player's cooldown map exists
            SkillManager.getCooldownMap().computeIfAbsent(p.getUniqueId(), k -> new HashMap<>());
            for (Class<?> clazz : getSkillClasses()) {
                Skill skill = clazz.getAnnotation(Skill.class);
                SkillManager.getCooldownMap().get(p.getUniqueId()).putIfAbsent(skill.ID(), 0);
            }

            AccessoriesUtils.getAccessories().computeIfAbsent(p.getUniqueId(), k -> new ItemStack[]{});

            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(statMap.get(Stats.HEALTH));
            p.setHealth(statMap.get(Stats.HEALTH));
        }

        if (Objects.nonNull(StatManager.getFinalStatMap().get(p.getUniqueId()))) {
            intelligence = StatManager.getFinalStatMap().get(p.getUniqueId()).get(Stats.INTELLIGENCE);
        }
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

                double heal = (max / 20) * asPercentageMultiplier(regen);
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
        double regen = health / 20 * asPercentageMultiplier(healthRegen);
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
                            if (victim.getNoDamageTicks() == 0) {
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
                    double defense = 0;
                    try {
                        defense = StatManager.getFinalStatMap().get(e.getUniqueId()).get(Stats.DEFENSE);
                    } catch (NullPointerException ignored) {
                    }
                    double baseDamage = event.getDamage() * (1 - (defense / (defense + 1)));
                    event.setDamage(baseDamage);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            updateDisplay(e);
                        }
                    }.runTaskLater(plugin, 1);
                    showDisplay(baseDamage, damageEvent.getEntity().getLocation(), event.getCause(), false);
                }
                if (damageEvent instanceof EntityDamageEvent event) {
                    double defense = 0;
                    try {
                        defense = StatManager.getFinalStatMap().get(e.getUniqueId()).get(Stats.DEFENSE);
                    } catch (NullPointerException ignored) {
                    }
                    double baseDamage = event.getDamage() * (1 - (defense / (defense + 1)));
                    event.setDamage(baseDamage);
                    showDisplay(event.getDamage(), e.getLocation(), event.getCause(), false);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            updateDisplay(e);
                        }
                    }.runTaskLater(plugin, 1);
                }
            } else damageEvent.setDamage(Double.MAX_VALUE);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) StatManager.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveFromWorldEvent event) {
        if (!(event.getEntity() instanceof Player)) StatManager.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Player)) StatManager.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onItemHurt1(PlayerItemDamageEvent event) {
        event.setDamage(0);
        ItemStack i = event.getItem();
        if (getItemID(i) == null) return;
        Player p = event.getPlayer();

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

    @EventHandler
    public void onItemHurt2(EntityDamageItemEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PLAYER)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (event.getEntity() instanceof TextDisplay display) {
            display.remove();
        }
    }

    private void initEntity(LivingEntity e) {
        if (!(e instanceof Player)) {
            UUID uuid = e.getUniqueId();
            if (StatManager.getBaseStatMap().get(uuid) == null) {
                Map<Stats, Double> statMap = new HashMap<>();
                for (Stats stat : Stats.values()) statMap.put(stat, 0d);

                if (Objects.nonNull(e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)))
                    statMap.put(Stats.DAMAGE, e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() * 10);
                if (Objects.nonNull(e.getAttribute(Attribute.GENERIC_MAX_HEALTH))) {
                    statMap.put(Stats.HEALTH, e.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * 5);
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(statMap.get(Stats.HEALTH));
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

    private Class<?> findClassById(String id) {
        for (Class<?> clazz : getItemClasses()) {
            if (clazz.getAnnotation(ItemMetadata.class).ID().equals(id)) {
                return clazz;
            }
        }
        return null; // Placeholder return
    }

    private void addDisplay(LivingEntity e) {
        if (!e.getType().equals(EntityType.PLAYER) || !e.getType().equals(EntityType.TEXT_DISPLAY)) {
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

            String text = name + " " + ChatColor.GREEN + numberFormat(e.getHealth()) + ChatColor.GRAY + "/" + ChatColor.GREEN + numberFormat(e.getMaxHealth()) + ChatColor.RED + "❤";
            display.setText(text);

            e.addPassenger(display);
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
}
