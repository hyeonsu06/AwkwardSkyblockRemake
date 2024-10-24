package io.hyonsu06.core.managers.vanilla;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static io.hyonsu06.Main.plugin;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;

public class VanillaItemManager implements Listener {
    public VanillaItemManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World w : Bukkit.getWorlds()) {
                    for (Entity entity : w.getEntities()) {
                        if (entity instanceof LivingEntity e) {
                            if (e.getEquipment() != null) {
                                List<ItemStack> items = new ArrayList<>(List.of(e.getEquipment().getArmorContents()));
                                if (!e.getEquipment().getItemInMainHand().getType().isAir())
                                    items.add(e.getEquipment().getItemInMainHand());
                                if (!e.getEquipment().getItemInOffHand().getType().isAir())
                                    items.add(e.getEquipment().getItemInOffHand());
                                for (ItemStack i : items) apply(i);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @EventHandler
    public void onLoot(LootGenerateEvent event) {
        for (ItemStack i : event.getLoot()) {
            apply(i);
            if (i.getType().equals(Material.ARROW)) i.setAmount(i.getAmount() * 3);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Item i) {
            apply(i.getItemStack());
        }
    }

    private void apply(ItemStack i) {
        PersistentDataContainer pdc;
        if (i != null || !i.getType().isAir()) {
            if (i.getItemMeta() != null) {
                pdc = i.getItemMeta().getPersistentDataContainer();
            } else {
                return;
            }
        } else {
            return;
        }

        if (!pdc.has(getPDC("id"), PersistentDataType.STRING)) {
            ItemMeta meta = i.getItemMeta();
            meta.getPersistentDataContainer().set(
                    getPDC("id"),
                    PersistentDataType.STRING,
                    i.getType().name().toLowerCase()
            );
            i.setItemMeta(meta);
        }
    }
}
