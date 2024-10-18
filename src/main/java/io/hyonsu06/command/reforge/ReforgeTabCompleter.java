package io.hyonsu06.command.reforge;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static io.hyonsu06.core.functions.getClasses.getReforgeClasses;

public class ReforgeTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            for (Class<?> clazz : getReforgeClasses()) {
                suggestions.add(clazz.getSimpleName());
            }
        }
        return suggestions;
    }
}
