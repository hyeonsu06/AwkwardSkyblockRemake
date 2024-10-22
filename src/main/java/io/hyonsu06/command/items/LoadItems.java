package io.hyonsu06.command.items;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.items.items.ItemAdditiveBonus;
import io.hyonsu06.core.annotations.items.items.ItemMultiplicativeBonus;
import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.interfaces.SkillMethods;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static io.hyonsu06.Main.plugin;
import static io.hyonsu06.core.Refresher.*;
import static io.hyonsu06.core.functions.CustomHead.setTexture;
import static io.hyonsu06.core.functions.ItemTypeForSlot.getReforgeType;
import static io.hyonsu06.core.functions.getClasses.getItemClasses;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.*;
import static java.text.MessageFormat.format;
import static org.bukkit.Bukkit.getLogger;

public class LoadItems {
    public static ArrayList<ItemStack> items = new ArrayList<>();
    public static final int WORDS_PER_LINE = 8000;

    public ItemStack get(Class<?> clazz) {

        // Default item
        ItemMetadata metadata = clazz.getAnnotation(ItemMetadata.class);
        ItemStack item = new ItemStack(metadata.material());
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        // Remove vanilla attributes
        for (Attribute attr : Attribute.values()) meta.addAttributeModifier(attr, new AttributeModifier(getPDC("nope"), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_STORED_ENCHANTS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_UNBREAKABLE
        );

        // Base item stat
        if (clazz.isAnnotationPresent(ItemStats.class)) {
            ItemStats stats = clazz.getAnnotation(ItemStats.class);
            baseStatLore(
                    lore,
                    stats.damage(), stats.strength(), stats.critChance(), stats.critDamage(),
                    stats.ferocity(), stats.attackSpeed(),
                    stats.health(), stats.defense(), stats.speed(), stats.intelligence(), stats.agility(),
                    stats.healthRegen(), stats.manaRegen(),
                    stats.swingRange(),
                    stats.luck(),
                    new HashMap<>(), 0, 0, getReforgeType(metadata.type()),
                    null
            );
        }

        // Additive bonus
        if (clazz.isAnnotationPresent(ItemAdditiveBonus.class)) {
            ItemAdditiveBonus bonus1 = clazz.getAnnotation(ItemAdditiveBonus.class);
            additiveStatLore(
                    lore,
                    bonus1.add_damage(), bonus1.add_strength(), bonus1.add_critChance(), bonus1.add_critDamage(),
                    bonus1.add_ferocity(), bonus1.add_attackSpeed(),
                    bonus1.add_health(), bonus1.add_defense(), bonus1.add_speed(), bonus1.add_intelligence(), bonus1.add_agility(),
                    bonus1.add_healthRegen(), bonus1.add_manaRegen(),
                    bonus1.add_swingRange(),
                    bonus1.add_luck()
            );
        }

        // Multiplicative bonus
        if (clazz.isAnnotationPresent(ItemMultiplicativeBonus.class)) {
            ItemMultiplicativeBonus bonus2 = clazz.getAnnotation(ItemMultiplicativeBonus.class);
            multiplicativeStatLore(
                    lore,
                    bonus2.mul_damage(), bonus2.mul_strength(), bonus2.mul_critChance(), bonus2.mul_critDamage(),
                    bonus2.mul_ferocity(), bonus2.mul_attackSpeed(),
                    bonus2.mul_health(), bonus2.mul_defense(), bonus2.mul_speed(), bonus2.mul_intelligence(), bonus2.mul_agility(),
                    bonus2.mul_healthRegen(), bonus2.mul_manaRegen(),
                    bonus2.mul_swingRange(),
                    bonus2.mul_luck()
            );
        }

        if (!metadata.description().isEmpty()) {
            lore.addAll(addSkillDescription(metadata.description(), WORDS_PER_LINE, new long[]{}, ChatColor.GRAY, ChatColor.AQUA));
            lore.add(" ");
        }

        for (Class<?> clazz2 : clazz.getAnnotation(ItemMetadata.class).skills()) {
            Skill skills = clazz2.getAnnotation(Skill.class);

            // Add skill name and general information once per skill class
            if (!skills.name().isEmpty()) {
                if (!skills.simpleDescription()) {
                    lore.add(format(ChatColor.GOLD + "Ability: {0}  {1}",
                            skills.name(),
                            ChatColor.BOLD + "", ChatColor.YELLOW + "")
                    );

                    lore.addAll(addSkillDescription(skills.description(), WORDS_PER_LINE, skills.args(), ChatColor.GRAY, ChatColor.AQUA));

                    if (skills.cost() != 0) {
                        lore.add(format(ChatColor.DARK_GRAY + "Mana cost: {0}", ChatColor.AQUA + String.valueOf(skills.cost())));
                    }
                    if (skills.cooldown() != 0) {
                        double value = ((double) skills.cooldown()) / 20;
                        lore.add(format(ChatColor.DARK_GRAY + "Cooldown: {0}s", ChatColor.AQUA + String.valueOf(value)));
                    }
                    lore.add(" ");
                } else {
                    lore.addAll(addSkillDescription(skills.description(), WORDS_PER_LINE, skills.args(), ChatColor.GRAY, ChatColor.AQUA));
                    lore.add(" ");
                }
            }
        }

        if (metadata.durability() != Long.MIN_VALUE) lore.add(ChatColor.DARK_GRAY + "Durability: " + metadata.durability());


        meta.setDisplayName(metadata.rarity().getColor() + metadata.name());

        meta.getPersistentDataContainer().set(getPDC("id"), PersistentDataType.STRING, metadata.ID());
        meta.getPersistentDataContainer().set(getPDC("type"), PersistentDataType.STRING, metadata.type().getDisplay().toLowerCase().replace(" ", "_"));
        meta.getPersistentDataContainer().set(getPDC("damage"), PersistentDataType.LONG, 0L);

        lore.add(metadata.rarity().getColor() + "" + ChatColor.BOLD + (metadata.rarity().getDisplay() + " " + metadata.type().getDisplay()).toUpperCase());

        meta.setMaxStackSize(metadata.stack());

        meta.setLore(lore);

        for (Material material : new Material[]{Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET, Material.LEATHER_HORSE_ARMOR}) {
            if (item.getType().equals(material)) {
                LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
                leatherMeta.setColor(Color.fromRGB(metadata.red(), metadata.green(), metadata.blue()));
                meta = leatherMeta;
            }
        }

        if (item.getType().equals(Material.PLAYER_HEAD)) {
            SkullMeta skullMeta = (SkullMeta) meta;
            String url = metadata.texture();
            setTexture(skullMeta, url);
            meta = skullMeta;
        }

        item.setItemMeta(meta);
        item.setType(metadata.material());
        return item;
    }

    public void registerAllItems() {
        items = new ArrayList<>();
        for (Class<?> clazz : getItemClasses()) {
            getLogger().info("[" + plugin.getName() + "] Loading item: " + clazz.getSimpleName());
            Class<? extends SkillMethods>[] skills = clazz.getAnnotation(ItemMetadata.class).skills();
            if (!Arrays.toString(skills).equals("[]")) {
                getLogger().info("[" + plugin.getName() + "] Item's skill(s) found:");
                for (Class<? extends SkillMethods> clazz2 : skills) getLogger().info("[" + plugin.getName() + "] - " + clazz2.getSimpleName());
            }
            items.add(get(clazz));
        }
    }
}
