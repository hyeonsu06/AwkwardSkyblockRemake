package io.hyonsu06.core;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;


import static io.hyonsu06.Main.plugin;
import static org.bukkit.Bukkit.getWorlds;

public class EntityLimiter implements Listener {
    private int count;


    public EntityLimiter() {
        new BukkitRunnable() {
            @Override
            public void run() {
                count = 0;
                for (World w : getWorlds()) for (Entity e : w.getEntities()) if (e.isValid()) count++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void limit(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            int limit = 300;
            if (count + 1 > limit) event.setCancelled(true);
        }
    }
}
