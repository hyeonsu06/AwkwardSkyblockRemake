package io.hyonsu06.command.enchant;

import io.hyonsu06.item.enchantments.Enchantment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static io.hyonsu06.item.enchantments.EnchantmentUtils.getAllEnchantments;

public class AddEnchantmentTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            for (Enchantment enchant : getAllEnchantments()) {
                String s = enchant.getName().toUpperCase().replace(" ", "_");
                suggestions.add(s);
            }
        }
        return suggestions;
    }
}
