package io.hyonsu06.command.recombobulator;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;

public class RecombobulatorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player p) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                p.sendMessage(ChatColor.RED + "Please hand a valid item!");
                return true;
            }

            boolean isRecombobulatorPresent;
            boolean isRecombobulatorEXPresent;
            Object temp1 = meta.getPersistentDataContainer().get(getPDC("recombobulator"), PersistentDataType.BOOLEAN);
            Object temp2 = meta.getPersistentDataContainer().get(getPDC("recombobulatorEX"), PersistentDataType.BOOLEAN);
            if (temp1 == null) isRecombobulatorPresent = false; else isRecombobulatorPresent = (boolean) temp1;
            if (temp2 == null) isRecombobulatorEXPresent = false; else isRecombobulatorEXPresent = (boolean) temp2;

            if (isRecombobulatorEXPresent) {
                meta.getPersistentDataContainer().set(getPDC("recombobulatorEX"), PersistentDataType.BOOLEAN, false);
                meta.getPersistentDataContainer().set(getPDC("recombobulator"), PersistentDataType.BOOLEAN, false);
                item.setItemMeta(meta);
                p.sendMessage(ChatColor.GREEN + "Removed recombobulators from your item!");
                return true;
            }
            if (isRecombobulatorPresent) {
                meta.getPersistentDataContainer().set(getPDC("recombobulatorEX"), PersistentDataType.BOOLEAN, true);
                item.setItemMeta(meta);
                p.sendMessage(ChatColor.GREEN + "Applied recombobulator EX to your item!");
                return true;
            }
            meta.getPersistentDataContainer().set(getPDC("recombobulator"), PersistentDataType.BOOLEAN, true);
            item.setItemMeta(meta);
            p.sendMessage(ChatColor.GREEN + "Applied recombobulator to your item!");
        } else commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
        return true;
    }
}
