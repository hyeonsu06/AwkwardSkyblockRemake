package io.hyonsu06.core.functions;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.hyonsu06.core.enums.Stats;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;

import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;

public class MapPDCConverter {

    private static final Gson gson = new Gson();

    public static void mapToPDC(ItemStack item, Map<Stats, Double> map, String key) {
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String json = gson.toJson(map);
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(getPDC(key), PersistentDataType.STRING, json);

        item.setItemMeta(meta);
    }

    public static Map<Stats, Double> PDCToMap(ItemStack item, String key) {
        Map<Stats, Double> map = new EnumMap<>(Stats.class);
        for (Stats stats : Stats.values()) map.put(stats, 0d);

        if (item == null) return map;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return map;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (!data.has(getPDC(key), PersistentDataType.STRING)) return map;
        String json = data.get(getPDC(key), PersistentDataType.STRING);

        Type type = new TypeToken<Map<Stats, Double>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
