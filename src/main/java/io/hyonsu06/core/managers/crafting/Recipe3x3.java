package io.hyonsu06.core.managers.crafting;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Recipe3x3 {
    public abstract List<Pair<String, Integer>> recipe();
    public abstract ItemStack result();
}
