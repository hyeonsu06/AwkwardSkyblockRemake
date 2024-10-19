package io.hyonsu06.core;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

import static io.hyonsu06.core.functions.ArmorTweaks.isArmor;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getItemID;

public class PreventUnintendedAction implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (Objects.nonNull(getItemID(event.getItem())) && !isArmor(event.getItem())) {
            if (event.getAction().isLeftClick()) {
                event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity e) {
            if (e.getEquipment().getItemInMainHand().getType().equals(Material.BOW) || e.getEquipment().getItemInMainHand().getType().equals(Material.CROSSBOW)) {
                event.setCancelled(true);
            }
        }
    }
}
