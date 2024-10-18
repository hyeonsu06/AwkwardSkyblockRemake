package io.hyonsu06.core.interfaces;

import io.hyonsu06.core.enums.Stats;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public interface ReforgeMethods {
    default Map<Stats, Double[]> baseValue() {
        Map<Stats, Double[]> map = new HashMap<>();
        // Each has for rarity from order:
        // COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, DIVINE, SPECIAL, VERY SPECIAL, VERY VERY SPECIAL
        map.put(Stats.DAMAGE, new Double[]{1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d, 10d});
        return map;
    }

    default double onHit(Player player, Entity target, double damage) {
        return -1;
    }

    default void passive(Player player) {

    }
}
