package io.hyonsu06.core;

import io.hyonsu06.core.enums.Stats;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataMapManager {
    private final File dataFile; // File to store the YAML data
    private final FileConfiguration config;

    public DataMapManager(File dataFile) {
        this.dataFile = dataFile;
        this.config = YamlConfiguration.loadConfiguration(dataFile);
    }

    // Saving Map<UUID, ItemStack[]> to YAML
    public void saveItemStackMap(Map<UUID, ItemStack[]> map) {
        for (Map.Entry<UUID, ItemStack[]> entry : map.entrySet()) {
            String uuidString = entry.getKey().toString();
            ItemStack[] items = entry.getValue();
            config.set("players." + uuidString + ".items", serializeItemStackArray(items)); // Use a proper path
        }
        saveConfig();
    }

    // Loading Map<UUID, ItemStack[]> from YAML
    public Map<UUID, ItemStack[]> loadItemStackMap() {
        Map<UUID, ItemStack[]> map = new HashMap<>();
        if (config.contains("players")) {
            for (String key : config.getConfigurationSection("players").getKeys(false)) { // Access the players section
                UUID uuid = UUID.fromString(key);
                ItemStack[] items = deserializeItemStackArray((List<Object>) config.getList("players." + key + ".items")); // Access the items section
                map.put(uuid, items);
            }
        }
        return map;
    }

    // Saving Map<UUID, Map<Stats, Double>> to YAML
    public void saveStatsMap(Map<UUID, Map<Stats, Double>> map) {
        for (Map.Entry<UUID, Map<Stats, Double>> entry : map.entrySet()) {
            String uuidString = entry.getKey().toString();
            Map<Stats, Double> stats = entry.getValue();
            for (Map.Entry<Stats, Double> statEntry : stats.entrySet()) {
                config.set("players." + uuidString + ".stats." + statEntry.getKey().name().toLowerCase(), statEntry.getValue());
            }
        }
        saveConfig();
    }

    // Loading Map<UUID, Map<Stats, Double>> from YAML
    public Map<UUID, Map<Stats, Double>> loadStatsMap() {
        Map<UUID, Map<Stats, Double>> map = new HashMap<>();
        if (config.contains("players")) {
            for (String key : config.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                Map<Stats, Double> statsMap = new HashMap<>();
                if (config.contains("players." + key + ".stats")) {
                    for (String statKey : config.getConfigurationSection("players." + key + ".stats").getKeys(false)) {
                        Stats stat = Stats.valueOf(statKey.toUpperCase()); // Assuming Stats enum names match the keys
                        double value = config.getDouble("players." + key + ".stats." + statKey);
                        statsMap.put(stat, value);
                    }
                }
                map.put(uuid, statsMap);
            }
        }
        return map;
    }

    // Helper method to serialize ItemStack[] to List<Object>
    private List<Object> serializeItemStackArray(ItemStack[] items) {
        List<Object> serializedList = new ArrayList<>();
        for (ItemStack item : items) {
            serializedList.add(item != null ? item.serialize() : null); // Serialize each ItemStack
        }
        return serializedList;
    }

    // Helper method to deserialize List<Object> to ItemStack[]
    private ItemStack[] deserializeItemStackArray(List<Object> list) {
        ItemStack[] items = new ItemStack[list.size()];
        for (int i = 0; i < list.size(); i++) {
            items[i] = list.get(i) != null ? ItemStack.deserialize((Map<String, Object>) list.get(i)) : null;
        }
        return items;
    }

    // Helper method to save the configuration file
    private void saveConfig() {
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
