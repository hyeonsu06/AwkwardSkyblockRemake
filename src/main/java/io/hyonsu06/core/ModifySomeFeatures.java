package io.hyonsu06.core;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

import static io.hyonsu06.core.functions.ArmorTweaks.isArmor;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getItemID;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;

public class ModifySomeFeatures implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (Objects.nonNull(getItemID(event.getItem())) && !isArmor(event.getItem())) {
            if (event.getAction().isLeftClick()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity e) {
            if (e.getEquipment().getItemInMainHand().getType().equals(Material.BOW) || e.getEquipment().getItemInMainHand().getType().equals(Material.CROSSBOW)) {
                event.setCancelled(true);
            }
        }
        if (event.getEntity() instanceof LivingEntity e) {
            if (e.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) e.getVelocity().multiply(0.7);
            if (e.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER)) e.getVelocity().multiply(0.1);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            event.setDroppedExp(event.getDroppedExp() * 3);
        }
        if (event.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER)) {
            event.setDroppedExp(event.getDroppedExp() * 10);
            if (event.getEntity().getPersistentDataContainer().get(getPDC("ominous"), PersistentDataType.BOOLEAN) != null)
                event.setDroppedExp(event.getDroppedExp() * 50);
        }
    }
}
