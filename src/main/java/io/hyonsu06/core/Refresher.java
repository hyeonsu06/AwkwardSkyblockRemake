package io.hyonsu06.core;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.items.items.ItemAdditiveBonus;
import io.hyonsu06.core.annotations.items.items.ItemMultiplicativeBonus;
import io.hyonsu06.core.annotations.items.reforge.ReforgeMetadata;
import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.enums.ReforgeType;
import io.hyonsu06.core.enums.Stats;
import io.hyonsu06.item.enchantments.Enchantment;
import io.hyonsu06.item.enchantments.EnchantmentUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static io.hyonsu06.Main.plugin;
import static io.hyonsu06.command.items.LoadItems.WORDS_PER_LINE;
import static io.hyonsu06.core.functions.MapPDCConverter.PDCToMap;
import static io.hyonsu06.core.managers.StatManager.rarityToIndex;
import static io.hyonsu06.core.enums.ItemRarity.next;
import static io.hyonsu06.core.functions.ItemTypeForSlot.getReforgeType;
import static io.hyonsu06.core.functions.NumberTweaks.*;
import static io.hyonsu06.core.functions.getClasses.getItemClasses;
import static io.hyonsu06.core.functions.getClasses.getReforgeClasses;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getItemID;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;
import static io.hyonsu06.core.functions.getStatText.*;
import static java.text.MessageFormat.format;
import static org.bukkit.Bukkit.getLogger;

public class Refresher {
    public static final int POTATO_BOOK_WEAPON_DAMAGE = 2;
    public static final int POTATO_BOOK_WEAPON_STRENGTH = 2;
    public static final int POTATO_BOOK_ARMOR_HEALTH = 5;
    public static final int POTATO_BOOK_ARMOR_DEFENSE = 4;

    public static Refresher instance = null;

    public static Refresher instance() {
        if (instance == null) {
            instance = new Refresher();
        }
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    public Refresher() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    for (int slot = 0; slot < p.getInventory().getSize(); slot++) {
                        if (p.getInventory().getItem(slot) != null) refreshItemVisuals(p.getInventory().getItem(slot));
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public void refreshItemVisuals(ItemStack item) {
        // Check if item has ItemMetadata
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.getPersistentDataContainer().has(getPDC("id"), PersistentDataType.STRING))
            return; // No valid item metadata

        String reforge = meta.getPersistentDataContainer().get(getPDC("reforge"), PersistentDataType.STRING);
        Class<?> reforgeClass = null;
        if (reforge != null) {
            for (Class<?> clazz : getReforgeClasses()) {
                if (clazz.getAnnotation(ReforgeMetadata.class).ID().equals(reforge.toLowerCase())) {
                    reforgeClass = clazz;
                    break;
                }
            }
        }

        // Retrieve the item ID
        String itemId = meta.getPersistentDataContainer().get(getPDC("id"), PersistentDataType.STRING);

        // Find the class associated with this item ID (assuming you have a way to map IDs to classes)
        Class<?> clazz = findClassById(itemId);

        // Recreate item metadata
        ItemMetadata metadata = clazz.getAnnotation(ItemMetadata.class);
        List<String> lore = new ArrayList<>();

        Map<Stats, Double[]> reforgeMap = new HashMap<>();
        if (reforgeClass != null) {
            try {
                for (Method m : reforgeClass.getDeclaredMethods()) {
                    if (m.getName().equals("baseValue")) {
                        Object instance = reforgeClass.getDeclaredConstructor().newInstance();
                        reforgeMap = (Map<Stats, Double[]>) m.invoke(instance);
                    }
                }
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException ex) {
                getLogger().severe(ex.getMessage());
            }
        }

        boolean isRecombobulatorPresent;
        boolean isRecombobulatorEXPresent;
        Object temp1 = meta.getPersistentDataContainer().get(getPDC("recombobulator"), PersistentDataType.BOOLEAN);
        Object temp2 = meta.getPersistentDataContainer().get(getPDC("recombobulatorEX"), PersistentDataType.BOOLEAN);
        if (temp1 == null) isRecombobulatorPresent = false;
        else isRecombobulatorPresent = (boolean) temp1;
        if (temp2 == null) isRecombobulatorEXPresent = false;
        else isRecombobulatorEXPresent = (boolean) temp2;

        int potatoBooks;
        Object temp3 = meta.getPersistentDataContainer().get(getPDC("potato"), PersistentDataType.INTEGER);
        if (temp3 == null) potatoBooks = 0;
        else potatoBooks = (int) temp3;

        if (clazz.isAnnotationPresent(ItemStats.class)) {
            ItemStats stats = clazz.getAnnotation(ItemStats.class);
            if (isRecombobulatorEXPresent) {
                baseStatLore(
                        lore,
                        stats.damage(), stats.strength(), stats.critChance(), stats.critDamage(),
                        stats.ferocity(), stats.attackSpeed(),
                        stats.health(), stats.defense(), stats.speed(), stats.intelligence(), stats.agility(),
                        stats.healthRegen(), stats.manaRegen(),
                        stats.luck(),
                        reforgeMap, rarityToIndex(next(next(metadata.rarity()))),
                        potatoBooks,
                        getReforgeType(clazz.getAnnotation(ItemMetadata.class).type()),
                        item
                );
            } else if (isRecombobulatorPresent) {
                baseStatLore(
                        lore,
                        stats.damage(), stats.strength(), stats.critChance(), stats.critDamage(),
                        stats.ferocity(), stats.attackSpeed(),
                        stats.health(), stats.defense(), stats.speed(), stats.intelligence(), stats.agility(),
                        stats.healthRegen(), stats.manaRegen(),
                        stats.luck(),
                        reforgeMap, rarityToIndex(next(metadata.rarity())),
                        potatoBooks,
                        getReforgeType(clazz.getAnnotation(ItemMetadata.class).type()),
                        item
                );
            } else {
                baseStatLore(
                        lore,
                        stats.damage(), stats.strength(), stats.critChance(), stats.critDamage(),
                        stats.ferocity(), stats.attackSpeed(),
                        stats.health(), stats.defense(), stats.speed(), stats.intelligence(), stats.agility(),
                        stats.healthRegen(), stats.manaRegen(),
                        stats.luck(),
                        reforgeMap, rarityToIndex(metadata.rarity()),
                        potatoBooks,
                        getReforgeType(clazz.getAnnotation(ItemMetadata.class).type()),
                        item
                );
            }
        }

        if (clazz.isAnnotationPresent(ItemAdditiveBonus.class)) {
            ItemAdditiveBonus bonus1 = clazz.getAnnotation(ItemAdditiveBonus.class);
            additiveStatLore(
                    lore,
                    bonus1.add_damage(), bonus1.add_strength(), bonus1.add_critChance(), bonus1.add_critDamage(),
                    bonus1.add_ferocity(), bonus1.add_attackSpeed(),
                    bonus1.add_health(), bonus1.add_defense(), bonus1.add_speed(), bonus1.add_intelligence(), bonus1.add_agility(),
                    bonus1.add_healthRegen(), bonus1.add_manaRegen(),
                    bonus1.add_luck()
            );
        }

        if (clazz.isAnnotationPresent(ItemMultiplicativeBonus.class)) {
            ItemMultiplicativeBonus bonus2 = clazz.getAnnotation(ItemMultiplicativeBonus.class);
            multiplicativeStatLore(
                    lore,
                    bonus2.mul_damage(), bonus2.mul_strength(), bonus2.mul_critChance(), bonus2.mul_critDamage(),
                    bonus2.mul_ferocity(), bonus2.mul_attackSpeed(),
                    bonus2.mul_health(), bonus2.mul_defense(), bonus2.mul_speed(), bonus2.mul_intelligence(), bonus2.mul_agility(),
                    bonus2.mul_healthRegen(), bonus2.mul_manaRegen(),
                    bonus2.mul_luck()
            );
        }

        // Add additional lore if present
        if (!metadata.description().isEmpty()) {
            lore.addAll(addSkillDescription(metadata.description(), WORDS_PER_LINE, new long[]{}, ChatColor.GRAY, ChatColor.AQUA));
            lore.add(" ");
        }

        StringBuilder builder = new StringBuilder();
        List<String> temp = new ArrayList<>();
        int count = 0;
        Map<Enchantment, Integer> enchantments = (EnchantmentUtils.getEnchantments(item));

        Map<Enchantment, Integer> sortedMap = enchantments.entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getName())) // Sort by Enchantment's key
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // Merge function (not used here)
                        LinkedHashMap::new // Preserve insertion order
                ));

        for (Map.Entry<Enchantment, Integer> entry : sortedMap.entrySet()) {
            String name = entry.getKey().getName();
            int level = entry.getValue();
            builder.append(ChatColor.BLUE).append(name).append(" ").append(level).append(", ");
            count++;
            if (count == 3 || count == enchantments.size()) {
                temp.add(builder.delete(builder.length() - 2, builder.length()).toString());
                count = 0;
            }
        }

        if (!temp.isEmpty()) {
            lore.addAll(temp);
            lore.add(" ");
        }

        for (Class<?> clazz2 : clazz.getAnnotation(ItemMetadata.class).skills()) {
            Skill skills = clazz2.getAnnotation(Skill.class);

            // Add skill name and general information once per skill class
            if (!skills.name().isEmpty()) {
                if (!skills.simpleDescription()) {
                    lore.add(format(ChatColor.GOLD + "Ability: {0}  {1}",
                            skills.name(),
                            ChatColor.BOLD + "", ChatColor.YELLOW + "")
                    );

                    lore.addAll(addSkillDescription(skills.description(), WORDS_PER_LINE, skills.args(), ChatColor.GRAY, ChatColor.AQUA));

                    if (skills.cost() != 0) {
                        lore.add(format(ChatColor.DARK_GRAY + "Mana cost: {0}", ChatColor.AQUA + String.valueOf(skills.cost())));
                    }
                    if (skills.cooldown() != 0) {
                        double value = ((double) skills.cooldown()) / 20;
                        lore.add(format(ChatColor.DARK_GRAY + "Cooldown: {0}s", ChatColor.AQUA + String.valueOf(value)));
                    }
                    lore.add(" ");
                } else {
                    lore.addAll(addSkillDescription(skills.description(), WORDS_PER_LINE, skills.args(), ChatColor.GRAY, ChatColor.AQUA));
                    lore.add(" ");
                }
            }
        }

        long customDurability = 0;
        Long customCurrentDamage = item.getItemMeta().getPersistentDataContainer().get(getPDC("damage"), PersistentDataType.LONG);
        if (customCurrentDamage == null) customCurrentDamage = 0L;
        for (Class<?> clazz2 : getItemClasses())
            if (clazz2.getAnnotation(ItemMetadata.class).ID().equals(getItemID(item)))
                customDurability = clazz2.getAnnotation(ItemMetadata.class).durability();

        double customDurabilityPercentage = ((double) (customDurability - customCurrentDamage) / customDurability);
        short finalDurability = (short) ((item.getType().getMaxDurability() - (item.getType().getMaxDurability() - ((1 - customDurabilityPercentage) * item.getType().getMaxDurability()))));

        // Update durability
        if (metadata.durability() != Long.MIN_VALUE) {
            lore.add(ChatColor.DARK_GRAY + "Durability: " + shortNumber(metadata.durability() - customCurrentDamage) + "/" + shortNumber(metadata.durability()));
        }

        meta.getPersistentDataContainer().set(getPDC("type"), PersistentDataType.STRING, metadata.type().getDisplay().toLowerCase());

        ChatColor color = metadata.rarity().getColor();
        if (isRecombobulatorPresent) color = next(metadata.rarity()).getColor();
        if (isRecombobulatorEXPresent) color = next(next(metadata.rarity())).getColor();

        // Set display name and lore
        if (reforge == null) {
            meta.setDisplayName(color + metadata.name());
        } else {
            meta.setDisplayName(color + capitalizeFirstLetter(reforge) + " " + metadata.name());
        }
        if (isRecombobulatorEXPresent) {
            lore.add(color + "" + ChatColor.BOLD + ChatColor.MAGIC + "xx" + ChatColor.RESET + color + " " + ChatColor.BOLD + next(next(metadata.rarity())).getDisplay().toUpperCase() + " " + metadata.type().getDisplay().toUpperCase() + " " + ChatColor.MAGIC + "xx");
        } else if (isRecombobulatorPresent) {
            lore.add(color + "" + ChatColor.BOLD + ChatColor.MAGIC + "x" + ChatColor.RESET + color + " " + ChatColor.BOLD + next(metadata.rarity()).getDisplay().toUpperCase() + " " + metadata.type().getDisplay().toUpperCase() + " " + ChatColor.MAGIC + "x");
        } else
            lore.add(color + "" + ChatColor.BOLD + metadata.rarity().getDisplay().toUpperCase() + " " + metadata.type().getDisplay().toUpperCase());
        meta.setLore(lore);

        // Reapply meta to item
        item.setItemMeta(meta);
        item.setDurability(finalDurability);
        item.setType(metadata.material());
    }

    public static Class<?> findClassById(String id) {
        for (Class<?> clazz : getItemClasses()) {
            if (clazz.getAnnotation(ItemMetadata.class).ID().equals(id)) {
                return clazz;
            }
        }
        return null; // Placeholder return
    }

    public static void baseStatLore(List<String> lore, double damage, double strength, double critChance, double critDamage, double ferocity, double attackSpeed, double health, double defense, double speed, double intelligence, double agility, double healthRegen, double manaRegen, double luck, Map<Stats, Double[]> map, int rarity, int potato, ReforgeType type, ItemStack item) {
        Double[] defaultMap = new Double[]{0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d};
        for (Stats stat : Stats.values()) map.putIfAbsent(stat, defaultMap);

        Map<Stats, Double> map2 = new EnumMap<>(Stats.class);
        for (Stats stats : Stats.values()) map2.put(stats, 0d);

        if (item != null) if (item.getPersistentDataContainer().has(getPDC("enchants"), PersistentDataType.STRING))
            map2 = PDCToMap(item, "enchants");

        if (damage != 0 || map.get(Stats.DAMAGE)[rarity] != 0 || (potato > 0 && (type == ReforgeType.MELEE || type == ReforgeType.RANGED)) || (map2.get(Stats.DAMAGE) != null && map2.get(Stats.DAMAGE) != 0)) {
            String stat = ChatColor.GRAY + "Damage: ";
            String ref = "", potatoBook = "";
            double value = damage;
            if (map2.get(Stats.DAMAGE) != null) value += map2.get(Stats.DAMAGE);
            if (map.get(Stats.DAMAGE)[rarity] != 0) {
                value += map.get(Stats.DAMAGE)[rarity];
                ref = ChatColor.BLUE + " (" + addPlusIfPositive(map.get(Stats.DAMAGE)[rarity]) + ")";
            }
            if (potato > 0 && (type == ReforgeType.MELEE || type == ReforgeType.RANGED)) {
                value += potato * POTATO_BOOK_WEAPON_DAMAGE;
                potatoBook = ChatColor.YELLOW + " (" + addPlusIfPositive(potato * POTATO_BOOK_WEAPON_DAMAGE) + ")";
            }
            lore.add(stat + ChatColor.RED + addPlusIfPositive(value) + ref + potatoBook);
            damage = value;
        } // Damage
        if (strength != 0 || map.get(Stats.STRENGTH)[rarity] != 0 || (potato > 0 && (type == ReforgeType.MELEE || type == ReforgeType.RANGED)) || (map2.get(Stats.STRENGTH) != null && map2.get(Stats.STRENGTH) != 0)) {
            String stat = ChatColor.GRAY + "Strength: ";
            String ref = "", potatoBook = "";
            double value = strength;
            if (map2.get(Stats.STRENGTH) != null) value += map2.get(Stats.STRENGTH);
            if (map.get(Stats.STRENGTH)[rarity] != 0) {
                value += map.get(Stats.STRENGTH)[rarity];
                ref = ChatColor.BLUE + " (" + addPlusIfPositive(map.get(Stats.STRENGTH)[rarity]) + ")";
            }
            if (potato > 0 && (type == ReforgeType.MELEE || type == ReforgeType.RANGED)) {
                value += potato * POTATO_BOOK_WEAPON_STRENGTH;
                potatoBook = ChatColor.YELLOW + " (" + addPlusIfPositive(potato * POTATO_BOOK_WEAPON_STRENGTH) + ")";
            }
            lore.add(stat + ChatColor.RED + addPlusIfPositive(value) + ref + potatoBook);
            strength = value;
        } // Strength
        if (critChance != 0 || map.get(Stats.CRITCHANCE)[rarity] != 0 || (map2.get(Stats.CRITCHANCE) != null && map2.get(Stats.CRITCHANCE) != 0)) {
            String stat = ChatColor.GRAY + "Crit Chance: ";
            String ref = "";
            double value = critChance;
            Stats thisStat = Stats.CRITCHANCE;
            if (map2.get(thisStat) != null) value += map2.get(thisStat);
            if (map.get(thisStat)[rarity] != 0) {
                value += map.get(thisStat)[rarity];
                ref = ChatColor.BLUE + " (" + addPlusIfPositive(map.get(thisStat)[rarity]) + ")";
            }
            lore.add(stat + ChatColor.RED + addPlusIfPositive(value) + ref);
            critChance = value;
        } // Crit Chance
        if (critDamage != 0 || map.get(Stats.CRITDAMAGE)[rarity] != 0 || (map2.get(Stats.CRITDAMAGE) != null && map2.get(Stats.CRITCHANCE) != 0)) {
            String stat = ChatColor.GRAY + "Crit Damage: ";
            String ref = "";
            double value = critDamage;
            Stats thisStat = Stats.CRITDAMAGE;
            if (map2.get(thisStat) != null) value += map2.get(thisStat);
            if (map.get(thisStat)[rarity] != 0) {
                value += map.get(thisStat)[rarity];
                ref = ChatColor.BLUE + " (" + addPlusIfPositive(map.get(thisStat)[rarity]) + ")";
            }
            lore.add(stat + ChatColor.RED + addPlusIfPositive(value) + ref);
            critDamage = value;
        } // Crit Damage
        if (ferocity != 0 || map.get(Stats.FEROCITY)[rarity] != 0 || (map2.get(Stats.FEROCITY) != null && map2.get(Stats.FEROCITY) != 0)) {
            String stat = ChatColor.GRAY + "Ferocity: ";
            String ref = "";
            double value = ferocity;
            Stats thisStat = Stats.FEROCITY;
            if (map2.get(thisStat) != null) value += map2.get(thisStat);
            if (map.get(thisStat)[rarity] != 0) {
                value += map.get(thisStat)[rarity];
                ref = ChatColor.BLUE + " (" + addPlusIfPositive(map.get(thisStat)[rarity]) + ")";
            }
            lore.add(stat + ChatColor.RED + addPlusIfPositive(value) + ref);
            ferocity = value;
        } // Ferocity
        if (attackSpeed != 0 || map.get(Stats.ATTACKSPEED)[rarity] != 0 || (map2.get(Stats.ATTACKSPEED) != null && map2.get(Stats.ATTACKSPEED) != 0)) {
            String stat = ChatColor.GRAY + "Bonus Attack Speed: ";
            String ref = "";
            double value = attackSpeed;
            Stats thisStat = Stats.ATTACKSPEED;
            if (map2.get(thisStat) != null) value += map2.get(thisStat);
            if (map.get(thisStat)[rarity] != 0) {
                value += map.get(thisStat)[rarity];
                ref = ChatColor.BLUE + " (" + addPlusIfPositive(map.get(thisStat)[rarity]) + ")";
            }
            lore.add(stat + ChatColor.YELLOW + addPlusIfPositive(value) + ref);
            attackSpeed = value;
        } // Attack Speed
        if (health != 0 || map.get(Stats.HEALTH)[rarity] != 0 || (potato > 0 && type == ReforgeType.ARMOR) || (map2.get(Stats.HEALTH) != null && map2.get(Stats.HEALTH) != 0)) {
            String stat = ChatColor.GRAY + "Health: ";
            String ref = "", potatoBook = "";
            double value = health;
            if (map2.get(Stats.HEALTH) != null) value += map2.get(Stats.HEALTH);
            if (map.get(Stats.HEALTH)[rarity] != 0) {
                value += map.get(Stats.HEALTH)[rarity];
                ref = ChatColor.BLUE + " (" + addPlusIfPositive(map.get(Stats.HEALTH)[rarity]) + ")";
            }
            if (potato > 0 && type == ReforgeType.ARMOR) {
                value += potato * POTATO_BOOK_ARMOR_HEALTH;
                potatoBook = ChatColor.YELLOW + " (" + addPlusIfPositive(potato * POTATO_BOOK_ARMOR_HEALTH) + ")";
            }
            lore.add(stat + ChatColor.GREEN + addPlusIfPositive(value) + ref + potatoBook);
            health = value;
        } // Health
        if (defense != 0 || map.get(Stats.DEFENSE)[rarity] != 0 || (potato > 0 && type == ReforgeType.ARMOR) || (map2.get(Stats.DEFENSE) != null && map2.get(Stats.DEFENSE) != 0)) {
            String stat = ChatColor.GRAY + "Defense: ";
            String ref = "", potatoBook = "";
            double value = defense;
            if (map2.get(Stats.DEFENSE) != null) value += map2.get(Stats.DEFENSE);
            if (map.get(Stats.DEFENSE)[rarity] != 0) {
                value += map.get(Stats.DEFENSE)[rarity];
                ref = ChatColor.BLUE + " (" + addPlusIfPositive(map.get(Stats.DEFENSE)[rarity]) + ")";
            }
            if (potato > 0 && type == ReforgeType.ARMOR) {
                value += potato * POTATO_BOOK_ARMOR_DEFENSE;
                potatoBook = ChatColor.YELLOW + " (" + addPlusIfPositive(potato * POTATO_BOOK_ARMOR_DEFENSE) + ")";
            }
            lore.add(stat + ChatColor.GREEN + addPlusIfPositive(value) + ref + potatoBook);
            defense = value;
        } // Defense
        if (speed != 0 || map.get(Stats.SPEED)[rarity] != 0 || (map2.get(Stats.SPEED) != null && map2.get(Stats.SPEED) != 0)) {
            String stat = ChatColor.GRAY + "Walk Speed: ";
            String ref = "";
            double value = speed;
            Stats thisStat = Stats.SPEED;
            if (map2.get(thisStat) != null) value += map2.get(thisStat);
            if (map2.get(thisStat) != null) value += map2.get(thisStat);
            if (map.get(thisStat)[rarity] != 0) {
                value += map.get(thisStat)[rarity];
                ref = ChatColor.BLUE + " (" + addPlusIfPositive(map.get(thisStat)[rarity]) + ")";
            }
            lore.add(stat + ChatColor.WHITE + addPlusIfPositive(value) + ref);
        } // Speed
        if (intelligence != 0 || map.get(Stats.INTELLIGENCE)[rarity] != 0 || (map2.get(Stats.INTELLIGENCE) != null && map2.get(Stats.INTELLIGENCE) != 0)) {
            String stat = ChatColor.GRAY + "Intelligence: ";
            String ref = "";
            double value = intelligence;
            Stats thisStat = Stats.INTELLIGENCE;
            if (map2.get(thisStat) != null) value += map2.get(thisStat);
            if (map.get(thisStat)[rarity] != 0) {
                value += map.get(thisStat)[rarity];
                ref = ChatColor.BLUE + " (" + addPlusIfPositive(map.get(thisStat)[rarity]) + ")";
            }
            lore.add(stat + ChatColor.AQUA + addPlusIfPositive(value) + ref);
        } // Intelligence
        if (agility != 0 || map.get(Stats.AGILITY)[rarity] != 0 || (map2.get(Stats.AGILITY) != null && map2.get(Stats.AGILITY) != 0)) {
            String stat = ChatColor.GRAY + "Agility: ";
            String ref = "";
            double value = agility;
            Stats thisStat = Stats.AGILITY;
            if (map2.get(thisStat) != null) value += map2.get(thisStat);
            if (map.get(thisStat)[rarity] != 0) {
                value += map.get(thisStat)[rarity];
                ref = ChatColor.BLUE + " (" + addPlusIfPositive(map.get(thisStat)[rarity]) + ")";
            }
            lore.add(stat + ChatColor.GREEN + addPlusIfPositive(value) + ref);
            agility = value;
        } // Agility
        if (healthRegen != 0 || map.get(Stats.HEALTHREGEN)[rarity] != 0 || (map2.get(Stats.HEALTHREGEN) != null && map2.get(Stats.HEALTHREGEN) != 0)) {
            String stat = ChatColor.GRAY + "Health Regen: ";
            String ref = "";
            double value = healthRegen;
            Stats thisStat = Stats.HEALTHREGEN;
            if (map2.get(thisStat) != null) value += map2.get(thisStat);
            if (map.get(thisStat)[rarity] != 0) {
                value += map.get(thisStat)[rarity];
                ref = ChatColor.BLUE + " (" + addPlusIfPositive(map.get(thisStat)[rarity]) + ")";
            }
            lore.add(stat + ChatColor.RED + addPlusIfPositive(value) + ref);
            healthRegen = value;
        } // Health Regen
        if (manaRegen != 0 || map.get(Stats.MANAREGEN)[rarity] != 0 || (map2.get(Stats.MANAREGEN) != null && map2.get(Stats.MANAREGEN) != 0)) {
            String stat = ChatColor.GRAY + "Mana Regen: ";
            String ref = "";
            double value = manaRegen;
            Stats thisStat = Stats.MANAREGEN;
            if (map2.get(thisStat) != null) value += map2.get(thisStat);
            if (map.get(thisStat)[rarity] != 0) {
                value += map.get(thisStat)[rarity];
                ref = ChatColor.BLUE + " (" + addPlusIfPositive(map.get(thisStat)[rarity]) + ")";
            }
            lore.add(stat + ChatColor.AQUA + addPlusIfPositive(value) + ref);
            manaRegen = value;
        } // Mana Regen
        if (luck != 0 || map.get(Stats.LUCK)[rarity] != 0 || (map2.get(Stats.LUCK) != null && map2.get(Stats.LUCK) != 0)) {
            String stat = ChatColor.GRAY + "Luckiness: ";
            String ref = "";
            double value = luck;
            Stats thisStat = Stats.LUCK;
            if (map2.get(thisStat) != null) value += map2.get(thisStat);
            if (map.get(thisStat)[rarity] != 0) {
                value += map.get(thisStat)[rarity];
                ref = ChatColor.BLUE + " (" + addPlusIfPositive(map.get(thisStat)[rarity]) + ")";
            }
            lore.add(stat + ChatColor.GREEN + addPlusIfPositive(value) + ref);
        } // Luck
        boolean b = damage != 0 || strength != 0 || critChance != 0 || critDamage != 0 || ferocity != 0 || attackSpeed != 0 || health != 0 || defense != 0 || speed != 0 || intelligence != 0 || agility != 0 || healthRegen != 0 || manaRegen != 0 || luck != 0;
        if (b) lore.add(" ");
    }

    public static void additiveStatLore(List<String> lore, double damage, double strength, double critChance, double critDamage, double ferocity, double attackSpeed, double health, double defense, double speed, double intelligence, double agility, double healthRegen, double manaRegen, double luck) {
        String baseText = ChatColor.GRAY + "Grants {0} {1}" + ChatColor.GRAY + ".";

        if (damage != 0) lore.add(format(baseText, ChatColor.GREEN + String.valueOf(damage), DAMAGE));
        if (strength != 0) lore.add(format(baseText, ChatColor.GREEN + String.valueOf(strength), STRENGTH));
        if (critChance != 0) lore.add(format(baseText, ChatColor.GREEN + String.valueOf(critChance), CRITCHANCE));
        if (critDamage != 0) lore.add(format(baseText, ChatColor.GREEN + String.valueOf(damage), CRITDAMAGE));

        if (ferocity != 0) lore.add(format(baseText, ChatColor.GREEN + String.valueOf(ferocity), FEROCITY));
        if (attackSpeed != 0) lore.add(format(baseText, ChatColor.GREEN + String.valueOf(attackSpeed), ATTACKSPEED));

        if (health != 0) lore.add(format(baseText, ChatColor.GREEN + String.valueOf(health), HEALTH));
        if (defense != 0) lore.add(format(baseText, ChatColor.GREEN + String.valueOf(defense), DEFENSE));
        if (speed != 0) lore.add(format(baseText, ChatColor.GREEN + String.valueOf(speed), SPEED));
        if (intelligence != 0) lore.add(format(baseText, ChatColor.GREEN + String.valueOf(intelligence), INTELLIGENCE));
        if (agility != 0) lore.add(format(baseText, ChatColor.GREEN + String.valueOf(agility), AGILITY));

        if (healthRegen != 0) lore.add(format(baseText, ChatColor.GREEN + String.valueOf(healthRegen), HEALTHREGEN));
        if (manaRegen != 0) lore.add(format(baseText, ChatColor.GREEN + String.valueOf(manaRegen), MANAREGEN));

        if (luck != 0) lore.add(format(baseText, ChatColor.GREEN + String.valueOf(luck), LUCK));
        if (
                damage != 0 || strength != 0 || critChance != 0 || critDamage != 0 ||
                        ferocity != 0 || attackSpeed != 0 ||
                        health != 0 || defense != 0 || speed != 0 || intelligence != 0 || agility != 0 ||
                        healthRegen != 0 || manaRegen != 0 ||
                        luck != 0
        ) {
            lore.add(" ");
        }
    }

    public static void multiplicativeStatLore(List<String> lore, double damage, double strength, double critChance, double critDamage, double ferocity, double attackSpeed, double health, double defense, double speed, double intelligence, double agility, double healthRegen, double manaRegen, double luck) {
        String baseText1 = ChatColor.GRAY + "Grants x{0} {1}.";
        String baseText2 = ChatColor.GRAY + "Divides {1} by {0}!";

        if (damage > 1) {
            lore.add(format(baseText1, ChatColor.GREEN + String.valueOf(damage), DAMAGE));
        } else if (damage < 1) {
            lore.add(format(baseText2, ChatColor.GREEN + String.valueOf(getDenominator(damage)), DAMAGE));
        }
        if (strength > 1) {
            lore.add(format(baseText1, ChatColor.GREEN + String.valueOf(strength), STRENGTH));
        } else if (strength < 1) {
            lore.add(format(baseText2, ChatColor.GREEN + String.valueOf(getDenominator(strength)), STRENGTH));
        }
        if (critChance > 1) {
            lore.add(format(baseText1, ChatColor.GREEN + String.valueOf(critChance), CRITCHANCE));
        } else if (critChance < 1) {
            lore.add(format(baseText2, ChatColor.GREEN + String.valueOf(getDenominator(critChance)), CRITCHANCE));
        }
        if (critDamage > 1) {
            lore.add(format(baseText1, ChatColor.GREEN + String.valueOf(critDamage), CRITDAMAGE));
        } else if (critDamage < 1) {
            lore.add(format(baseText2, ChatColor.GREEN + String.valueOf(getDenominator(critDamage)), CRITDAMAGE));
        }
        if (ferocity > 1) {
            lore.add(format(baseText1, ChatColor.GREEN + String.valueOf(ferocity), FEROCITY));
        } else if (ferocity < 1) {
            lore.add(format(baseText2, ChatColor.GREEN + String.valueOf(getDenominator(ferocity)), FEROCITY));
        }
        if (attackSpeed > 1) {
            lore.add(format(baseText1, ChatColor.GREEN + String.valueOf(attackSpeed), ATTACKSPEED));
        } else if (attackSpeed < 1) {
            lore.add(format(baseText2, ChatColor.GREEN + String.valueOf(getDenominator(attackSpeed)), ATTACKSPEED));
        }
        if (health > 1) {
            lore.add(format(baseText1, ChatColor.GREEN + String.valueOf(health), HEALTH));
        } else if (health < 1) {
            lore.add(format(baseText2, ChatColor.GREEN + String.valueOf(getDenominator(health)), HEALTH));
        }
        if (defense > 1) {
            lore.add(format(baseText1, ChatColor.GREEN + String.valueOf(defense), DEFENSE));
        } else if (defense < 1) {
            lore.add(format(baseText2, ChatColor.GREEN + String.valueOf(getDenominator(defense)), DEFENSE));
        }
        if (speed > 1) {
            lore.add(format(baseText1, ChatColor.GREEN + String.valueOf(speed), SPEED));
        } else if (speed < 1) {
            lore.add(format(baseText2, ChatColor.GREEN + String.valueOf(getDenominator(speed)), SPEED));
        }
        if (intelligence > 1) {
            lore.add(format(baseText1, ChatColor.GREEN + String.valueOf(intelligence), INTELLIGENCE));
        } else if (intelligence < 1) {
            lore.add(format(baseText2, ChatColor.GREEN + String.valueOf(getDenominator(intelligence)), INTELLIGENCE));
        }
        if (agility > 1) {
            lore.add(format(baseText1, ChatColor.GREEN + String.valueOf(agility), AGILITY));
        } else if (agility < 1) {
            lore.add(format(baseText2, ChatColor.GREEN + String.valueOf(getDenominator(agility)), AGILITY));
        }
        if (healthRegen > 1) {
            lore.add(format(baseText1, ChatColor.GREEN + String.valueOf(healthRegen), HEALTHREGEN));
        } else if (healthRegen < 1) {
            lore.add(format(baseText2, ChatColor.GREEN + String.valueOf(getDenominator(healthRegen)), HEALTHREGEN));
        }
        if (manaRegen > 1) {
            lore.add(format(baseText1, ChatColor.GREEN + String.valueOf(manaRegen), MANAREGEN));
        } else if (manaRegen < 1) {
            lore.add(format(baseText2, ChatColor.GREEN + String.valueOf(getDenominator(manaRegen)), MANAREGEN));
        }
        if (luck > 1) {
            lore.add(format(baseText1, ChatColor.GREEN + String.valueOf(luck), LUCK));
        } else if (luck < 1) {
            lore.add(format(baseText2, ChatColor.GREEN + String.valueOf(getDenominator(luck)), LUCK));
        }
        if (
                damage != 1 || strength != 1 || critChance != 1 || critDamage != 1 ||
                        ferocity != 1 || attackSpeed != 1 ||
                        health != 1 || defense != 1 || speed != 1 || intelligence != 1 || agility != 1 ||
                        healthRegen != 1 || manaRegen != 1 ||
                        luck != 1
        ) {
            lore.add(" ");
        }
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return the input unchanged if it's null or empty
        }

        // Capitalize the first letter and make the rest lowercase
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public static List<String> addSkillDescription(String template, int maxCharCount, long[] args, ChatColor color1, ChatColor color2) {
        String formatted = template;

        // Replace placeholders in the template with colored arguments
        for (int i = 0; i < args.length; i++) {
            String placeholder = "{" + i + "}";
            String coloredArg = color2 + numberFormat(args[i]) + color1;  // Apply the placeholder color
            formatted = formatted.replace(placeholder, coloredArg);
        }

        // Split the formatted string into words
        String[] words = formatted.split(" ");
        List<String> result = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        // Variable to track character count
        int charCount = 0;

        // Append words and collect into lines based on character limit
        for (String word : words) {
            int wordLength = word.length();

            // Check if adding this word would exceed the max character count
            if (charCount + wordLength + (!currentLine.isEmpty() ? 1 : 0) > maxCharCount) {
                // If so, add the current line to the result list and reset for the next line
                result.add(currentLine.toString().trim());
                currentLine.setLength(0);
                charCount = 0;  // Reset character count
            }

            // Add the word to the current line
            currentLine.append(word).append(" ");
            charCount += wordLength + 1;  // Include the space after the word
        }

        // Add any remaining words in the current line
        if (!currentLine.isEmpty()) {
            result.add(currentLine.toString().trim());
        }

        return result;
    }

/*
    public static List<String> addSkillDescription(String template, int wordsPerLine, long[] args, ChatColor color1, ChatColor color2) {
        String formatted = template;
        for (int i = 0; i < args.length; i++) {
            String placeholder = "{" + i + "}";
            String coloredArg = color2 + numberFormat(args[i]) + color1;  // Apply the placeholder color
            formatted = formatted.replace(placeholder, coloredArg);
        }

        // Split the formatted string into words
        String[] words = formatted.split(" ");
        List<String> result = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        // Append words and collect into lines after a certain number of words
        int wordCount = 0;
        for (String word : words) {
            currentLine.append(color1).append(word).append(" ");
            wordCount++;

            // When we reach the word limit for a line, add the current line to the list
            if (wordCount >= wordsPerLine) {
                result.add(currentLine.toString().trim());
                currentLine.setLength(0);  // Clear the StringBuilder for the next line
                wordCount = 0;  // Reset the word count
            }
        }

        // Add the last line if any remaining words
        if (!currentLine.isEmpty()) {
            result.add(currentLine.toString().trim());
        }

        return result;  // Return the list of split lines
    }
 */
}
