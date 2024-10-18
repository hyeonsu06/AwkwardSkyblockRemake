package io.hyonsu06.core.functions;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import static io.hyonsu06.Main.plugin;

public class getPluginNameSpacedKey {
    public static NamespacedKey getPDC(String key) {
        return new NamespacedKey(plugin, key);
    }

    public static String getItemID(ItemStack i) {
        if (i != null && !i.getType().isAir()) {
            return i.getItemMeta().getPersistentDataContainer().get(getPDC("id"), PersistentDataType.STRING);
        }
        return null;
    }
}
