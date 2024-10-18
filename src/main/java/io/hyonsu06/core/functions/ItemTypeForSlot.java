package io.hyonsu06.core.functions;

import io.hyonsu06.core.enums.ItemType;
import io.hyonsu06.core.enums.ReforgeType;

import static io.hyonsu06.core.enums.ReforgeType.*;

public class ItemTypeForSlot {
    public static ReforgeType getReforgeType(ItemType type) {
        return switch (type) {
            case NONE, SWORD, LONGSWORD, PICKAXE, DRILL, SHOVEL, AXE, HOE, FISHING_ROD -> MELEE;
            case ARROW, PET, POTION, RUNE -> UNREFORGEABLE;
            case BOW, SHORTBOW -> RANGED;
            case HELMET, CHESTPLATE, LEGGINGS, BOOTS -> ARMOR;
            case ACCESSORY -> ACCESSORY;
        };
    }
}
