package io.hyonsu06;

import io.hyonsu06.command.accessories.AccessoriesListener;
import io.hyonsu06.command.accessories.AccessoriesUtils;
import io.hyonsu06.command.accessories.ShowAccessoriesCommand;
import io.hyonsu06.command.autobuild.AutoBuild;
import io.hyonsu06.command.enchant.AddEnchantmentCommand;
import io.hyonsu06.command.enchant.AddEnchantmentTabCompleter;
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
import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.HashMap;

import static io.hyonsu06.core.managers.EntityManager.loadData;
import static io.hyonsu06.core.functions.getClasses.*;
import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.Bukkit.getScheduler;

public final class Main extends JavaPlugin {
    public static Main plugin;
    public static boolean isReloading;
    public static DataMapManager dataMapManager1;
    public static DataMapManager dataMapManager2;

    File myPluginFolder = new File(getDataFolder().getAbsolutePath());

    @Override
    public void onEnable() {
        if (!isReloading) {
            plugin = this;

            File dataFile1 = new File(getDataFolder(), "baseMap.yml");
            dataMapManager1 = new DataMapManager(dataFile1);

            File dataFile2 = new File(getDataFolder(), "accessories.yml");
            dataMapManager2 = new DataMapManager(dataFile2);

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

            getSkillClasses();
            getItemClasses();
            getReforgeClasses();

        }

        plugin.getCommand("items").setExecutor(new ShowAllItemsCommand());
        plugin.getCommand("stat").setExecutor(new setStatCommand());
        plugin.getCommand("stat").setTabCompleter(new setStatTabCompleter());
        plugin.getCommand("reforge").setExecutor(new ReforgeCommand());
        plugin.getCommand("reforge").setTabCompleter(new ReforgeTabCompleter());
        plugin.getCommand("accessories").setExecutor(new ShowAccessoriesCommand());
        plugin.getCommand("recombobulate").setExecutor(new RecombobulatorCommand());
        plugin.getCommand("potato").setExecutor(new PotatoBookCommand());
        plugin.getCommand("addenchant").setExecutor(new AddEnchantmentCommand());
        plugin.getCommand("addenchant").setTabCompleter(new AddEnchantmentTabCompleter());
        plugin.getCommand("autobuild").setExecutor(new AutoBuild());

        StatManager statManager = StatManager.instance();
        NoParticle noParticle =  NoParticle.instance();
        Refresher refresher = Refresher.instance();

        getPluginManager().registerEvents(new ModifySomeFeatures(), plugin);
        getPluginManager().registerEvents(new VanillaEntityManager(), plugin);
        getPluginManager().registerEvents(new AllItemsListener(), plugin);
        getPluginManager().registerEvents(new AccessoriesListener(), plugin);

        getPluginManager().registerEvents(new EnchantManager(), plugin);
        getPluginManager().registerEvents(new EntityManager(), plugin);
        getPluginManager().registerEvents(new SkillManager(), plugin);

        getPluginManager().registerEvents(new DragonHoming(), plugin);

        if (isReloading) {
            getLogger().info("Seems plugin is on reload, remapping stat map...");
            loadData();

            for (Player p : Bukkit.getOnlinePlayers()) {
                EntityManager.getMeleeHits().put(p.getUniqueId(), 0);
                EntityManager.getRangedHits().put(p.getUniqueId(), 0);

                SkillManager.getCooldownMap().put(p.getUniqueId(), new HashMap<>());
                for (Class<?> clazz : getSkillClasses()) {
                    Skill skill = clazz.getAnnotation(Skill.class);
                    SkillManager.getCooldownMap().get(p.getUniqueId()).put(skill.ID(), 0);
                }
            }
        }

        new LoadItems().registerAllItems();

        for (World w : Bukkit.getWorlds())
            for (Entity e : w.getEntities())
                if (e instanceof TextDisplay display) if (display.getVehicle() == null) display.remove();
    }

    @Override
    public void onDisable() {
        saveData();
        for (BukkitTask task : getScheduler().getPendingTasks()) if (task.getOwner().equals(plugin)) if (!EntityManager.getPlayerTaskMap().containsValue(task.getTaskId())) task.cancel();

        plugin.getCommand("items").setExecutor(null);
        plugin.getCommand("stat").setExecutor(null);
        plugin.getCommand("stat").setTabCompleter(null);
        plugin.getCommand("reforge").setExecutor(null);
        plugin.getCommand("reforge").setTabCompleter(null);
        plugin.getCommand("accessories").setExecutor(null);
        plugin.getCommand("recombobulate").setExecutor(null);
        plugin.getCommand("potato").setExecutor(null);
        plugin.getCommand("addenchant").setExecutor(null);
        plugin.getCommand("addenchant").setTabCompleter(null);
        plugin.getCommand("autobuild").setExecutor(null);

        StatManager.clear();
        NoParticle.clear();
        Refresher.clear();

        EnchantManager.clear();
        EntityManager.clear();
        SkillManager.clear();
    }

    // Save all data
    private void saveData() {
        dataMapManager1.saveStatsMap(StatManager.getBaseStatMap());
        dataMapManager2.saveItemStackMap(AccessoriesUtils.getAccessories());
        getLogger().info("Data saved successfully.");
    }
}
