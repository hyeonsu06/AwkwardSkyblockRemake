package io.hyonsu06;

import io.hyonsu06.command.accessories.AccessoriesListener;
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
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.Comparator;

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
/*
    public void saveData() {
        saveMap(StatManager.getBaseStatMap(), "baseStatMap.json");
        saveMap(EntityManager.getPlayerIntelligence(), "playerIntelligence.json");
    }

    public void loadData() {
        StatManager.setBaseStatMap(loadMap("baseStatMap.json"));
        EntityManager.setPlayerIntelligence(loadMap("playerIntelligence.json"));
    }
    private <K, V> void saveMap(Map<K, V> map, String fileName) {
        // Create a set to track unique values
        Set<V> uniqueValues = new HashSet<>();
        Map<K, V> uniqueMap = new HashMap<>();

        // Iterate over the original map to filter out duplicates
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (uniqueValues.add(entry.getValue())) { // Returns true if the value was not already present
                uniqueMap.put(entry.getKey(), entry.getValue());
            } else {
                // Log a message about the duplicate value
                getLogger().warning("[" + this.getName() + "] Duplicate value found: " + entry.getValue());
            }
        }

        // Save the unique map to the file
        File file = new File(getDataFolder(), fileName);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(uniqueMap, writer);
            getLogger().info("[" + this.getName() + "] Saved " + fileName + " with unique values");
        } catch (IOException e) {
            getLogger().severe("[" + this.getName() + "] Error saving " + fileName + ": " + e.getMessage());
        }
    }

    private <K, V> Map<K, V> loadMap(String fileName) {
        File file = new File(getDataFolder(), fileName);
        if (!file.exists()) {
            return new HashMap<>(); // Return an empty map if the file doesn't exist
        }

        Type mapType = new TypeToken<Map<K, V>>() {}.getType();
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, mapType);
        } catch (IOException e) {
            getLogger().severe("[" + this.getName() + "] Error loading " + fileName + ": " + e.getMessage());
            return new HashMap<>(); // Return an empty map on error
        }
    }

 */
}
