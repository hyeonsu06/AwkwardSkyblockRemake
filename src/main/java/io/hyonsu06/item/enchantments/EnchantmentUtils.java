package io.hyonsu06.item.enchantments;

import io.hyonsu06.item.enchantments.enchants.armor.Growth;
import io.hyonsu06.item.enchantments.enchants.armor.ManaRegen;
import io.hyonsu06.item.enchantments.enchants.armor.Protection;
import io.hyonsu06.item.enchantments.enchants.armor.Rejuvenate;
import io.hyonsu06.item.enchantments.enchants.melee.FireAspect;
import io.hyonsu06.item.enchantments.enchants.melee.Sharpness;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;
import static org.bukkit.persistence.PersistentDataType.INTEGER;

public class EnchantmentUtils {
    public static List<Enchantment> getAllEnchantments() {
        return Arrays.asList(
                new Sharpness(), new FireAspect(),
                new Growth(), new Protection(), new Rejuvenate(), new ManaRegen()
        );
    }

    public static ItemStack addEnchantment(ItemStack item, Enchantment enchantment, int level) {
        if (item == null || level <= 0) return item;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey enchantmentKey = getPDC(enchantment.getName().toLowerCase().replace(" ", "_"));
        data.set(enchantmentKey, PersistentDataType.INTEGER, level);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack removeEnchantment(ItemStack item, Enchantment enchantment) {
        if (item == null) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // Get the PersistentDataContainer for this item
        PersistentDataContainer data = meta.getPersistentDataContainer();

        // Store the enchantment level using its name as the key
        NamespacedKey enchantmentKey = getPDC(enchantment.getName().toLowerCase().replace(" ", "_"));
        data.remove(enchantmentKey);

        item.setItemMeta(meta);
        return item;
    }

    public static Map<Enchantment, Integer> getEnchantments(ItemStack item) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        if (item == null) return enchantments;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return enchantments;

        PersistentDataContainer data = meta.getPersistentDataContainer();

        // Example: Check for specific custom enchantments
        for (Enchantment enchantment : getAllEnchantments()) {
            NamespacedKey key = getPDC(enchantment.getName().toLowerCase().replace(" ", "_"));
            if (data.has(key, INTEGER)) {
                int level = data.get(key, PersistentDataType.INTEGER);
                enchantments.put(enchantment, level);
            }
        }

        return enchantments;
    }

    public static Enchantment getEnchantmentByName(String name) {
        return switch (name) {
            case "sharpness" -> new Sharpness();
            case "fire_aspect" -> new FireAspect();
            case "growth" -> new Growth();
            case "protection" -> new Protection();
            case "rejuvenate" -> new Rejuvenate();
            case "manaregen" -> new ManaRegen();
            default -> null;
        };
    }
}
