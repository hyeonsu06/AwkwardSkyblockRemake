package io.hyonsu06.item.enchantments;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class Enchantment {
    String name;
    int level;
    String description;

    /**
        @param name Name of enchantment.
        @param description Description of enchantment.
        @param level Max level of enchantment from enchanting table.
     */
    public Enchantment(String name, String description, int level) {
        this.name = name;
        this.level = level;
        this.description = description;
    }

    public void applyEffect(Player player, ItemStack item, int level) {}

    public void deApplyEffect(Player player, ItemStack item, int level) {}
}
