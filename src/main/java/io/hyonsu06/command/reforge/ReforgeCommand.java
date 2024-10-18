package io.hyonsu06.command.reforge;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;
import static org.bukkit.Bukkit.getLogger;

public class ReforgeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            return false;
        }

        if (sender instanceof Player p) {
            String reforge = args[0];
            if (reforge == null) {
                p.sendMessage(ChatColor.RED + "You must provide a reforge to apply!");
                return true;
            }

            if (p.getInventory().getItemInMainHand().isEmpty()) {
                p.sendMessage(ChatColor.RED + "You must hand a item to apply reforge!");
                return true;
            }

            ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
            meta.getPersistentDataContainer().set(getPDC("reforge"), PersistentDataType.STRING, args[0].toLowerCase());
            p.getInventory().getItemInMainHand().setItemMeta(meta);
            p.sendMessage(ChatColor.GREEN + "Successfully applied reforge " + args[0] + " to " + p.getInventory().getItemInMainHand().getItemMeta().getDisplayName());
            return true;
        }
        return true;
    }
}
