package io.hyonsu06.command.items;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.items.items.ItemAdditiveBonus;
import io.hyonsu06.core.annotations.items.items.ItemMultiplicativeBonus;
import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.annotations.tags.SkillTagged;
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

import java.lang.reflect.Method;
import java.util.*;

import static io.hyonsu06.Main.plugin;
import static io.hyonsu06.core.Refresher.*;
import static io.hyonsu06.core.functions.CustomHead.setTexture;
import static io.hyonsu06.core.functions.ItemTypeForSlot.getReforgeType;
import static io.hyonsu06.core.functions.NumberTweaks.*;
import static io.hyonsu06.core.functions.getClasses.getItemClasses;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.*;
import static java.text.MessageFormat.format;
import static org.bukkit.Bukkit.getLogger;

public class LoadItems {
    public static ArrayList<ItemStack> items = new ArrayList<>();
    public static final int WORDS_PER_LINE = 5;

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

        // Display material, lore, and data

        // Base item stat
        if (clazz.isAnnotationPresent(ItemStats.class)) {
            ItemStats stats = clazz.getAnnotation(ItemStats.class);
            baseStatLore(
                    lore,
                    stats.damage(), stats.strength(), stats.critChance(), stats.critDamage(),
                    stats.ferocity(), stats.attackSpeed(),
                    stats.health(), stats.defense(), stats.speed(), stats.intelligence(), stats.agility(),
                    stats.healthRegen(), stats.manaRegen(),
                    stats.luck(),
                    new HashMap<>(), 0, 0, getReforgeType(metadata.type())
            );
        }

        // Additive bonus
        if (clazz.isAnnotationPresent(ItemAdditiveBonus.class)) {
            ItemAdditiveBonus bonus1 = clazz.getAnnotation(ItemAdditiveBonus.class);
            additiveStatLore(
                    lore,
                    bonus1.damage(), bonus1.strength(), bonus1.critChance(), bonus1.critDamage(),
                    bonus1.ferocity(), bonus1.attackSpeed(),
                    bonus1.health(), bonus1.defense(), bonus1.speed(), bonus1.intelligence(), bonus1.agility(),
                    bonus1.healthRegen(), bonus1.manaRegen(),
                    bonus1.luck()
            );
        }

        // Multiplicative bonus
        if (clazz.isAnnotationPresent(ItemMultiplicativeBonus.class)) {
            ItemMultiplicativeBonus bonus2 = clazz.getAnnotation(ItemMultiplicativeBonus.class);
            multiplicativeStatLore(
                    lore,
                    bonus2.damage(), bonus2.strength(), bonus2.critChance(), bonus2.critDamage(),
                    bonus2.ferocity(), bonus2.attackSpeed(),
                    bonus2.health(), bonus2.defense(), bonus2.speed(), bonus2.intelligence(), bonus2.agility(),
                    bonus2.healthRegen(), bonus2.manaRegen(),
                    bonus2.luck()
            );
        }

        if (!metadata.description().isEmpty()) {
            lore.addAll(addSkillDescription(metadata.description(), WORDS_PER_LINE, new long[]{}, ChatColor.GRAY + ""));
            lore.add(" ");
        }

        for (Class<?> clazz2 : clazz.getAnnotation(ItemMetadata.class).skills()) {
            Skill skills = clazz2.getAnnotation(Skill.class);
            for (Method method : clazz2.getMethods()) {
                if (method.isAnnotationPresent(SkillTagged.class)) {
                    if (!skills.name().isEmpty()) {
                        if (!skills.simpleDescription()) {
                            lore.add(format(ChatColor.GOLD + "Ability: {0}  {1}",
                                    skills.name(),
                                    ChatColor.BOLD + "", ChatColor.YELLOW + method.getName())
                            );

                            lore.addAll(addSkillDescription(skills.description(), WORDS_PER_LINE, skills.args(), ChatColor.GRAY + ""));

                            if (skills.cost() != 0)
                                lore.add(format(ChatColor.DARK_GRAY + "Mana cost: {0}", ChatColor.AQUA + String.valueOf(skills.cost())));
                            if (skills.cooldown() != 0) {
                                double value = ((double) skills.cooldown()) / 20;
                                lore.add(format(ChatColor.DARK_GRAY + "Cooldown: {0}s", ChatColor.AQUA + String.valueOf(value)));
                            }
                            lore.add(" ");
                        } else {
                            lore.addAll(addSkillDescription(skills.description(), WORDS_PER_LINE, skills.args(), ChatColor.GRAY + ""));
                            lore.add(" ");
                        }
                    }
                }
            }
        }

        if (metadata.durability() != Long.MIN_VALUE) lore.add(ChatColor.DARK_GRAY + "Durability: " + metadata.durability() + "/" + metadata.durability());


        meta.setDisplayName(metadata.rarity().getColor() + metadata.name());

        meta.getPersistentDataContainer().set(getPDC("id"), PersistentDataType.STRING, metadata.ID());
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

    // This method is based on generation of ChatGPT
    public List<String> addSkillDescription(String inputText, int wordsPerLine, long[] args, String color) {
        if (!inputText.isEmpty()) {
            // Replace placeholders in the input text with corresponding args
            for (int i = 0; i < args.length; i++) {
                inputText = inputText.replace("{" + i + "}", numberFormat(args[i]));
            }

            // Split the input text by spaces while keeping placeholders as whole words
            String[] words = inputText.split(" (?=(\\{\\w+}))| ");
            List<String> lines = new ArrayList<>(); // List to hold formatted lines

            StringBuilder currentLine = new StringBuilder();
            int wordCount = 0;

            for (String word : words) {
                // Trim leading and trailing whitespace from the word
                word = word.trim();
                if (!word.isEmpty()) {
                    // Check if the word is a placeholder (e.g., {0})
                    if (word.matches("\\{\\d+}")) {
                        // Append the formatted placeholder with ChatColor.AQUA
                        currentLine.append(ChatColor.AQUA).append(word).append(" ");
                    } else {
                        // Append ChatColor.GRAY for other words
                        currentLine.append(color).append(word).append(" ");
                    }
                    wordCount++;

                    if (wordCount == wordsPerLine) {
                        lines.add(currentLine.toString().trim()); // Add the current line to the list
                        currentLine.setLength(0); // Clear the StringBuilder for the next line
                        wordCount = 0; // Reset the word count for the next line
                    }
                }
            }

            // Add any remaining text as the last line
            if (!currentLine.isEmpty()) {
                lines.add(currentLine.toString().trim());
            }

            return lines; // Return the list of lines
        }
        return List.of();
    }

    private String returnTypeName(String method) {
        return switch(method) {
            case "onRightClick" -> "RIGHT CLICK";
            case "onLeftClick" -> "LEFT CLICK";
            case "onSneakRightClick" -> "SNEAK RIGHT CLICK";
            case "onSneak LeftClick" -> "SNEAK LEFT CLICK";
            case "onSneak" -> "SNEAK";
            case "onJump" -> "JUMP";
            default -> "";
        };
    }

    public void registerAllItems() {
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
