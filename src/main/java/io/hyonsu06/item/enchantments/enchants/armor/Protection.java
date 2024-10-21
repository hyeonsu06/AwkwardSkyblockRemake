package io.hyonsu06.item.enchantments.enchants.armor;

import io.hyonsu06.core.enums.Stats;
import io.hyonsu06.item.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static io.hyonsu06.core.functions.MapPDCConverter.PDCToMap;
import static io.hyonsu06.core.functions.MapPDCConverter.mapToPDC;
import static io.hyonsu06.core.functions.getStatText.DEFENSE;

public class Protection extends Enchantment {
    public Protection() {
        super("Protection", "Grants " + DEFENSE, 5);
    }

    final static int MODIFIER = 4;
    final static Stats STAT = Stats.DEFENSE;

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
