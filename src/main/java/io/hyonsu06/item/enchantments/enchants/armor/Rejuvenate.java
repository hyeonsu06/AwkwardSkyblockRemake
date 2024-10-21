package io.hyonsu06.item.enchantments.enchants.armor;

import io.hyonsu06.core.enums.Stats;
import io.hyonsu06.item.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static io.hyonsu06.core.functions.MapPDCConverter.PDCToMap;
import static io.hyonsu06.core.functions.MapPDCConverter.mapToPDC;
import static io.hyonsu06.core.functions.getStatText.HEALTHREGEN;

public class Rejuvenate extends Enchantment {
    public Rejuvenate() {
        super("Rejuvenate", "Grants " + HEALTHREGEN, 5);
    }

    final static int MODIFIER = 3;
    final static Stats STAT = Stats.HEALTHREGEN;

    @Override
    public void applyEffect(Player player, ItemStack item, int level) {
        Map<Stats, Double> map = PDCToMap(item, "enchants");
        map.put(STAT,  (double) (level * MODIFIER));
        mapToPDC(item, map, "enchants");
    }

    @Override
    public void deApplyEffect(Player player, ItemStack item, int level) {
        Map<Stats, Double> map = PDCToMap(item, "enchants");
        map.compute(STAT, (k, original) -> original - (double) (level * MODIFIER));
        mapToPDC(item, map, "enchants");
    }
}
