package io.hyonsu06;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static io.hyonsu06.Main.plugin;

public class AutoBuild extends Command implements CommandExecutor {

    public AutoBuild() {
        super("autobuild", "Renews plugin automatically.", "autobuild", List.of("recompile"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        new Thread("AutoBuild") {
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec("./reload-server.sh");
                    Scanner scanner = new Scanner(process.getInputStream());
                    while(process.isAlive()) {
                        String st = "[AutoBuild] " + scanner.nextLine();
                        System.out.println(st);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } catch (NoSuchElementException ignored) {
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reload confirm");
                    }
                }.runTask(plugin);
            }
        }.start();
        return true;
    }
}