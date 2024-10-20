package io.hyonsu06.command.stat;

import io.hyonsu06.core.enums.Stats;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class setStatTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
        } else if (args.length == 2) {
            for (Stats stat : Stats.values()) {
                suggestions.add(stat.name());
            }
        } else if (args.length == 3) {
            suggestions.add("get");
            suggestions.add("set");
        } else if (args.length == 4) {
            suggestions.add("<value>");
        }

        return suggestions;
    }
}
