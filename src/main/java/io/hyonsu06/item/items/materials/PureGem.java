package io.hyonsu06.item.items.materials;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import org.bukkit.Material;

@ItemTagged
@ItemMetadata(
        ID = "pure_gem",
        name = "Pure Gem",
        description = "Might be used for craft some items.",
        rarity = ItemRarity.EPIC,
        type = ItemType.NONE,
        material = Material.EMERALD
)
public class PureGem {}
