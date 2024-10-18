package io.hyonsu06.command.potatobook;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;

public class PotatoBookCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof Player p) {
            String amountString = args[0];
            if (amountString.isBlank()) amountString = "0";
            int amount = Integer.parseInt(amountString);

            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                p.sendMessage(org.bukkit.ChatColor.RED + "Please hand a valid item!");
                return true;
            }

            meta.getPersistentDataContainer().set(getPDC("potato"), PersistentDataType.INTEGER, amount);
            item.setItemMeta(meta);
            p.sendMessage(ChatColor.GREEN + "Applied " + amount + " books to " + item.getItemMeta().getDisplayName());
            return true;
        } else commandSender.sendMessage(ChatColor.RED + "You must be a Player to use this command!");
        return true;
    }
}
