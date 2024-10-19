package io.hyonsu06.command.stat;

import io.hyonsu06.core.managers.StatManager;
import io.hyonsu06.core.enums.Stats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class setStatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 3) {
            return false;
        }

        String entityName = args[0];
        String statName = args[1];
        String action = args[2];
        String valueStr = "0";
        try {
            valueStr = args[3];
        } catch (ArrayIndexOutOfBoundsException ignored) {}

        Entity entity = Bukkit.getPlayer(entityName);

        if (action.equals("set")) {
            if (entity != null) {
                try {
                    Stats stat = Stats.valueOf(statName.toUpperCase());
                    double value = Double.parseDouble(valueStr);
                    StatManager.getBaseStatMap().get(entity.getUniqueId()).put(stat, value);

                    if (stat == Stats.HEALTH && value == 0) {
                        sender.sendMessage(ChatColor.RED + "You cannot set health to zero!");
                        return true;
                    }
                    if (value < 0) {
                        sender.sendMessage(ChatColor.RED + "You cannot set stat below zero!");
                        return true;
                    }
                    sender.sendMessage("Set " + stat.name() + " of " + entity.getName() + " to: " + value);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid stat!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Entity not found!");
            }
            return true;
        } else if (action.equals("get")) {
            if (entity != null) {
                try {
                    Stats stat = Stats.valueOf(statName.toUpperCase());
                    double value = StatManager.getFinalStatMap().get(entity.getUniqueId()).get(stat);

                    sender.sendMessage("Stat " + stat.name() + " of " + entity.getName() + " is: " + value);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid stat!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Entity not found!");
            }
            return true;
        } else {
            sender.sendMessage( ChatColor.RED + "Wrong argument! Must be get or set!");
            return true;
        }
    }
}
