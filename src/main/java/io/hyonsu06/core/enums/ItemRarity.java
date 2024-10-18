package io.hyonsu06.core.enums;

import lombok.Getter;
import org.bukkit.ChatColor;

/**
 * Enum representing different item rarities.
 * <p>
 * Each rarity has an associated color for display and a name for identification.
 */
@Getter
public enum ItemRarity {
    COMMON(ChatColor.WHITE, "Common"),
    UNCOMMON(ChatColor.GREEN, "Uncommon"),
    RARE(ChatColor.BLUE, "Rare"),
    EPIC(ChatColor.DARK_PURPLE, "Epic"),
    LEGENDARY(ChatColor.GOLD, "Legendary"),
    MYTHIC(ChatColor.LIGHT_PURPLE, "Mythic"),
    DIVINE(ChatColor.AQUA, "Divine"),
    SPECIAL(ChatColor.RED, "Special"),
    VERY_SPECIAL(ChatColor.RED, "Very Special"),
    VERY_VERY_SPECIAL(ChatColor.DARK_RED, "Very very Special"),
    ADMIN(ChatColor.DARK_RED, "Admin");

    private final ChatColor color;
    private final String display;

    ItemRarity(ChatColor color, String display) {
        this.color = color;
        this.display = display;
    }

    /**
     * Get the next rarity for upgrades.
     * <p>
     * If the current rarity is the last one, it returns itself.
     *
     * @return The next rarity in the sequence or itself if it's the last.
     */
    public ItemRarity next() {
        return next(this);
    }

    /**
     * Get the next rarity after the specified rarity.
     *
     * @param rarity The current rarity.
     * @return The next rarity in the sequence, or the last rarity if the current is the last one.
     */
    public static ItemRarity next(ItemRarity rarity) {
        ItemRarity[] values = ItemRarity.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == rarity) {
                return (i + 1 < values.length) ? values[i + 1] : rarity;
            }
        }
        return rarity; // Return itself if rarity is not found
    }
}
