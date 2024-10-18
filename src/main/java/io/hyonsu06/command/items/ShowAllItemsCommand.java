package io.hyonsu06.command.items;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.hyonsu06.command.items.ShowAllItemsGUI.open;

public class ShowAllItemsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            open((Player) commandSender);
        } else {
            commandSender.sendMessage("You must be a player to use this command!");
        }
        return true;
    }
}
