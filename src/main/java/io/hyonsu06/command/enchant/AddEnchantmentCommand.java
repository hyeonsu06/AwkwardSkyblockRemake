package io.hyonsu06.command.enchant;

import io.hyonsu06.item.enchantments.Enchantment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;
import static io.hyonsu06.item.enchantments.EnchantmentUtils.*;

public class AddEnchantmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        ItemStack item = player.getInventory().getItemInMainHand();

        if (args.length < 2) return false;

        String enchantmentName = args[0].toLowerCase().replace(" ", "_");
        int level = Integer.parseInt(args[1]);

        Enchantment enchantment = getEnchantmentByName(enchantmentName);
        if (enchantment == null) {
            player.sendMessage("Invalid enchantment!");
            return false;
        } else {
            if (level > 0) {
                player.getInventory().setItemInMainHand(addEnchantment(item, enchantment, level));
                player.sendMessage("Added " + enchantment.getName() + " " + level + " to your item!");
            } else if (level == 0) {
                Enchantment ench = getEnchantmentByName(enchantmentName);
                int origLevel;
                try {
                    origLevel = item.getPersistentDataContainer().get(getPDC(enchantmentName), PersistentDataType.INTEGER);
                } catch (NullPointerException ignored) {
                    sender.sendMessage("Enchant is not present!");
                    return true;
                }
                ench.deApplyEffect(player, item, origLevel);
                player.getInventory().setItemInMainHand(removeEnchantment(item, enchantment));
                player.sendMessage("Removed " + enchantment.getName() + " from your item!");
            } else {
                player.sendMessage("Wait what? Applying a negative level enchantments?");
            }
        }
        return true;
    }
}
