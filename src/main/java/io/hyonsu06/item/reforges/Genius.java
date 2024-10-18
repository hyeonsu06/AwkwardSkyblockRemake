package io.hyonsu06.item.reforges;

import io.hyonsu06.core.EntityManager;
import io.hyonsu06.core.annotations.items.reforge.ReforgeMetadata;
import io.hyonsu06.core.annotations.tags.ReforgeTagged;
import io.hyonsu06.core.enums.Stats;
import io.hyonsu06.core.interfaces.ReforgeMethods;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static io.hyonsu06.core.functions.getAnnotationArguments.getReforgeAnnotation;

@ReforgeTagged
@ReforgeMetadata(
        ID = "genius",
        lore = "Gain {0}% of max mana every hit.",
        args = {
                1
        }
)
public class Genius implements ReforgeMethods {
    @Override
    public Map<Stats, Double[]> baseValue() {
        Map<Stats, Double[]> map = new HashMap<>();
        map.put(Stats.INTELLIGENCE, new Double[]{25d, 35d, 50d, 75d, 100d, 125d, 175d, 250d, 400d, 500d});
        return map;
    }

    @Override
    public double onHit(Player player, Entity target, double damage) {
        EntityManager.getPlayerIntelligence().compute(player.getUniqueId(), (k, intelligence) -> intelligence + getReforgeAnnotation(getClass()).args()[0]);
        return -1;
    }
}
