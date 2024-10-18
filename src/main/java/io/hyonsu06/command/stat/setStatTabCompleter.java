package io.hyonsu06.command.stat;

import io.hyonsu06.core.enums.Stats;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class setStatTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            List<LivingEntity> list = new ArrayList<>(Bukkit.getOnlinePlayers());
            if (sender instanceof Player p) {
                List<Entity> entities = (p.getNearbyEntities(5, 5, 5));
                for (Entity e : entities) if (e instanceof LivingEntity le) list.add(le);
            }
            for (LivingEntity e : list) {
                TextComponent textComponent = new TextComponent(e.getName());
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(e.getUniqueId() + "\n" + e.getName() + "\n" + e.getCustomName()).create()));
                suggestions.add(textComponent.toPlainText());
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
