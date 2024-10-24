package io.hyonsu06.core.functions;

import io.hyonsu06.core.managers.entity.EntityManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class checkAndSpendMana {
    public static boolean isQualified(Player p, double mana) {
        double playerMana = EntityManager.getPlayerIntelligence().get(p.getUniqueId());
        if (playerMana >= mana) return true;
        p.sendMessage(ChatColor.RED + "You do not have enough mana!");
        return false;
    }

    public static void spendMana(Player p, double mana) {
        EntityManager.getPlayerIntelligence().compute(p.getUniqueId(), (k, playerMana) -> playerMana - mana);
    }
}
