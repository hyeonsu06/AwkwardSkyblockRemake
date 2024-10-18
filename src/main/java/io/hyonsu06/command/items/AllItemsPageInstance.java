package io.hyonsu06.command.items;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class AllItemsPageInstance {
    private static final HashMap<Player, Integer> page = new HashMap<>();
    public static int getPage(Player p) {
        if (!page.containsKey(p)) page.put(p, 0);
        return page.get(p);
    }

    public static void nextPage(Player p) {
        page.put(p, getPage(p) + 1);
    }

    public static void previousPage(Player p) {
        if (page.get(p) == 0) return;
        page.put(p, getPage(p) - 1);
    }
}
