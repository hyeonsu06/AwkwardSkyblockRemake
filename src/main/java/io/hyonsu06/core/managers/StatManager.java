package io.hyonsu06.core.managers;

import io.hyonsu06.command.accessories.AccessoriesUtils;
import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.items.items.ItemAdditiveBonus;
import io.hyonsu06.core.annotations.items.items.ItemMultiplicativeBonus;
import io.hyonsu06.core.annotations.items.reforge.ReforgeMetadata;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ReforgeType;
import io.hyonsu06.core.enums.Stats;
import io.hyonsu06.core.enums.ItemType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static io.hyonsu06.Main.plugin;
import static io.hyonsu06.core.Refresher.*;
import static io.hyonsu06.core.enums.ItemRarity.next;
import static io.hyonsu06.core.functions.ArmorTweaks.getItems;
import static io.hyonsu06.core.functions.ItemTypeForSlot.getReforgeType;
import static io.hyonsu06.core.functions.MapPDCConverter.PDCToMap;
import static io.hyonsu06.core.functions.WeaponType.itemTypeFromItemStack;
import static io.hyonsu06.core.functions.convertStringTypeToItemType.stringTypeToItemType;
import static io.hyonsu06.core.functions.getClasses.getItemClasses;
import static io.hyonsu06.core.functions.getClasses.getReforgeClasses;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.*;
import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.persistence.PersistentDataType.STRING;

public class StatManager {
    private static Map<UUID, Map<EquipmentSlot, Map<Stats, Double>>> itemStatMap = new HashMap<>();
    private static Map<UUID, Map<EquipmentSlot, Map<Stats, Double>>> itemAdditiveStatMap = new HashMap<>();
    private static Map<UUID, Map<EquipmentSlot, Map<Stats, Double>>> itemMultiplicativeStatMap = new HashMap<>();

    private static Map<UUID, Map<Integer, Map<Stats, Double>>> accessoryStatMap = new HashMap<>();
    private static Map<UUID, Map<Integer, Map<Stats, Double>>> accessoryAdditiveStatMap = new HashMap<>();
    private static Map<UUID, Map<Integer, Map<Stats, Double>>> accessoryMultiplicativeStatMap = new HashMap<>();

    private static Map<UUID, Map<Integer, Map<Stats, Double>>> reforgeStatMap = new HashMap<>();

    @Getter
    @Setter
    private static Map<UUID, Map<Stats, Double>> skillBonusMap = new HashMap<>();

    @Getter
    private static Map<UUID, Map<EquipmentSlot, Map<Stats, Double>>> enchantStatMap = new HashMap<>();

    @Getter
    @Setter
    public static Map<UUID, Map<Stats, Double>> baseStatMap = new HashMap<>();
    @Getter
    private static Map<UUID, Map<Stats, Double>> finalStatMap = new HashMap<>();

    public static EquipmentSlot[] slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.BODY, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND};

    double FEROCITY_CAP = 400;
    double ATTACK_SPEED_CAP = 100;
    double SPEED_CAP = 500;
    double AGILITY_CAP = 100;
    double SWING_RANGE_CAP = 32;

    public static StatManager instance = null;

    public static StatManager instance() {
        if (instance == null) {
            instance = new StatManager();
        }
        return instance;
    }

    public static void clear() {
        instance = null;
        baseStatMap = null;
        reforgeStatMap = null;
        enchantStatMap = null;
    }

    public StatManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof LivingEntity e) {
                            initMap(e);
                            for (Class<?> clazz : getItemClasses()) {
                                ItemStack[] items = getItems(e);
                                for (int i = 0; i < items.length; i++) {
                                    ItemStack item = items[i];
                                    ItemMetadata meta = clazz.getAnnotation(ItemMetadata.class);
                                    if (item != null) {
                                        String id = getItemID(item);
                                        if (id == null) continue;
                                        if (id.equals(meta.ID())) {
                                            if (i == 0 && meta.type() == ItemType.HELMET) {
                                                applyStat(
                                                        e,
                                                        EquipmentSlot.HEAD,
                                                        clazz.getAnnotation(ItemStats.class),
                                                        clazz.getAnnotation(ItemAdditiveBonus.class),
                                                        clazz.getAnnotation(ItemMultiplicativeBonus.class)
                                                );
                                            } else if (i == 1 && meta.type() == ItemType.CHESTPLATE) {
                                                applyStat(
                                                        e,
                                                        EquipmentSlot.BODY,
                                                        clazz.getAnnotation(ItemStats.class),
                                                        clazz.getAnnotation(ItemAdditiveBonus.class),
                                                        clazz.getAnnotation(ItemMultiplicativeBonus.class)
                                                );
                                            } else if (i == 2 && meta.type() == ItemType.LEGGINGS) {
                                                applyStat(
                                                        e,
                                                        EquipmentSlot.LEGS,
                                                        clazz.getAnnotation(ItemStats.class),
                                                        clazz.getAnnotation(ItemAdditiveBonus.class),
                                                        clazz.getAnnotation(ItemMultiplicativeBonus.class)
                                                );
                                            } else if (i == 3 && meta.type() == ItemType.BOOTS) {
                                                applyStat(
                                                        e,
                                                        EquipmentSlot.FEET,
                                                        clazz.getAnnotation(ItemStats.class),
                                                        clazz.getAnnotation(ItemAdditiveBonus.class),
                                                        clazz.getAnnotation(ItemMultiplicativeBonus.class)
                                                );
                                            } else if (i == 4 && !(meta.type() == ItemType.HELMET || meta.type() == ItemType.CHESTPLATE || meta.type() == ItemType.LEGGINGS || meta.type() == ItemType.BOOTS) && meta.type() != ItemType.ACCESSORY) {
                                                applyStat(
                                                        e,
                                                        EquipmentSlot.HAND,
                                                        clazz.getAnnotation(ItemStats.class),
                                                        clazz.getAnnotation(ItemAdditiveBonus.class),
                                                        clazz.getAnnotation(ItemMultiplicativeBonus.class)
                                                );
                                            }
                                        }
                                    }
                                }
                            }
                            calcFinalWithCaps(e);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void applyStat(LivingEntity e, EquipmentSlot slot, ItemStats value1, ItemAdditiveBonus value2, ItemMultiplicativeBonus value3) {
        ItemStack item = null;
        if (slot == EquipmentSlot.HEAD) item = e.getEquipment().getHelmet();
        else if (slot == EquipmentSlot.BODY) item = e.getEquipment().getChestplate();
        else if (slot == EquipmentSlot.LEGS) item = e.getEquipment().getLeggings();
        else if (slot == EquipmentSlot.FEET) item = e.getEquipment().getBoots();
        else if (slot == EquipmentSlot.HAND) item = e.getEquipment().getItemInMainHand();

        Map<Stats, Double> defaultMap = new EnumMap<>(Stats.class);
        for (Stats stat : Stats.values()) defaultMap.put(stat, 0d);

        Map<Stats, Double> defaultMapMul = new EnumMap<>(Stats.class);
        for (Stats stat : Stats.values()) defaultMapMul.put(stat, 1d);

        int potato_damage = 0, potato_strength = 0, potato_health = 0, potato_defense = 0;

        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                int count;
                Object temp3 = meta.getPersistentDataContainer().get(getPDC("potato"), PersistentDataType.INTEGER);
                if (temp3 == null) count = 0;
                else count = (int) temp3;

                String type = item.getItemMeta().getPersistentDataContainer().get(getPDC("type"), STRING);
                if (getReforgeType(stringTypeToItemType(type)) == ReforgeType.MELEE) {
                    potato_damage = count * POTATO_BOOK_WEAPON_DAMAGE;
                    potato_strength = count * POTATO_BOOK_WEAPON_STRENGTH;
                }
                if (getReforgeType(stringTypeToItemType(type)) == ReforgeType.ARMOR) {
                    potato_health = count * POTATO_BOOK_ARMOR_HEALTH;
                    potato_defense = count * POTATO_BOOK_ARMOR_DEFENSE;
                }
            }
        }

        if (value1 != null) {
            Map<Stats, Double> temp = new EnumMap<>(Stats.class);
            temp.put(Stats.DAMAGE, value1.damage() + potato_damage);
            temp.put(Stats.STRENGTH, value1.strength() + potato_strength);
            temp.put(Stats.CRITCHANCE, value1.critChance());
            temp.put(Stats.CRITDAMAGE, value1.critDamage());

            temp.put(Stats.FEROCITY, value1.ferocity());
            temp.put(Stats.ATTACKSPEED, value1.attackSpeed());

            temp.put(Stats.HEALTH, value1.health() + potato_health);
            temp.put(Stats.DEFENSE, value1.defense() + potato_defense);
            temp.put(Stats.SPEED, value1.speed());
            temp.put(Stats.INTELLIGENCE, value1.intelligence());
            temp.put(Stats.AGILITY, value1.agility());

            temp.put(Stats.HEALTHREGEN, value1.healthRegen());
            temp.put(Stats.MANAREGEN, value1.manaRegen());

            temp.put(Stats.SWING_RANGE, value1.swingRange());

            temp.put(Stats.LUCK, value1.luck());
            itemStatMap.get(e.getUniqueId()).put(slot, temp);
        } else {
            itemStatMap.get(e.getUniqueId()).put(slot, defaultMap);
        }

        if (value2 != null) {
            Map<Stats, Double> temp2 = new EnumMap<>(Stats.class);
            temp2.put(Stats.DAMAGE, value2.add_damage());
            temp2.put(Stats.STRENGTH, value2.add_strength());
            temp2.put(Stats.CRITCHANCE, value2.add_critChance());
            temp2.put(Stats.CRITDAMAGE, value2.add_critDamage());

            temp2.put(Stats.FEROCITY, value2.add_ferocity());
            temp2.put(Stats.ATTACKSPEED, value2.add_attackSpeed());

            temp2.put(Stats.HEALTH, value2.add_health());
            temp2.put(Stats.DEFENSE, value2.add_defense());
            temp2.put(Stats.SPEED, value2.add_speed());
            temp2.put(Stats.INTELLIGENCE, value2.add_intelligence());
            temp2.put(Stats.AGILITY, value2.add_agility());

            temp2.put(Stats.HEALTHREGEN, value2.add_healthRegen());
            temp2.put(Stats.MANAREGEN, value2.add_manaRegen());

            temp2.put(Stats.SWING_RANGE, value2.add_swingRange());

            temp2.put(Stats.LUCK, value2.add_luck());
            itemAdditiveStatMap.get(e.getUniqueId()).put(slot, temp2);
        } else {
            itemAdditiveStatMap.get(e.getUniqueId()).put(slot, defaultMap);
        }

        if (value3 != null) {
            Map<Stats, Double> temp = new EnumMap<>(Stats.class);
            temp.put(Stats.DAMAGE, value3.mul_damage());
            temp.put(Stats.STRENGTH, value3.mul_strength());
            temp.put(Stats.CRITCHANCE, value3.mul_critChance());
            temp.put(Stats.CRITDAMAGE, value3.mul_critDamage());

            temp.put(Stats.FEROCITY, value3.mul_ferocity());
            temp.put(Stats.ATTACKSPEED, value3.mul_attackSpeed());

            temp.put(Stats.HEALTH, value3.mul_health());
            temp.put(Stats.DEFENSE, value3.mul_defense());
            temp.put(Stats.SPEED, value3.mul_speed());
            temp.put(Stats.INTELLIGENCE, value3.mul_intelligence());
            temp.put(Stats.AGILITY, value3.mul_agility());

            temp.put(Stats.HEALTHREGEN, value3.mul_healthRegen());
            temp.put(Stats.MANAREGEN, value3.mul_manaRegen());

            temp.put(Stats.SWING_RANGE, value3.mul_swingRange());

            temp.put(Stats.LUCK, value3.mul_luck());
            itemMultiplicativeStatMap.get(e.getUniqueId()).put(slot, temp);
        } else {
            itemMultiplicativeStatMap.get(e.getUniqueId()).put(slot, defaultMapMul);
        }

    }

    private void applyAccessoryStat(LivingEntity e, Integer i, ItemStats value1, ItemAdditiveBonus value2, ItemMultiplicativeBonus value3) {
        Map<Stats, Double> map1 = accessoryStatMap.get(e.getUniqueId()).get(i);
        if (value1 != null) {
            if (map1 != null) {
                map1.put(Stats.DAMAGE, value1.damage());
                map1.put(Stats.STRENGTH, value1.strength());
                map1.put(Stats.CRITCHANCE, value1.critChance());
                map1.put(Stats.CRITDAMAGE, value1.critDamage());

                map1.put(Stats.FEROCITY, value1.ferocity());
                map1.put(Stats.ATTACKSPEED, value1.attackSpeed());

                map1.put(Stats.HEALTH, value1.health());
                map1.put(Stats.DEFENSE, value1.defense());
                map1.put(Stats.SPEED, value1.speed());
                map1.put(Stats.INTELLIGENCE, value1.intelligence());
                map1.put(Stats.AGILITY, value1.agility());

                map1.put(Stats.HEALTHREGEN, value1.healthRegen());
                map1.put(Stats.MANAREGEN, value1.manaRegen());

                map1.put(Stats.SWING_RANGE, value1.swingRange());

                map1.put(Stats.LUCK, value1.luck());

                accessoryStatMap.get(e.getUniqueId()).put(i, map1);
            }
        }

        Map<Stats, Double> map2 = accessoryAdditiveStatMap.get(e.getUniqueId()).get(i);
        if (value2 != null) {
            if (map2 != null) {
                map2.put(Stats.DAMAGE, value2.add_damage());
                map2.put(Stats.STRENGTH, value2.add_strength());
                map2.put(Stats.CRITCHANCE, value2.add_critChance());
                map2.put(Stats.CRITDAMAGE, value2.add_critDamage());

                map2.put(Stats.FEROCITY, value2.add_ferocity());
                map2.put(Stats.ATTACKSPEED, value2.add_attackSpeed());

                map2.put(Stats.HEALTH, value2.add_health());
                map2.put(Stats.DEFENSE, value2.add_defense());
                map2.put(Stats.SPEED, value2.add_speed());
                map2.put(Stats.INTELLIGENCE, value2.add_intelligence());
                map2.put(Stats.AGILITY, value2.add_agility());

                map2.put(Stats.HEALTHREGEN, value2.add_healthRegen());
                map2.put(Stats.MANAREGEN, value2.add_manaRegen());

                map2.put(Stats.SWING_RANGE, value2.add_swingRange());

                map2.put(Stats.LUCK, value2.add_luck());

                accessoryAdditiveStatMap.get(e.getUniqueId()).put(i, map1);
            }
        }

        Map<Stats, Double> map3 = accessoryMultiplicativeStatMap.get(e.getUniqueId()).get(i);
        if (value3 != null) {
            if (map3 != null) {
                map3.put(Stats.DAMAGE, value3.mul_damage());
                map3.put(Stats.STRENGTH, value3.mul_strength());
                map3.put(Stats.CRITCHANCE, value3.mul_critChance());
                map3.put(Stats.CRITDAMAGE, value3.mul_critDamage());

                map3.put(Stats.FEROCITY, value3.mul_ferocity());
                map3.put(Stats.ATTACKSPEED, value3.mul_attackSpeed());

                map3.put(Stats.HEALTH, value3.mul_health());
                map3.put(Stats.DEFENSE, value3.mul_defense());
                map3.put(Stats.SPEED, value3.mul_speed());
                map3.put(Stats.INTELLIGENCE, value3.mul_intelligence());
                map3.put(Stats.AGILITY, value3.mul_agility());

                map3.put(Stats.HEALTHREGEN, value3.mul_healthRegen());
                map3.put(Stats.MANAREGEN, value3.mul_manaRegen());

                map3.put(Stats.SWING_RANGE, value3.mul_swingRange());

                map3.put(Stats.LUCK, value3.mul_luck());

                accessoryMultiplicativeStatMap.get(e.getUniqueId()).put(i, map1);
            }
        }
    }

    private void applyReforgeStat(LivingEntity e, Integer i, int rarity, Map<Stats, Double[]> value1) {
        Map<Stats, Double> map = reforgeStatMap.get(e.getUniqueId()).get(i);
        if (value1 != null) {
            if (map == null) {
                map = new EnumMap<>(Stats.class);
                for (Stats s : Stats.values()) map.put(s, 0d);
            }
            for (Stats stat : Stats.values()) {
                if (value1.get(stat) != null) map.put(stat, value1.get(stat)[rarity]);
            }
            reforgeStatMap.get(e.getUniqueId()).put(i, map);
        }
    }

    private void calcFinalWithCaps(LivingEntity e) {
        UUID entityId = e.getUniqueId();
        Map<Stats, Double> baseMap = baseStatMap.get(entityId);
        Map<Stats, Double> finalStats = finalStatMap.get(entityId);

        Map<EquipmentSlot, Map<Stats, Double>> itemMap = itemStatMap.get(entityId);
        Map<EquipmentSlot, Map<Stats, Double>> addMap = itemAdditiveStatMap.get(entityId);
        Map<EquipmentSlot, Map<Stats, Double>> mulMap = itemMultiplicativeStatMap.get(entityId);

        Map<Integer, Map<Stats, Double>> accMap = accessoryStatMap.get(entityId);
        Map<Integer, Map<Stats, Double>> accAddMap = accessoryAdditiveStatMap.get(entityId);
        Map<Integer, Map<Stats, Double>> accMulMap = accessoryMultiplicativeStatMap.get(entityId);

        Map<Integer, Map<Stats, Double>> reforgeMap = reforgeStatMap.get(entityId);

        Map<Stats, Double> skillMap = skillBonusMap.get(entityId);

        for (Stats stat : Stats.values()) {
            double total;
            double base;
            if (baseMap == null) {
                e.remove();
                StatManager.remove(entityId);
                return;
            } else {
                base = baseMap.get(stat);
            }

            double accValue = 0, accAddValue = 0, accMulValue = 0, reforgeValue = 0;
            double finalBaseValue, finalAddValue = 0, finalMulValue = 1;
            if (e instanceof Player p) {
                ItemStack[] accessories = AccessoriesUtils.getAccessories().get(p.getUniqueId());
                if (accessories == null) {
                    accessories = new ItemStack[]{};
                }
                for (int i = 0; i < accessories.length; i++) {
                    ItemStack accessory = accessories[i];
                    for (Class<?> clazz : getItemClasses()) {
                        if (clazz.getAnnotation(ItemMetadata.class).ID().equals(getItemID(accessory))) {
                            applyAccessoryStat(
                                    e,
                                    i,
                                    clazz.getAnnotation(ItemStats.class),
                                    clazz.getAnnotation(ItemAdditiveBonus.class),
                                    clazz.getAnnotation(ItemMultiplicativeBonus.class)
                            );
                        }
                    }

                    accValue += accMap.get(i).get(stat);
                    accAddValue += accAddMap.get(i).get(stat);
                    accMulValue += accMulMap.get(i).get(stat) - 1;
                }

                ItemStack[] items = getItems(e);
                ItemStack[] add = AccessoriesUtils.getAccessories().get(e.getUniqueId());
                if (add == null) add = new ItemStack[]{};
                items = addItemStacks(items, add);

                for (int i = 0; i < items.length; i++) {
                    if (items[i] == null) continue;
                    ItemStack currentItem = items[i];
                    String reforge = currentItem.getPersistentDataContainer().get(getPDC("reforge"), STRING);
                    if (reforge == null) continue;
                    for (Class<?> clazz1 : getReforgeClasses()) {
                        if (reforge.toLowerCase().equalsIgnoreCase(clazz1.getAnnotation(ReforgeMetadata.class).ID())) {
                            int rarityNumber = 0;

                            boolean isRecombobulatorPresent;
                            boolean isRecombobulatorEXPresent;
                            Object temp1 = currentItem.getPersistentDataContainer().get(getPDC("recombobulator"), PersistentDataType.BOOLEAN);
                            Object temp2 = currentItem.getPersistentDataContainer().get(getPDC("recombobulatorEX"), PersistentDataType.BOOLEAN);
                            if (temp1 == null) isRecombobulatorPresent = false;
                            else isRecombobulatorPresent = (boolean) temp1;
                            if (temp2 == null) isRecombobulatorEXPresent = false;
                            else isRecombobulatorEXPresent = (boolean) temp2;

                            for (Class<?> clazz2 : getItemClasses()) {
                                if (clazz2.getAnnotation(ItemMetadata.class).ID().equals(getItemID(currentItem))) {
                                    ItemRarity rarity = clazz2.getAnnotation(ItemMetadata.class).rarity();
                                    if (isRecombobulatorPresent) {
                                        rarity = next(rarity);
                                    }
                                    if (isRecombobulatorEXPresent) {
                                        rarity = next(rarity);
                                    }

                                    rarityNumber = rarityToIndex(rarity);
                                }

                                Map<Stats, Double[]> reforgeMap2 = new HashMap<>();
                                try {
                                    for (Method m : clazz1.getDeclaredMethods()) {
                                        Object instance = clazz1.getDeclaredConstructor().newInstance();
                                        if (m.getName().equals("baseValue")) {
                                            reforgeMap2 = (Map<Stats, Double[]>) m.invoke(instance);
                                        }
                                    }
                                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                                         NoSuchMethodException ex) {
                                    getLogger().warning(ex.getMessage());
                                }


                                String itemType = currentItem.getItemMeta().getPersistentDataContainer().get(getPDC("type"), STRING).toLowerCase();
                                for (Class<?> clazz3 : getItemClasses()) {
                                    if (clazz3.getAnnotation(ItemMetadata.class).type().getDisplay().equalsIgnoreCase(itemType)) {
                                        if (isSlotMatch(i, clazz3.getAnnotation(ItemMetadata.class).type())) {
                                            applyReforgeStat(e, i, rarityNumber, reforgeMap2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (reforgeMap.get(i) != null) reforgeValue += reforgeMap.get(i).get(stat);
                }
            }

            finalBaseValue = reforgeValue;

            for (int i = 0; i < getItems(e).length; i++) {
                ItemStack item = getItems(e)[i];
                if (item == null) continue;
                EquipmentSlot eq = itemTypeFromItemStack(item);
                if (i == 0 && eq.equals(EquipmentSlot.HEAD)) finalBaseValue += PDCToMap(item, "enchants").get(stat);
                if (i == 1 && eq.equals(EquipmentSlot.BODY)) finalBaseValue += PDCToMap(item, "enchants").get(stat);
                if (i == 2 && eq.equals(EquipmentSlot.LEGS)) finalBaseValue += PDCToMap(item, "enchants").get(stat);
                if (i == 3 && eq.equals(EquipmentSlot.FEET)) finalBaseValue += PDCToMap(item, "enchants").get(stat);
                if (i == 4 && eq.equals(EquipmentSlot.HAND)) finalBaseValue += PDCToMap(item, "enchants").get(stat);
            }

            finalBaseValue += accValue;
            finalAddValue += accAddValue;
            finalMulValue += accMulValue;

            for (EquipmentSlot slot : slots) {
                finalBaseValue += itemMap.get(slot).get(stat);
                finalAddValue += addMap.get(slot).get(stat);
                finalMulValue += (mulMap.get(slot).get(stat) - 1);
            }

            total = ((base + finalBaseValue) * (finalMulValue)) + finalAddValue;
            if (e instanceof Player p) {
                if (skillMap == null) {
                    StatManager.getSkillBonusMap().put(p.getUniqueId(), new HashMap<>());
                    for (Stats stat2 : Stats.values())
                        StatManager.getSkillBonusMap().get(p.getUniqueId()).put(stat2, 0d);
                } else total += skillMap.get(stat);
            }

            finalStats.put(stat, total);
        }
        finalStats.put(Stats.FEROCITY, Math.min(finalStats.get(Stats.FEROCITY), FEROCITY_CAP));
        finalStats.put(Stats.ATTACKSPEED, Math.min(finalStats.get(Stats.ATTACKSPEED), ATTACK_SPEED_CAP));
        finalStats.put(Stats.SPEED, Math.min(finalStats.get(Stats.SPEED), SPEED_CAP));
        finalStats.put(Stats.AGILITY, Math.min(finalStats.get(Stats.AGILITY), AGILITY_CAP));
        finalStats.put(Stats.SWING_RANGE, Math.min(finalStats.get(Stats.SWING_RANGE), SWING_RANGE_CAP));

        finalStatMap.put(entityId, finalStats);
    }

    private ItemStack[] addItemStacks(ItemStack[] array1, ItemStack[] array2) {
        // Create a new array with size equal to the sum of both arrays' sizes
        ItemStack[] combined = new ItemStack[array1.length + array2.length];

        // Copy the elements of array1 and array2 into the combined array
        System.arraycopy(array1, 0, combined, 0, array1.length);
        System.arraycopy(array2, 0, combined, array1.length, array2.length);

        return combined;
    }

    private boolean isSlotMatch(int i, ItemType type) {
        if (i == 0 && type == ItemType.HELMET) return true;
        if (i == 1 && type == ItemType.CHESTPLATE) return true;
        if (i == 2 && type == ItemType.LEGGINGS) return true;
        if (i == 3 && type == ItemType.BOOTS) return true;
        if (i == 4 && !(type == ItemType.HELMET || type == ItemType.CHESTPLATE || type == ItemType.LEGGINGS || type == ItemType.BOOTS) && type != ItemType.ACCESSORY)
            return true;
        return i >= 5 && type == ItemType.ACCESSORY;
    }

    public static void remove(UUID uuid) {
        baseStatMap.remove(uuid);
        itemStatMap.remove(uuid);
        itemAdditiveStatMap.remove(uuid);
        itemMultiplicativeStatMap.remove(uuid);
        finalStatMap.remove(uuid);
    }

    private void initMap(LivingEntity e) {
        UUID entityId = e.getUniqueId();

        finalStatMap.put(entityId, new EnumMap<>(Stats.class));

        // Initialize empty mutable maps for the entity
        Map<EquipmentSlot, Map<Stats, Double>> itemStat = new HashMap<>();
        Map<EquipmentSlot, Map<Stats, Double>> additiveStat = new HashMap<>();
        Map<EquipmentSlot, Map<Stats, Double>> multiplicativeStat = new HashMap<>();

        // Loop through all slots and stats
        for (EquipmentSlot slot : slots) {
            Map<Stats, Double> statMap = new HashMap<>(); // EnumMap for performance
            Map<Stats, Double> additiveStatMap = new EnumMap<>(Stats.class);
            Map<Stats, Double> multiplicativeStatMap = new EnumMap<>(Stats.class);

            for (Stats stat : Stats.values()) {
                statMap.put(stat, 0d);
                additiveStatMap.put(stat, 0d);               // Initialize base stats
                multiplicativeStatMap.put(stat, 1d); // Initialize multiplicative stats
            }

            // Add the stat maps for the current slot
            itemStat.put(slot, statMap);
            additiveStat.put(slot, statMap);
            multiplicativeStat.put(slot, multiplicativeStatMap);
        }

        // Finally, put the fully built maps in the main maps
        itemStatMap.put(entityId, itemStat);
        itemAdditiveStatMap.put(entityId, additiveStat);
        itemMultiplicativeStatMap.put(entityId, multiplicativeStat);

        accessoryStatMap.put(e.getUniqueId(), new HashMap<>());
        accessoryAdditiveStatMap.put(e.getUniqueId(), new HashMap<>());
        accessoryMultiplicativeStatMap.put(e.getUniqueId(), new HashMap<>());

        Map<Stats, Double> statMap = new EnumMap<>(Stats.class); // EnumMap for performance
        Map<Stats, Double> addStatMap = new EnumMap<>(Stats.class);
        Map<Stats, Double> mulStatMap = new EnumMap<>(Stats.class);

        for (int i = 0; i < slots.length; i++) {
            for (Stats stat : Stats.values()) {
                statMap.put(stat, 0d);
                addStatMap.put(stat, 0d);
                mulStatMap.put(stat, 1d);
            }
            accessoryStatMap.get(e.getUniqueId()).put(i, statMap);
            accessoryAdditiveStatMap.get(e.getUniqueId()).put(i, addStatMap);
            accessoryMultiplicativeStatMap.get(e.getUniqueId()).put(i, mulStatMap);
        }


        reforgeStatMap.put(e.getUniqueId(), new HashMap<>());

        Map<Stats, Double> reforgeStatMapTemp = new EnumMap<>(Stats.class);

        for (int i = 0; i < slots.length; i++) {
            for (Stats stat : Stats.values()) {
                reforgeStatMapTemp.put(stat, 0d);
            }
            reforgeStatMap.get(e.getUniqueId()).put(i, reforgeStatMapTemp);
        }
    }

    public static int rarityToIndex(ItemRarity rarity) {
        return switch (rarity) {
            case COMMON -> 0;
            case UNCOMMON -> 1;
            case RARE -> 2;
            case EPIC -> 3;
            case LEGENDARY -> 4;
            case MYTHIC -> 5;
            case DIVINE -> 6;
            case SPECIAL -> 7;
            case VERY_SPECIAL -> 8;
            case VERY_VERY_SPECIAL, ADMIN -> 9;
        };
    }
}