package io.hyonsu06.item.reforges;

import io.hyonsu06.core.annotations.items.reforge.ReforgeMetadata;
import io.hyonsu06.core.annotations.tags.ReforgeTagged;
import io.hyonsu06.core.enums.Stats;
import io.hyonsu06.core.interfaces.ReforgeMethods;

import java.util.HashMap;
import java.util.Map;

@ReforgeTagged
@ReforgeMetadata(
        ID = "martial"
)
public class Martial implements ReforgeMethods {
    @Override
    public Map<Stats, Double[]> baseValue() {
        Map<Stats, Double[]> map = new HashMap<>();
        map.put(Stats.STRENGTH, new Double[]{3d, 7d, 13d, 20d, 45d, 70d, 100d, 150d, 250d, 400d});
        map.put(Stats.CRITCHANCE, new Double[]{1d, 2d, 3d, 4d, 5d, 6d, 10d, 12d, 15d, 20d});
        map.put(Stats.CRITDAMAGE, new Double[]{5d, 9d, 17d, 25d, 40d, 50d, 75d, 125d, 175d, 225d});
        return map;
    }
}
