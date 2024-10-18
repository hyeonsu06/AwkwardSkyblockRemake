package io.hyonsu06;

import io.hyonsu06.command.accessories.AccessoriesListener;
import io.hyonsu06.command.accessories.AccessoriesUtils;
import io.hyonsu06.command.accessories.ShowAccessoriesCommand;
import io.hyonsu06.command.items.LoadItems;
import io.hyonsu06.command.items.ShowAllItemsCommand;
import io.hyonsu06.command.items.AllItemsListener;
import io.hyonsu06.command.potatobook.PotatoBookCommand;
import io.hyonsu06.command.recombobulator.RecombobulatorCommand;
import io.hyonsu06.command.reforge.ReforgeCommand;
import io.hyonsu06.command.reforge.ReforgeTabCompleter;
import io.hyonsu06.command.stat.setStatTabCompleter;
import io.hyonsu06.command.stat.setStatCommand;
import io.hyonsu06.core.*;
import io.hyonsu06.core.Refresher;
import io.hyonsu06.core.enums.Stats;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

import static io.hyonsu06.core.DataMapManager.loadObjectFromJson;
import static io.hyonsu06.core.DataMapManager.saveObjectToJson;
import static io.hyonsu06.core.functions.getClasses.*;
import static org.bukkit.Bukkit.getPluginManager;

public final class Main extends JavaPlugin implements Listener {
    public static Main plugin;
    // private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void onEnable() {
        plugin = this;
        getPluginManager().registerEvents(new PreventUnintendedAction(), plugin);

        File myPluginFolder = new File(getDataFolder().getAbsolutePath());
        if (!myPluginFolder.exists()) {
            boolean created = myPluginFolder.mkdirs();
            if (created) {
                getLogger().info("[" + this.getName() + "] Successfully created plugin directory to: " + myPluginFolder.getAbsolutePath());
            } else {
                getLogger().warning("[" + this.getName() + "] Could not create plugin directory to: " + myPluginFolder.getAbsolutePath());
            }
        } else {
            getLogger().info("[" + this.getName() + "] Plugin directory already exists, skipping creating it");
        }

        StatManager.setBaseStatMap((Map<UUID, Map<Stats, Double>>) loadObjectFromJson(myPluginFolder + "baseMap.json"));
        AccessoriesUtils.setAccessories((Map<UUID, ItemStack[]>) loadObjectFromJson(myPluginFolder + "accessories.json"));

        plugin.getCommand("items").setExecutor(new ShowAllItemsCommand());

        plugin.getCommand("stat").setExecutor(new setStatCommand());
        plugin.getCommand("stat").setTabCompleter(new setStatTabCompleter());

        plugin.getCommand("reforge").setExecutor(new ReforgeCommand());
        plugin.getCommand("reforge").setTabCompleter(new ReforgeTabCompleter());

        plugin.getCommand("accessories").setExecutor(new ShowAccessoriesCommand());

        plugin.getCommand("recombobulate").setExecutor(new RecombobulatorCommand());

        plugin.getCommand("potato").setExecutor(new PotatoBookCommand());

        plugin.getCommand("autobuild").setExecutor(new AutoBuild());

        getSkillClasses();
        getItemClasses();
        getReforgeClasses();

        getPluginManager().registerEvents(new VanillaManager(), plugin);
        getPluginManager().registerEvents(new SkillManager(), plugin);
        getPluginManager().registerEvents(new EntityManager(), plugin);
        getPluginManager().registerEvents(new AllItemsListener(), plugin);
        getPluginManager().registerEvents(new AccessoriesListener(), plugin);
        getPluginManager().registerEvents(this, this);

        new NoParticle();
        new StatManager();
        new Refresher();
        new LoadItems().registerAllItems();

        //TODO: recombobulator 3000, potato books, and enchants
    }

    @Override
    public void onDisable() {
        saveObjectToJson(StatManager.getBaseStatMap(), new File(getDataFolder().getAbsolutePath()) + "baseMap.json");
        saveObjectToJson(AccessoriesUtils.getAccessories(), new File(getDataFolder().getAbsolutePath()) + "accessories.json");
    }

    @EventHandler
    public void onArrowShoot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow arrow) {
            // Create new runnable to update the arrow's trajectory
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (arrow.getTicksLived() > 40) {
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
            }.runTaskTimer(this, 0L, 1L); // Update every tick
        }
    }

    private EnderDragon findNearestEnderDragon(Location location) {
        return (EnderDragon) location.getWorld().getEntities().stream()
                .filter(entity -> entity.getType() == EntityType.ENDER_DRAGON)
                .min(Comparator.comparingDouble(dragon -> dragon.getLocation().distance(location)))
                .orElse(null);
    }
}
