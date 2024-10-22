package io.hyonsu06.core.managers;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.reforge.ReforgeMetadata;
import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.functions.setSkillMapOfEntity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.hyonsu06.Main.plugin;
import static io.hyonsu06.core.functions.checkAndSpendMana.isQualified;
import static io.hyonsu06.core.functions.checkAndSpendMana.spendMana;
import static io.hyonsu06.core.functions.getAnnotationArguments.getSkillAnnotation;
import static io.hyonsu06.core.functions.ArmorTweaks.getItems;
import static io.hyonsu06.core.functions.getClasses.*;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getItemID;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;

public class SkillManager implements Listener {
    @Getter
    @Setter
    public static Map<UUID, Map<String, Integer>> cooldownMap = new HashMap<>();

    public static SkillManager instance;

    public static SkillManager instance() {
        if (instance == null) {
            instance = new SkillManager();
        }
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    public SkillManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Skill skill;
                    for (ItemStack item : getItems(p)) {
                        String id = getItemID(item);
                        if (id == null) break;
                        try {
                            // Invoke passive skills
                            for (Class<?> skillClass : getMatchedSkill(id)) {
                                for (Method m : skillClass.getDeclaredMethods()) {
                                    if (m.getName().equals("passive")) {
                                        Object skillInstance = skillClass.getDeclaredConstructor().newInstance();
                                        skill = skillClass.getAnnotation(Skill.class);

                                        Map<String, Integer> playerCooldowns = cooldownMap.get(p.getUniqueId());

                                        // Initialize cooldown if not present
                                        playerCooldowns.putIfAbsent(skill.ID(), 0);

                                        // Check if cooldown is zero
                                        if (isQualified(p, skill.cost())) {
                                            if (playerCooldowns.get(skill.ID()) == 0) {
                                                m.invoke(skillInstance, p); // Invoke the skill
                                                playerCooldowns.put(skill.ID(), skill.cooldown()); // Set cooldown
                                                spendMana(p, skill.cost());
                                            } else {
                                                p.sendMessage(ChatColor.RED + "This item has a cooldown of " + (playerCooldowns.get(skill.ID()) / 20) + "s.");
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                                 InstantiationException e) {
                            e.printStackTrace();
                        }
                        String reforge = item.getPersistentDataContainer().get(getPDC("reforge"), PersistentDataType.STRING);
                        for (Class<?> clazz : getReforgeClasses()) {
                            if (clazz.getAnnotation(ReforgeMetadata.class).ID().equals(reforge)) {
                                try { // Invoke
                                    for (Method m : clazz.getDeclaredMethods()) {
                                        if (m.getName().equals("passive")) {
                                            Object skillInstance = clazz.getDeclaredConstructor().newInstance();
                                            m.invoke(skillInstance, p);
                                        }
                                    }
                                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                                         InstantiationException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    // Decrement cooldowns for the player
                    Map<String, Integer> playerCooldowns = cooldownMap.get(p.getUniqueId());
                    if (playerCooldowns != null) {
                        for (Class<?> clazz : getSkillClasses()) {
                            Skill skill2 = clazz.getAnnotation(Skill.class);
                            String skillID = skill2.ID();

                            // Decrease cooldown if it's greater than 0
                            if (playerCooldowns.get(skillID) > 0) {
                                playerCooldowns.put(skillID, playerCooldowns.get(skillID) - 1);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer(); // Player
        if (event.getHand() == EquipmentSlot.HAND) {
            for (ItemStack item : getItems(p)) {
                String id = getItemID(item);
                if (id == null) continue;
                if (event.getAction().isRightClick()) { // If right-click
                    if (p.isSneaking()) {
                        try { // Invoke
                            for (Class<?> skillClass : getMatchedSkill(id))
                                for (Method m : skillClass.getDeclaredMethods())
                                    if (m.getName().equals("onSneakRightClick")) {
                                        Object skillInstance = skillClass.getDeclaredConstructor().newInstance();
                                        Skill skill = skillClass.getAnnotation(Skill.class);
                                        Map<String, Integer> temp = Map.of(skill.ID(), 0);
                                        cooldownMap.putIfAbsent(p.getUniqueId(), temp);
                                        if (isQualified(p, getSkillAnnotation(skillClass).cost())) {
                                            if (cooldownMap.get(p.getUniqueId()).get(skill.ID()) == 0) {
                                                m.invoke(skillInstance, p);
                                                cooldownMap.get(p.getUniqueId()).put(skill.ID(), skill.cooldown());
                                                spendMana(p, skill.cost());
                                            } else {
                                                p.sendMessage(ChatColor.RED + "This item has cooldown of " + ((double) cooldownMap.get(p.getUniqueId()).get(skill.ID()) / 20) + "s.");
                                            }
                                        }
                                    }
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                                 InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                    try { // Invoke
                        for (Class<?> skillClass : getMatchedSkill(id))
                            for (Method m : skillClass.getDeclaredMethods())
                                if (m.getName().equals("onRightClick")) {
                                    Object skillInstance = skillClass.getDeclaredConstructor().newInstance();
                                    Skill skill = skillClass.getAnnotation(Skill.class);
                                    Map<String, Integer> temp = Map.of(skill.ID(), 0);
                                    cooldownMap.putIfAbsent(p.getUniqueId(), temp);
                                    if (isQualified(p, getSkillAnnotation(skillClass).cost())) {
                                        if (cooldownMap.get(p.getUniqueId()).get(skill.ID()) == 0) {
                                            m.invoke(skillInstance, p);
                                            cooldownMap.get(p.getUniqueId()).put(skill.ID(), skill.cooldown());
                                            spendMana(p, skill.cost());
                                        } else {
                                            p.sendMessage(ChatColor.RED + "This item has cooldown of " + ((double) cooldownMap.get(p.getUniqueId()).get(skill.ID()) / 20) + "s.");
                                        }
                                    }
                                }
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                             InstantiationException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction().isLeftClick()) { // If left-click
                    if (p.isSneaking()) {
                        try { // Invoke
                            for (Class<?> skillClass : getMatchedSkill(id))
                                for (Method m : skillClass.getDeclaredMethods())
                                    if (m.getName().equals("onSneakLeftClick")) {
                                        Object skillInstance = skillClass.getDeclaredConstructor().newInstance();
                                        Skill skill = skillClass.getAnnotation(Skill.class);
                                        Map<String, Integer> temp = Map.of(skill.ID(), 0);
                                        cooldownMap.putIfAbsent(p.getUniqueId(), temp);
                                        if (isQualified(p, getSkillAnnotation(skillClass).cost())) {
                                            if (cooldownMap.get(p.getUniqueId()).get(skill.ID()) == 0) {
                                                m.invoke(skillInstance, p);
                                                cooldownMap.get(p.getUniqueId()).put(skill.ID(), skill.cooldown());
                                                spendMana(p, skill.cost());
                                            } else {
                                                p.sendMessage(ChatColor.RED + "This item has cooldown of " + ((double) cooldownMap.get(p.getUniqueId()).get(skill.ID()) / 20) + "s.");
                                            }
                                        }
                                    }
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                                 InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                    try { // Invoke
                        for (Class<?> skillClass : getMatchedSkill(id))
                            for (Method m : skillClass.getDeclaredMethods())
                                if (m.getName().equals("onLeftClick")) {
                                    Object skillInstance = skillClass.getDeclaredConstructor().newInstance();
                                    Skill skill = skillClass.getAnnotation(Skill.class);
                                    Map<String, Integer> temp = Map.of(skill.ID(), 0);
                                    cooldownMap.putIfAbsent(p.getUniqueId(), temp);
                                    if (isQualified(p, getSkillAnnotation(skillClass).cost())) {
                                        if (cooldownMap.get(p.getUniqueId()).get(skill.ID()) == 0) {
                                            m.invoke(skillInstance, p);
                                            cooldownMap.get(p.getUniqueId()).put(skill.ID(), skill.cooldown());
                                            spendMana(p, skill.cost());
                                        } else {
                                            p.sendMessage(ChatColor.RED + "This item has cooldown of " + ((double) cooldownMap.get(p.getUniqueId()).get(skill.ID()) / 20) + "s!");
                                        }
                                    }
                                }
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                             InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player p = event.getPlayer(); // Player
        for (ItemStack item : getItems(p)) {
            String id = getItemID(item);
            if (id == null) continue;
            if (!event.getPlayer().isSneaking()) { // If player "start" sneaking
                try { // Invoke
                    for (Class<?> skillClass : getMatchedSkill(id))
                        for (Method m : skillClass.getDeclaredMethods())
                            if (m.getName().equals("onSneak")) {
                                Object skillInstance = skillClass.getDeclaredConstructor().newInstance();
                                Skill skill = skillClass.getAnnotation(Skill.class);
                                Map<String, Integer> temp = Map.of(skill.ID(), 0);
                                cooldownMap.putIfAbsent(p.getUniqueId(), temp);
                                if (isQualified(p, getSkillAnnotation(skillClass).cost())) {
                                    if (cooldownMap.get(p.getUniqueId()).get(skill.ID()) == 0) {
                                        m.invoke(skillInstance, p);
                                        cooldownMap.get(p.getUniqueId()).put(skill.ID(), skill.cooldown());
                                        spendMana(p, skill.cost());
                                    } else {
                                        p.sendMessage(ChatColor.RED + "This item has cooldown of " + ((double) cooldownMap.get(p.getUniqueId()).get(skill.ID()) / 20) + "s.");
                                    }
                                }
                            }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                         InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        Player p = event.getPlayer(); // Player
        for (ItemStack item : getItems(p)) {
            String id = getItemID(item);
            if (id == null) continue;
            try { // Invoke
                for (Class<?> skillClass : getMatchedSkill(id))
                    for (Method m : skillClass.getDeclaredMethods())
                        if (m.getName().equals("onJump")) {
                            Object skillInstance = skillClass.getDeclaredConstructor().newInstance();
                            Skill skill = skillClass.getAnnotation(Skill.class);
                            Map<String, Integer> temp = Map.of(skill.ID(), 0);
                            cooldownMap.putIfAbsent(p.getUniqueId(), temp);
                            if (isQualified(p, getSkillAnnotation(skillClass).cost())) {
                                if (cooldownMap.get(p.getUniqueId()).get(skill.ID()) == 0) {
                                    m.invoke(skillInstance, p);
                                    cooldownMap.get(p.getUniqueId()).put(skill.ID(), skill.cooldown());
                                    spendMana(p, skill.cost());
                                } else {
                                    p.sendMessage(ChatColor.RED + "This item has cooldown of " + ((double) cooldownMap.get(p.getUniqueId()).get(skill.ID()) / 20) + "s.");
                                }
                            }
                        }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player p) { // If attack is player
            for (ItemStack item : getItems(p)) {
                String id = getItemID(item);
                if (id == null) continue;
                try { // Invoke
                    for (Class<?> skillClass : getMatchedSkill(id))
                        for (Method m : skillClass.getDeclaredMethods())
                            if (m.getName().equals("onHit")) {
                                Object skillInstance = skillClass.getDeclaredConstructor().newInstance();
                                Skill skill = skillClass.getAnnotation(Skill.class);
                                Map<String, Integer> temp1 = Map.of(skill.ID(), 0);
                                Map<String, Boolean> temp2 = Map.of(skill.ID(), false);
                                cooldownMap.putIfAbsent(p.getUniqueId(), temp1);
                                setSkillMapOfEntity.getSkillMap().putIfAbsent(p.getUniqueId(), temp2);
                                if (setSkillMapOfEntity.getSkillMap().get(p.getUniqueId()).get(skill.ID())) {
                                    m.invoke(skillInstance, p, event.getEntity(), event.getDamage());
                                }
                            }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                         InstantiationException e) {
                    e.printStackTrace();
                }

                String reforge = item.getPersistentDataContainer().get(getPDC("reforge"), PersistentDataType.STRING);
                for (Class<?> clazz : getReforgeClasses()) {
                    if (clazz.getAnnotation(ReforgeMetadata.class).ID().equals(reforge)) {
                        try { // Invoke
                            for (Method m : clazz.getDeclaredMethods()) {
                                if (m.getName().equals("onHit")) {
                                    Object skillInstance = clazz.getDeclaredConstructor().newInstance();
                                    m.invoke(skillInstance, p, event.getEntity(), event.getDamage());
                                }
                            }
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                                 InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private Class<?>[] getMatchedSkill(String id) {
        // Handed item
        for (Class<?> clazz1 : getItemClasses()) { // All item classes
            ItemMetadata meta = clazz1.getAnnotation(ItemMetadata.class); // Metadata of item class
            if (meta.ID().equals(id)) return meta.skills(); // Skill list of item class
        }
        return null;
    }
}