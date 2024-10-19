package io.hyonsu06;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Comparator;

import static io.hyonsu06.Main.plugin;

public class DragonHoming implements Listener {
    @EventHandler
    public void onArrowShoot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow arrow) {
            // Create new runnable to update the arrow's trajectory
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (arrow.getTicksLived() > 20) {
                        if (!arrow.isValid()) {
                            cancel(); // Stop if the arrow is dead
                            return;
                        }
                        EnderDragon nearestDragon = findNearestEnderDragon(arrow.getLocation());
                        if (nearestDragon != null) {
                            Vector direction = nearestDragon.getLocation().subtract(arrow.getLocation()).toVector().normalize();
                            arrow.setVelocity(direction.multiply(1.2)); // Adjust the multiplier for speed
                        }
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L); // Update every tick
        }
    }

    private EnderDragon findNearestEnderDragon(Location location) {
        return (EnderDragon) location.getWorld().getEntities().stream()
                .filter(entity -> entity.getType() == EntityType.ENDER_DRAGON)
                .min(Comparator.comparingDouble(dragon -> dragon.getLocation().distance(location)))
                .orElse(null);
    }
}
