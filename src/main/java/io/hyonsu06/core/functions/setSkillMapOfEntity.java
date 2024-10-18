package io.hyonsu06.core.functions;

import io.hyonsu06.core.StatManager;
import io.hyonsu06.core.enums.Stats;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.hyonsu06.Main.plugin;

public class setSkillMapOfEntity {
    private static Map<UUID, Map<String, Boolean>> skillMap = new HashMap<>();

    public static void setBonus(LivingEntity e, String skillID, Stats stat, double value, int duration) {
        UUID uuid = e.getUniqueId();
        if (skillMap == null) skillMap.put(uuid, new HashMap<>());
        skillMap.computeIfAbsent(uuid, k -> Map.of(skillID, false));
        if (skillMap.get(uuid).get(skillID).equals(false)) {
            Map<UUID, Map<Stats, Double>> map = StatManager.getSkillBonusMap();
            if (map == null) map.put(uuid, new HashMap<>());
            double originalValue;
            try {
                originalValue = map.get(e.getUniqueId()).get(stat);
            } catch (Exception ignored) {
                originalValue = 0;
            }
            StatManager.getSkillBonusMap().get(uuid).put(stat, originalValue + value);
            skillMap.put(uuid, Map.of(skillID, true));
            double finalOriginalValue = originalValue;
            new BukkitRunnable() {
                @Override
                public void run() {
                    StatManager.getSkillBonusMap().get(uuid).put(stat, finalOriginalValue);
                    skillMap.put(uuid, Map.of(skillID, false));
                }
            }.runTaskLater(plugin, duration);
        }
    }
}
