package io.hyonsu06.item.items.recipes;

import io.hyonsu06.command.items.LoadItems;
import io.hyonsu06.core.annotations.tags.RecipeTagged;
import io.hyonsu06.core.managers.crafting.Recipe3x3;
import io.hyonsu06.item.items.materials.PureGem;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RecipeTagged
public class PureGemRecipe extends Recipe3x3 {
    @Override
    public List<Pair<String, Integer>> recipe() {
        List<Pair<String, Integer>> materials = new ArrayList<>();

        Pair<String, Integer> emerald = Pair.of("emerald", 64);
        Pair<String, Integer> block = Pair.of("emerald_block", 64);

        materials.add(null); materials.add(emerald); materials.add(null);
        materials.add(emerald); materials.add(block); materials.add(emerald);
        materials.add(null); materials.add(emerald); materials.add(null);

        return materials;
    }

    @Override
    public ItemStack result() {
        return LoadItems.getInstance().get(PureGem.class);
    }
}
