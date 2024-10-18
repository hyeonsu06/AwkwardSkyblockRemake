package io.hyonsu06.core.functions;

import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import static io.hyonsu06.Main.plugin;

public class setImmuneTime {
    public static void setNoDamageTicks(LivingEntity e, int ticks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                e.setNoDamageTicks(ticks);
            }
        }.runTaskLater(plugin, 1);
    }
}
