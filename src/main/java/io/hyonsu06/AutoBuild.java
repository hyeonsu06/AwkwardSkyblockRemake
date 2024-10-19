package io.hyonsu06;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static io.hyonsu06.Main.isReloading;
import static io.hyonsu06.Main.plugin;
import static org.bukkit.Bukkit.getPluginManager;

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
        Thread thread = new Thread(() -> {
            try {
                Process process = Runtime.getRuntime().exec("./reload-server.sh");
                Scanner scanner = new Scanner(process.getInputStream());
                while (process.isAlive()) {
                    String st = "[AutoBuild] " + scanner.nextLine();
                    System.out.println(st);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchElementException ignored) {
            }
        });

        thread.start();
        try {
            // Wait for the thread to complete its work
            thread.join();

            isReloading = true;
            getPluginManager().disablePlugin(plugin);
            getPluginManager().enablePlugin(plugin);

            commandSender.sendMessage("Reload Completed.");
        } catch (InterruptedException e) {
            System.err.println("Main thread was interrupted while waiting.");
        }
        return true;
    }
}