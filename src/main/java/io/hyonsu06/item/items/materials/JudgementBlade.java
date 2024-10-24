package io.hyonsu06.item.items.materials;

import io.hyonsu06.core.annotations.items.items.ItemMetadata;
import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.enums.ItemRarity;
import io.hyonsu06.core.enums.ItemType;
import org.bukkit.Material;

@ItemTagged
@ItemMetadata(
        ID = "judgement_blade",
        name = "Judgement Blade",
        description = "This can be either body of bow or blade of endermen slayer.",
        rarity = ItemRarity.LEGENDARY,
        type = ItemType.NONE,
        material = Material.STICK
)
public class JudgementBlade {}
