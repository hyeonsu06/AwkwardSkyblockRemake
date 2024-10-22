package io.hyonsu06.core.managers;

import io.hyonsu06.core.enums.Stats;
import io.hyonsu06.item.enchantments.Enchantment;
import io.hyonsu06.item.enchantments.EnchantmentUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static io.hyonsu06.Main.plugin;
import static io.hyonsu06.core.functions.ArmorTweaks.getItems;
import static io.hyonsu06.core.managers.StatManager.slots;

public class EnchantManager implements Listener {
    public static EnchantManager instance;

    public static EnchantManager instance() {
        if (instance == null) {
            instance = new EnchantManager();
        }
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    public EnchantManager() {
        new BukkitRunnable() {
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    StatManager.getEnchantStatMap().put(p.getUniqueId(), new HashMap<>());
                    Map<EquipmentSlot, Map<Stats, Double>> map = StatManager.getEnchantStatMap().get(p.getUniqueId());
                    for (EquipmentSlot slot : slots) {
                        map.put(slot, new EnumMap<>(Stats.class));
                        for (Stats stat : Stats.values()) map.get(slot).put(stat, 0d);
                    }
                    StatManager.getEnchantStatMap().put(p.getUniqueId(), map);
                    for (ItemStack weapon : getItems(p)){
                        if (weapon != null) {
                            Map<Enchantment, Integer> enchantments = EnchantmentUtils.getEnchantments(weapon);

                            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                                Enchantment enchantment = entry.getKey();
                                int level = entry.getValue();

                                enchantment.applyEffect(p, weapon, level);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }
}
