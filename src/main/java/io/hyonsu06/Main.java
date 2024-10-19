package io.hyonsu06;

import io.hyonsu06.command.accessories.AccessoriesListener;
import io.hyonsu06.command.accessories.AccessoriesUtils;
import io.hyonsu06.command.accessories.ShowAccessoriesCommand;
import io.hyonsu06.command.autobuild.AutoBuild;
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
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.Comparator;

import static io.hyonsu06.core.EntityManager.loadData;
import static io.hyonsu06.core.functions.getClasses.*;
import static org.bukkit.Bukkit.getPluginManager;

public final class Main extends JavaPlugin implements Listener {
    public static Main plugin;
    public static boolean isReloading;
    public static DataMapManager dataMapManager1;
    public static DataMapManager dataMapManager2;

    File myPluginFolder = new File(getDataFolder().getAbsolutePath());
    @Override
    public void onEnable() {
        plugin = this;

        File dataFile1 = new File(getDataFolder(), "baseMap.yml");
        dataMapManager1 = new DataMapManager(dataFile1);

        File dataFile2 = new File(getDataFolder(), "accessories.yml");
        dataMapManager2 = new DataMapManager(dataFile2);

        getPluginManager().registerEvents(new PreventUnintendedAction(), plugin);

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
        getPluginManager().registerEvents(new AllItemsListener(), plugin);
        getPluginManager().registerEvents(new AccessoriesListener(), plugin);
        getPluginManager().registerEvents(new EntityManager(), plugin);
        getPluginManager().registerEvents(this, this);

        new NoParticle();
        new StatManager();
        new Refresher();
        new LoadItems().registerAllItems();

        if (isReloading) {
            getLogger().info("Seems plugin is on reload, remapping stat map...");
            loadData();

            itemReflections = null;
            skillReflections = null;
            reforgeReflections = null;
        }

        //TODO: enchants
        //TODO: save/load data
    }

    @Override
    public void onDisable() {
        saveData();
    }

    // Save all data
    private void saveData() {
        dataMapManager1.saveStatsMap(StatManager.getBaseStatMap());
        dataMapManager2.saveItemStackMap(AccessoriesUtils.getAccessories());
        getLogger().info("Data saved successfully.");
    }

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
