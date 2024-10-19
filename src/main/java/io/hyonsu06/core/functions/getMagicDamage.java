package io.hyonsu06.core.functions;

import io.hyonsu06.core.managers.StatManager;
import io.hyonsu06.core.enums.Stats;
import org.bukkit.entity.Player;

public class getMagicDamage {
    public static long magicDamage(Player p, long damage) {
        double multiplier;
        try {
            multiplier = StatManager.getFinalStatMap().get(p.getUniqueId()).get(Stats.INTELLIGENCE);
        } catch (NullPointerException ignored) {
            multiplier = 0;
        }
        return (long) (damage * (1 + (multiplier / 69)));
    }
}
