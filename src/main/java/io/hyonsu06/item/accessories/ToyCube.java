package io.hyonsu06.item.accessories;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.items.items.ItemStats;
import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import org.bukkit.Material;

@ItemTagged
@ItemMetadata(
        ID = "toy_cube",
        name = "Toy Cube",
        rarity = ItemRarity.COMMON,
        type = ItemType.ACCESSORY,
        material = Material.PLAYER_HEAD,
        texture = "https://textures.minecraft.net/texture/f4e94273c727b1f2c9376b5cae4ed9a48d5851bd2ab2fd83d5f81a6e6aff193d"
        )
@ItemStats(
        intelligence = 100
)
public class ToyCube {}
