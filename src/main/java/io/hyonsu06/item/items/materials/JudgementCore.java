package io.hyonsu06.item.items.materials;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import org.bukkit.Material;

@ItemTagged
@ItemMetadata(
        ID = "judgement_core",
        name = "Judgement Core",
        description = "Core of the enderman slayer.",
        rarity = ItemRarity.LEGENDARY,
        type = ItemType.NONE,
        material = Material.PLAYER_HEAD,
        texture = "https://textures.minecraft.net/texture/2f3ddd7f81089c85b26ed597675519f03a1dcd6d1713e0cfc66afb8743cbe0"
)
public class JudgementCore {}
