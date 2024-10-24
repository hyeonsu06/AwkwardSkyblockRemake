package io.hyonsu06.item.items.recipes;

import io.hyonsu06.command.items.LoadItems;
import io.hyonsu06.core.annotations.tags.RecipeTagged;
import io.hyonsu06.core.managers.crafting.Recipe3x3;
import io.hyonsu06.item.items.weapons.ranged.Terminator;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RecipeTagged(
        requireTag = true
)
public class TerminatorRecipe extends Recipe3x3 {
    @Override
    public List<Pair<String, Integer>> recipe() {
        List<Pair<String, Integer>> materials = new ArrayList<>();

        Pair<String, Integer> pearl = Pair.of("ender_pearl", 16);
        Pair<String, Integer> blade = Pair.of("judgement_blade", 1);
        Pair<String, Integer> core = Pair.of("judgement_core", 1);
        Pair<String, Integer> string = Pair.of("string", 64);

        materials.add(pearl); materials.add(blade); materials.add(string);
        materials.add(blade); materials.add(core); materials.add(string);
        materials.add(pearl); materials.add(blade); materials.add(string);

        return materials;
    }

    @Override
    public ItemStack result() {
        return LoadItems.getInstance().get(Terminator.class);
    }
}
