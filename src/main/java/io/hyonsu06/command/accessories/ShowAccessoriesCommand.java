package io.hyonsu06.command.accessories;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.hyonsu06.command.accessories.ShowAccessoriesGUI.open;

public class ShowAccessoriesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player p) {
            open(p);
        } else {
            commandSender.sendMessage("You must be a player to use this command!");
        }
        return true;
    }
}
