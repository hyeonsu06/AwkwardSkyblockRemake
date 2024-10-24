package io.hyonsu06.core.managers.crafting;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static io.hyonsu06.Main.plugin;
import static io.hyonsu06.core.functions.getClasses.getRecipeClasses;
import static io.hyonsu06.core.managers.crafting.CraftingGUI.get3x3Inventory;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;
import static io.hyonsu06.core.managers.crafting.CraftingGUI.get5x5Inventory;
import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class CraftingManager implements Listener {
    private final int[] slots3x3 = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};
    private final int[] slots5x5 = new int[]{0, 1, 2, 3, 4, 9, 10, 11, 12, 13, 18, 19, 20, 21, 22, 27, 28, 29, 30, 31, 36, 37, 38, 39, 40};

    private ItemStack validatorT;
    private ItemStack validatorF;
    private ItemStack resultItem;

    private Map<UUID, Boolean> isCustom = new HashMap<>();

    Map<String, List<Pair<String, Integer>>> recipe = new HashMap<>();
    Map<String, ItemStack> result = new HashMap<>();

    AtomicReference<Map<UUID, List<Integer>>> consumption = new AtomicReference<>();

    public CraftingManager() {
        validatorF = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta1 = validatorF.getItemMeta();
        meta1.getPersistentDataContainer().set(getPDC("no"), PersistentDataType.BOOLEAN, true);
        meta1.setHideTooltip(true);
        validatorF.setItemMeta(meta1);

        validatorT = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta2 = validatorT.getItemMeta();
        meta2.getPersistentDataContainer().set(getPDC("no"), PersistentDataType.BOOLEAN, true);
        meta2.setHideTooltip(true);
        validatorT.setItemMeta(meta2);

        resultItem = new ItemStack(Material.BARRIER);
        ItemMeta meta3 = resultItem.getItemMeta();
        meta3.getPersistentDataContainer().set(getPDC("no"), PersistentDataType.BOOLEAN, true);
        meta3.setHideTooltip(true);
        resultItem.setItemMeta(meta3);

        for (Class<?> clazz : getRecipeClasses()) {
            try {
                Object instance = clazz.newInstance();
                for (Method m : clazz.getMethods()) {
                    if (m.getName().equals("recipe"))
                        recipe.put(clazz.getSimpleName(), (List<Pair<String, Integer>>) clazz.getMethod("recipe").invoke(instance));
                    if (m.getName().equals("result"))
                        result.put(clazz.getSimpleName(), (ItemStack) clazz.getMethod("result").invoke(instance));
                }
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onClickTable(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType().equals(Material.CRAFTING_TABLE)) {
                event.setCancelled(true);
                event.getPlayer().openInventory(get3x3Inventory());
                isCustom.put(event.getPlayer().getUniqueId(), false);
            }
        }
    }

    @EventHandler
    public void preventGUIClick(InventoryClickEvent event) {
        if (isThis3x3GUI(event.getView())) {
            PersistentDataContainer pdc;
            try {
                pdc = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
            } catch (NullPointerException ignored) {
                return;
            }
            if (pdc.has(getPDC("no"), PersistentDataType.BOOLEAN)) {
                if (pdc.get(getPDC("no"), PersistentDataType.BOOLEAN).equals(true)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (isThis3x3GUI(event.getView())) {
            if (event.getSlot() == 25) {
                if (!isCustom.get(event.getWhoClicked().getUniqueId())) {
                    for (int slot : slots3x3) {
                        if (event.getView().getTopInventory().getItem(slot) != null) {
                            if (event.getView().getTopInventory().getItem(slot).getAmount() > 0) {
                                ItemStack material = event.getView().getTopInventory().getItem(slot);
                                material.setAmount(material.getAmount() - 1);
                                event.getView().getTopInventory().setItem(slot, material);
                            }
                        }
                    }
                    event.getView().getTopInventory().setItem(25, resultItem);
                    for (int i = 45; i < 54; i++) if (i != 49) event.getView().getTopInventory().setItem(i, validatorF);
                } else {
                    for (int slot : slots3x3) {
                        if (event.getView().getTopInventory().getItem(slot) != null) {
                            if (event.getView().getTopInventory().getItem(slot).getAmount() > 0) {
                                ItemStack material = event.getView().getTopInventory().getItem(slot);
                                material.setAmount(material.getAmount() - consumption.get().get(event.getWhoClicked().getUniqueId()).get(correspond3x3(slot)));
                                event.getView().getTopInventory().setItem(slot, material);
                            }
                        }
                    }
                }
                return;
            }
            if (event.getSlot() == 43) {
                event.getWhoClicked().openInventory(get5x5Inventory());
                return;
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> consumption.set(new HashMap<>(Map.of(event.getWhoClicked().getUniqueId(), updateCraftingResult(event.getView(), (Player) event.getWhoClicked())))), 1);
        }

        if (isThis5x5GUI(event.getView())) {
            if (event.getSlot() == 43) {
                event.getWhoClicked().openInventory(get3x3Inventory());
            }
        }

        if (isThis3x3GUI(event.getView()) || isThis5x5GUI(event.getView())) {
            if (event.getSlot() == 49 && event.getClickedInventory().equals(event.getView().getTopInventory())) {
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (isThis3x3GUI(event.getView())) {
            for (int slot1 : slots3x3) {
                for (int slot2 : event.getRawSlots()) {
                    if (slot1 == slot2) {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> consumption.set(new HashMap<>(Map.of(event.getWhoClicked().getUniqueId(), updateCraftingResult(event.getView(), (Player) event.getWhoClicked())))), 1);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (isThis3x3GUI(event.getView())) {
            for (int slot : slots3x3) {
                Player p = (Player) event.getPlayer();
                if (event.getView().getTopInventory().getItem(slot) != null) {
                    Item item = p.getWorld().spawn(p.getLocation(), Item.class);
                    item.setItemStack(event.getView().getTopInventory().getItem(slot));
                    item.setPickupDelay(0);
                    event.getView().getTopInventory().setItem(slot, new ItemStack(Material.AIR));
                }
            }
        }
        if (isThis5x5GUI(event.getView()))
            for (int slot : slots5x5) {
                Player p = (Player) event.getPlayer();
                if (event.getView().getTopInventory().getItem(slot) != null) {
                    Item item = p.getWorld().spawn(p.getLocation(), Item.class);
                    item.setItemStack(event.getView().getTopInventory().getItem(slot));
                    item.setPickupDelay(0);
                    event.getView().getTopInventory().setItem(slot, new ItemStack(Material.AIR));
                }
            }

        if (isThis5x5GUI(event.getView())) {
            for (int slot : slots5x5) {
                Player p = (Player) event.getPlayer();
                if (event.getView().getTopInventory().getItem(slot) != null) {
                    Item item = p.getWorld().spawn(p.getLocation(), Item.class);
                    item.setItemStack(event.getView().getTopInventory().getItem(slot));
                    item.setPickupDelay(0);
                    event.getView().getTopInventory().setItem(slot, new ItemStack(Material.AIR));
                }
            }
        }
    }

    private List<Integer> updateCraftingResult(InventoryView view, Player p) {
        int resultSlot = 25;
        Inventory inventory = view.getTopInventory();
        ItemStack[] grid;
        if (isThis3x3GUI(view)) {
            grid = new ItemStack[]{
                    inventory.getItem(10), inventory.getItem(11), inventory.getItem(12),
                    inventory.getItem(19), inventory.getItem(20), inventory.getItem(21),
                    inventory.getItem(28), inventory.getItem(29), inventory.getItem(30)};
        } else if (isThis5x5GUI(view)) {
            grid = new ItemStack[]{
                    inventory.getItem(0), inventory.getItem(1), inventory.getItem(2), inventory.getItem(3), inventory.getItem(4),
                    inventory.getItem(9), inventory.getItem(10), inventory.getItem(11), inventory.getItem(12), inventory.getItem(13),
                    inventory.getItem(18), inventory.getItem(19), inventory.getItem(20), inventory.getItem(21), inventory.getItem(22),
                    inventory.getItem(27), inventory.getItem(28), inventory.getItem(29), inventory.getItem(30), inventory.getItem(31),
                    inventory.getItem(36), inventory.getItem(37), inventory.getItem(38), inventory.getItem(39), inventory.getItem(40)};
        }
        else throw new IllegalArgumentException("What is this crafting type? (" + view.getTitle() + ")");

        if (isThis3x3GUI(view)) {
            ItemStack results = getServer().craftItem(grid, p.getWorld(), p);
            if (!results.getType().equals(Material.AIR)) {
                isCustom.put(p.getUniqueId(), false);
                inventory.setItem(resultSlot, results);
                results.getItemMeta().getPersistentDataContainer().set(getPDC("id"), PersistentDataType.STRING, results.getType().name().toLowerCase());

                for (int i = 45; i < 54; i++) if (i != 49) inventory.setItem(i, validatorT);
                return new ArrayList<>();
            } else {
                for (Class<?> clazz : getRecipeClasses()) {
                    int match = 0;
                    List<Integer> consumption = new ArrayList<>();
                    List<Pair<String, Integer>> criteria = recipe.get(clazz.getSimpleName());
                    for (int n : slots3x3) {
                        ItemStack i = inventory.getItem(n);
                        String id;
                        if (i == null) id = "null"; else id = i.getItemMeta().getPersistentDataContainer().get(getPDC("id"), PersistentDataType.STRING);
                        int amount;
                        if (i == null) amount = 0; else amount = i.getAmount();

                        int x = correspond3x3(n);
                        String left;
                        int right;
                        try {
                            left = criteria.get(x).left();
                        } catch (NullPointerException ignored) {
                            left = "null";
                        }
                        try {
                            right = criteria.get(x).right();
                        } catch (NullPointerException ignored) {
                            right = 0;
                        }
                        getLogger().info("id: " + id + ", amount: " + amount + ", left: " + left + ", right: " + right);
                        if (left.equals(id) && right >= amount) {
                            match++;
                            consumption.add(right);
                        }
                    }
                    getLogger().info("matched: " + match);
                    if (match == 9) {
                        isCustom.put(p.getUniqueId(), true);
                        inventory.setItem(resultSlot, result.get(clazz.getSimpleName()));
                        return consumption;
                    }
                }
            }
            if (view.getTitle().equals("5x5 Crafting Table")) {
                //TODO: 5x5 crafting
            }

            for (int i = 45; i < 54; i++) if (i != 49) inventory.setItem(i, validatorF);
            inventory.setItem(resultSlot, resultItem);
        }
        return new ArrayList<>();
    }

    private boolean isThis3x3GUI(InventoryView view) {
        return view.getTopInventory().getHolder() == null && view.getTitle().equals("Crafting Table");
    }

    private boolean isThis5x5GUI(InventoryView view) {
        return view.getTopInventory().getHolder() == null && view.getTitle().equals("5x5 Crafting Table");
    }

    private int correspond3x3(int n) {
        int x = -1;
        if (n == 10) x = 0; if (n == 11) x = 1; if (n == 12) x = 2;
        if (n == 19) x = 3; if (n == 20) x = 4; if (n == 21) x = 5;
        if (n == 28) x = 6; if (n == 29) x = 7; if (n == 30) x = 8;
        return x;
    }
}
