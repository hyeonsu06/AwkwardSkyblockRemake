package io.hyonsu06.core;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.items.items.ItemAdditiveBonus;
import io.hyonsu06.core.annotations.items.items.ItemMultiplicativeBonus;
import io.hyonsu06.core.annotations.items.reforge.ReforgeMetadata;
import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.annotations.tags.SkillTagged;
import io.hyonsu06.core.enums.ItemType;
import io.hyonsu06.core.enums.ReforgeType;
import io.hyonsu06.core.enums.Stats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.hyonsu06.Main.plugin;
import static io.hyonsu06.command.items.LoadItems.WORDS_PER_LINE;
import static io.hyonsu06.core.StatManager.rarityToIndex;
import static io.hyonsu06.core.enums.ItemRarity.next;
import static io.hyonsu06.core.functions.ItemTypeForSlot.getReforgeType;
import static io.hyonsu06.core.functions.NumberTweaks.*;
import static io.hyonsu06.core.functions.getClasses.getItemClasses;
import static io.hyonsu06.core.functions.getClasses.getReforgeClasses;
import static io.hyonsu06.core.functions.getMagicDamage.magicDamage;
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

    public Refresher() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    for (int slot = 0; slot < p.getInventory().getSize(); slot++) {
                        if (p.getInventory().getItem(slot) != null) p.getInventory().setItem(slot, refreshItemVisuals(p, p.getInventory().getItem(slot)));
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public ItemStack refreshItemVisuals(Player player, ItemStack item) {
        // Check if item has ItemMetadata
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.getPersistentDataContainer().has(getPDC("id"), PersistentDataType.STRING)) return item; // No valid item metadata

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
        if (temp1 == null) isRecombobulatorPresent = false; else isRecombobulatorPresent = (boolean) temp1;
        if (temp2 == null) isRecombobulatorEXPresent = false; else isRecombobulatorEXPresent = (boolean) temp2;

        int potatoBooks;
        Object temp3 = meta.getPersistentDataContainer().get(getPDC("potato"), PersistentDataType.INTEGER);
        if (temp3 == null) potatoBooks = 0; else potatoBooks = (int) temp3;

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
                        getReforgeType(clazz.getAnnotation(ItemMetadata.class).type())
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
                        getReforgeType(clazz.getAnnotation(ItemMetadata.class).type())
                );
            } else baseStatLore(
                    lore,
                    stats.damage(), stats.strength(), stats.critChance(), stats.critDamage(),
                    stats.ferocity(), stats.attackSpeed(),
                    stats.health(), stats.defense(), stats.speed(), stats.intelligence(), stats.agility(),
                    stats.healthRegen(), stats.manaRegen(),
                    stats.luck(),
                    reforgeMap, rarityToIndex(metadata.rarity()),
                    potatoBooks,
                    getReforgeType(clazz.getAnnotation(ItemMetadata.class).type())
            );
        }

        if (clazz.isAnnotationPresent(ItemAdditiveBonus.class)) {
            ItemAdditiveBonus bonus1 = clazz.getAnnotation(ItemAdditiveBonus.class);
            additiveStatLore(
                    lore,
                    bonus1.damage(), bonus1.strength(), bonus1.critChance(), bonus1.critDamage(),
                    bonus1.ferocity(), bonus1.attackSpeed(),
                    bonus1.health(), bonus1.defense(), bonus1.speed(), bonus1.intelligence(), bonus1.agility(),
                    bonus1.healthRegen(), bonus1.manaRegen(),
                    bonus1.luck()
            );
        }

        if (clazz.isAnnotationPresent(ItemMultiplicativeBonus.class)) {
            ItemMultiplicativeBonus bonus2 = clazz.getAnnotation(ItemMultiplicativeBonus.class);
            multiplicativeStatLore(
                    lore,
                    bonus2.damage(), bonus2.strength(), bonus2.critChance(), bonus2.critDamage(),
                    bonus2.ferocity(), bonus2.attackSpeed(),
                    bonus2.health(), bonus2.defense(), bonus2.speed(), bonus2.intelligence(), bonus2.agility(),
                    bonus2.healthRegen(), bonus2.manaRegen(),
                    bonus2.luck()
            );
        }

        // Add additional lore if present
        if (!metadata.description().isEmpty()) {
            lore.addAll(addSkillDescription(metadata.description(), WORDS_PER_LINE, new long[]{}, ChatColor.GRAY, ChatColor.AQUA));
            lore.add(" ");
        }

        // Add skills lore
        for (Class<?> clazz2 : metadata.skills()) {
            Skill skills = clazz2.getAnnotation(Skill.class);
            for (Method method : clazz2.getMethods()) {
                if (method.isAnnotationPresent(SkillTagged.class)) {
                    if (!skills.name().isEmpty()) {
                        if (!skills.simpleDescription()) {
                            lore.add(format(ChatColor.GOLD + "Ability: {0}  {1}",
                                    skills.name(),
                                    ChatColor.BOLD + "", ChatColor.YELLOW + method.getName())
                            );

                            long[] args = skills.args();
                            args[0] = magicDamage(player, args[0]);
                            lore.addAll(addSkillDescription(skills.description(), WORDS_PER_LINE, args, ChatColor.GRAY, ChatColor.AQUA));

                            if (skills.cost() != 0)
                                lore.add(format(ChatColor.DARK_GRAY + "Mana cost: {0}", ChatColor.AQUA + String.valueOf(skills.cost())));
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
            }
        }

        long customDurability = 0;
        Long customCurrentDamage = item.getItemMeta().getPersistentDataContainer().get(getPDC("damage"), PersistentDataType.LONG);
        if (customCurrentDamage == null) customCurrentDamage = 0L;
        for (Class<?> clazz2 : getItemClasses()) if (clazz2.getAnnotation(ItemMetadata.class).ID().equals(getItemID(item))) customDurability = clazz2.getAnnotation(ItemMetadata.class).durability();

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
        } else lore.add(color + "" + ChatColor.BOLD + metadata.rarity().getDisplay().toUpperCase() + " " + metadata.type().getDisplay().toUpperCase());
        meta.setLore(lore);

        // Reapply meta to item
        item.setItemMeta(meta);
        item.setDurability(finalDurability);
        item.setType(metadata.material());
        return item;
    }

    private Class<?> findClassById(String id) {
        for (Class<?> clazz : getItemClasses()) {
            if (clazz.getAnnotation(ItemMetadata.class).ID().equals(id)) {
                return clazz;
            }
        }
        return null; // Placeholder return
    }

    public static void baseStatLore(List<String> lore, double damage, double strength, double critChance, double critDamage, double ferocity, double attackSpeed, double health, double defense, double speed, double intelligence, double agility, double healthRegen, double manaRegen, double luck, Map<Stats, Double[]> map, int rarity, int potato, ReforgeType type) {
        Double[] defaultMap = new Double[]{0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d};
        map.putIfAbsent(Stats.DAMAGE, defaultMap);
        map.putIfAbsent(Stats.STRENGTH, defaultMap);
        map.putIfAbsent(Stats.CRITCHANCE, defaultMap);
        map.putIfAbsent(Stats.CRITDAMAGE, defaultMap);

        map.putIfAbsent(Stats.FEROCITY, defaultMap);
        map.putIfAbsent(Stats.ATTACKSPEED, defaultMap);

        map.putIfAbsent(Stats.HEALTH, defaultMap);
        map.putIfAbsent(Stats.DEFENSE, defaultMap);
        map.putIfAbsent(Stats.SPEED, defaultMap);
        map.putIfAbsent(Stats.INTELLIGENCE, defaultMap);
        map.putIfAbsent(Stats.AGILITY, defaultMap);

        map.putIfAbsent(Stats.HEALTHREGEN, defaultMap);
        map.putIfAbsent(Stats.MANAREGEN, defaultMap);

        map.putIfAbsent(Stats.LUCK, defaultMap);

        if (damage != 0 || map.get(Stats.DAMAGE)[rarity] != 0 || (potato > 0 && (type == ReforgeType.MELEE || type == ReforgeType.RANGED))) {
            String stat = ChatColor.GRAY + "Damage: ";
            String ref = "", potatoBook = "";
            double value = damage;
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
        if (strength != 0 || map.get(Stats.STRENGTH)[rarity] != 0 || (potato > 0 && (type == ReforgeType.MELEE || type == ReforgeType.RANGED))) {
            String stat = ChatColor.GRAY + "Strength: ";
            String ref = "", potatoBook = "";
            double value = strength;
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
        if (critChance != 0 || map.get(Stats.CRITCHANCE)[rarity] != 0) {
            String stat = ChatColor.GRAY + "Crit Damage: ";
            String value = ChatColor.RED + addPlusIfPositive(critChance);
            if (map.get(Stats.CRITCHANCE)[rarity] != 0) {
                double number = critChance + map.get(Stats.CRITCHANCE)[rarity];
                value = ChatColor.RED + addPlusIfPositive(number) + ChatColor.BLUE + " (+" + map.get(Stats.CRITCHANCE)[rarity] + ")";
            }
            lore.add(stat + value);
        } // Crit Chance
        if (critDamage != 0 || map.get(Stats.CRITDAMAGE)[rarity] != 0) {
            String stat = ChatColor.GRAY + "Crit Damage: ";
            String value = ChatColor.RED + addPlusIfPositive(critDamage);
            if (map.get(Stats.CRITDAMAGE)[rarity] != 0) {
                double number = critDamage + map.get(Stats.CRITDAMAGE)[rarity];
                value = ChatColor.RED + addPlusIfPositive(number) + ChatColor.BLUE + " (+" + map.get(Stats.CRITDAMAGE)[rarity] + ")";
            }
            lore.add(stat + value);
        } // Crit Damage
        if (ferocity != 0 || map.get(Stats.FEROCITY)[rarity] != 0) {
            String stat = ChatColor.GRAY + "Ferocity: ";
            String value = ChatColor.RED + addPlusIfPositive(ferocity);
            if (map.get(Stats.FEROCITY)[rarity] != 0) {
                double number = ferocity + map.get(Stats.FEROCITY)[rarity];
                value = ChatColor.RED + addPlusIfPositive(number) + ChatColor.BLUE + " (+" + map.get(Stats.FEROCITY)[rarity] + ")";
            }
            lore.add(stat + value);
        } // Ferocity
        if (attackSpeed != 0 || map.get(Stats.ATTACKSPEED)[rarity] != 0) {
            String stat = ChatColor.GRAY + "Bonus Attack Speed: ";
            String value = ChatColor.YELLOW + addPlusIfPositive(attackSpeed);
            if (map.get(Stats.ATTACKSPEED)[rarity] != 0) {
                double number = attackSpeed + map.get(Stats.ATTACKSPEED)[rarity];
                value = ChatColor.YELLOW + addPlusIfPositive(number) + ChatColor.BLUE + " (+" + map.get(Stats.ATTACKSPEED)[rarity] + ")";
            }
            lore.add(stat + value);
        } // Attack Speed
        if (health != 0 || map.get(Stats.HEALTH)[rarity] != 0 || (potato > 0 && type == ReforgeType.ARMOR)) {
            String stat = ChatColor.GRAY + "Health: ";
            String ref = "", potatoBook = "";
            double value = health;
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
        if (defense != 0 || map.get(Stats.DEFENSE)[rarity] != 0 || (potato > 0 && type == ReforgeType.ARMOR)) {
            String stat = ChatColor.GRAY + "Defense: ";
            String ref = "", potatoBook = "";
            double value = defense;
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
        if (speed != 0 || map.get(Stats.SPEED)[rarity] != 0) {
            String stat = ChatColor.GRAY + "Walk Speed: ";
            String value = ChatColor.GREEN + addPlusIfPositive(speed);
            if (map.get(Stats.SPEED)[rarity] != 0) {
                double number = speed + map.get(Stats.SPEED)[rarity];
                value = ChatColor.GREEN + addPlusIfPositive(number) + ChatColor.BLUE + " (+" + map.get(Stats.SPEED)[rarity] + ")";
            }
            lore.add(stat + value);
        } // Speed
        if (intelligence != 0 || map.get(Stats.INTELLIGENCE)[rarity] != 0) {
            String stat = ChatColor.GRAY + "Intelligence: ";
            String value = ChatColor.AQUA + addPlusIfPositive(intelligence);
            if (map.get(Stats.INTELLIGENCE)[rarity] != 0) {
                double number = intelligence + map.get(Stats.INTELLIGENCE)[rarity];
                value = ChatColor.AQUA + addPlusIfPositive(number) + ChatColor.BLUE + " (+" + map.get(Stats.INTELLIGENCE)[rarity] + ")";
            }
            lore.add(stat + value);
        } // Intelligence
        if (agility != 0 || map.get(Stats.AGILITY)[rarity] != 0) {
            String stat = ChatColor.GRAY + "Agility: ";
            String value = ChatColor.GREEN + addPlusIfPositive(agility);
            if (map.get(Stats.AGILITY)[rarity] != 0) {
                double number = agility + map.get(Stats.AGILITY)[rarity];
                value = ChatColor.GREEN + addPlusIfPositive(number) + ChatColor.BLUE + " (+" + map.get(Stats.AGILITY)[rarity] + ")";
            }
            lore.add(stat + value);
        } // Agility
        if (healthRegen != 0 || map.get(Stats.HEALTHREGEN)[rarity] != 0) {
            String stat = ChatColor.GRAY + "Health Regen: ";
            String value = ChatColor.GREEN + addPlusIfPositive(healthRegen);
            if (map.get(Stats.HEALTHREGEN)[rarity] != 0) {
                double number = healthRegen + map.get(Stats.HEALTHREGEN)[rarity];
                value = ChatColor.GREEN + addPlusIfPositive(number) + ChatColor.BLUE + " (+" + map.get(Stats.HEALTHREGEN)[rarity] + ")";
            }
            lore.add(stat + value);
        } // Health Regen
        if (manaRegen != 0 || map.get(Stats.MANAREGEN)[rarity] != 0) {
            String stat = ChatColor.GRAY + "Mana Regen: ";
            String value = ChatColor.GREEN + addPlusIfPositive(manaRegen);
            if (map.get(Stats.MANAREGEN)[rarity] != 0) {
                double number = manaRegen + map.get(Stats.MANAREGEN)[rarity];
                value = ChatColor.GREEN + addPlusIfPositive(number) + ChatColor.BLUE + " (+" + map.get(Stats.MANAREGEN)[rarity] + ")";
            }
            lore.add(stat + value);
        } // Mana Regen
        if (luck != 0 || map.get(Stats.LUCK)[rarity] != 0) {
            String stat = ChatColor.GRAY + "Luckiness: ";
            String value = ChatColor.GREEN + addPlusIfPositive(luck);
            if (map.get(Stats.LUCK)[rarity] != 0) {
                double number = luck + map.get(Stats.LUCK)[rarity];
                value = ChatColor.GREEN + addPlusIfPositive(number) + ChatColor.BLUE + " (+" + map.get(Stats.LUCK)[rarity] + ")";
            }
            lore.add(stat + value);
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
}
