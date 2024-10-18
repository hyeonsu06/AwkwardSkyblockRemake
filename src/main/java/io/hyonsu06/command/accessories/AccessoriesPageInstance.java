package io.hyonsu06.command.accessories;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class AccessoriesPageInstance {
    private static final HashMap<Player, Integer> accessoryPage = new HashMap<>();
    public static int getPage(Player p) {
        if (!accessoryPage.containsKey(p)) accessoryPage.put(p, 0);
        return accessoryPage.get(p);
    }

    public static void nextPage(Player p) {
        accessoryPage.put(p, getPage(p) + 1);
    }

    public static void previousPage(Player p) {
        if (accessoryPage.get(p) == null || accessoryPage.get(p) == 0) {
            accessoryPage.put(p, 0);
            return;
        }
        accessoryPage.put(p, getPage(p) - 1);
    }
}
