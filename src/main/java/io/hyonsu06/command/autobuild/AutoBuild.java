package io.hyonsu06.command.autobuild;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static io.hyonsu06.Main.isReloading;
import static io.hyonsu06.Main.plugin;
import static org.bukkit.Bukkit.*;
/*
public class AutoBuild implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] strings) {
        broadcastMessage(commandSender.getName() + " started reloading! Server might lag for a minute!");
        getPluginManager().disablePlugin(plugin);
        Thread thread = new Thread(() -> {
            try {
                Process process = Runtime.getRuntime().exec("./reload-server.sh");
                Scanner scanner = new Scanner(process.getInputStream());
                while (process.isAlive()) {
                    String s = "[AutoBuild] " + scanner.nextLine();
                    getLogger().info(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchElementException ignored) {
            }
        });

        thread.start();
        try {
            thread.join();

            isReloading = true;
            getPluginManager().enablePlugin(plugin);

            commandSender.sendMessage("Reload Completed.");
        } catch (InterruptedException e) {
            getLogger().severe("Main thread was interrupted while waiting.");
            e.printStackTrace();
        }
        return true;
    }
}
 */
public class AutoBuild implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] strings) {
        // Step 1: Disable the plugin
        getPluginManager().disablePlugin(plugin);

        // Step 2: Create and start a thread to run the rebuild process
        Thread thread = new Thread(() -> {
            Process process = null;
            Scanner scanner = null;
            try {
                // Run the rebuild script (which presumably deletes, rebuilds, and moves the new JAR file)
                process = Runtime.getRuntime().exec("./reload-server.sh");
                scanner = new Scanner(process.getInputStream());

                // Log the output from the rebuild process
                while (process.isAlive()) {
                    if (scanner.hasNextLine()) {
                        String s = "[AutoBuild] " + scanner.nextLine();
                        getLogger().info(s);
                    }
                }

                // Wait for the process to complete
                process.waitFor();

            } catch (IOException | InterruptedException e) {
                getLogger().severe("Error during rebuild process.");
                e.printStackTrace();
            } finally {
                // Ensure the scanner and process are closed
                if (scanner != null) {
                    scanner.close();
                }
                if (process != null) {
                    process.destroy();
                }
            }
        });

        // Step 3: Start the rebuild process and wait for it to complete
        thread.start();
        try {
            thread.join(); // Wait for the rebuild thread to complete

            // Step 4: Re-enable the plugin after rebuild
            isReloading = true;
            getPluginManager().enablePlugin(plugin);

            // Notify the command sender that the process is complete
            commandSender.sendMessage("Reload Completed.");

        } catch (InterruptedException e) {
            getLogger().severe("Main thread was interrupted while waiting.");
            e.printStackTrace();
        }
        return true;
    }
}
