package io.hyonsu06.core.functions;

import io.hyonsu06.core.enums.ItemType;

import static io.hyonsu06.core.enums.ReforgeType.*;

public class convertStringTypeToItemType {
    public static ItemType stringTypeToItemType(String s) {
        for (ItemType type : ItemType.values()) {
            if (type.getDisplay().equalsIgnoreCase(s)) {
                return type;
            }
        }
        return null;
    }
}
