package com.wandrunes.gui;

import com.wandrunes.WandRunes;
import com.wandrunes.rune.RuneType;
import com.wandrunes.wand.WandType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class AdminGUI {

    public static void openMain(WandRunes plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_RED + "⚙ WandRunes Admin Panel");
        fillBorder(inv, Material.RED_STAINED_GLASS_PANE);

        inv.setItem(10, makeItem(Material.BLAZE_ROD, ChatColor.GOLD + "Manage Wands",
            List.of(ChatColor.GRAY + "Enable/disable wands",
                ChatColor.GRAY + "Set power levels")));

        inv.setItem(12, makeItem(Material.LODESTONE, ChatColor.AQUA + "Manage Rune Circles",
            List.of(ChatColor.GRAY + "Enable/disable runes",
                ChatColor.GRAY + "Set power levels")));

        inv.setItem(14, makeItem(Material.AMETHYST_SHARD, ChatColor.LIGHT_PURPLE + "Give Items",
            List.of(ChatColor.GRAY + "Give wands/runes/materials",
                ChatColor.GRAY + "to players")));

        inv.setItem(16, makeItem(Material.COMPARATOR, ChatColor.YELLOW + "Reload Config",
            List.of(ChatColor.GRAY + "Reload config.yml",
                ChatColor.GRAY + "without restart")));

        player.openInventory(inv);
    }

    public static void openWandManager(WandRunes plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_RED + "⚙ Wand Manager");
        fillBorder(inv, Material.RED_STAINED_GLASS_PANE);

        WandType[] wands = WandType.values();
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 29, 30, 31};

        for (int i = 0; i < Math.min(wands.length, slots.length); i++) {
            WandType w = wands[i];
            boolean enabled = plugin.getConfigManager().isWandEnabled(w.getId());
            String power = plugin.getConfigManager().getWandPower(w.getId());

            ItemStack item = new ItemStack(w.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(w.getDisplayName());
            meta.setLore(Arrays.asList(
                ChatColor.WHITE + "Status: " + (enabled ? ChatColor.GREEN + "✔ Enabled" : ChatColor.RED + "✘ Disabled"),
                ChatColor.WHITE + "Power: " + getPowerColor(power) + power,
                ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻",
                ChatColor.YELLOW + "Left-click → Toggle on/off",
                ChatColor.YELLOW + "Right-click → Cycle power level"
            ));
            item.setItemMeta(meta);
            inv.setItem(slots[i], item);
        }

        inv.setItem(49, makeItem(Material.ARROW, ChatColor.RED + "← Back", List.of()));
        player.openInventory(inv);
    }

    public static void openRuneManager(WandRunes plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_RED + "⚙ Rune Manager");
        fillBorder(inv, Material.RED_STAINED_GLASS_PANE);

        RuneType[] runes = RuneType.values();
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 22};

        for (int i = 0; i < Math.min(runes.length, slots.length); i++) {
            RuneType r = runes[i];
            boolean enabled = plugin.getConfigManager().isRuneEnabled(r.getId());
            String power = plugin.getConfigManager().getRunePower(r.getId());

            ItemStack item = new ItemStack(r.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(r.getDisplayName());
            meta.setLore(Arrays.asList(
                ChatColor.WHITE + "Status: " + (enabled ? ChatColor.GREEN + "✔ Enabled" : ChatColor.RED + "✘ Disabled"),
                ChatColor.WHITE + "Power: " + getPowerColor(power) + power,
                ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻",
                ChatColor.YELLOW + "Left-click → Toggle on/off",
                ChatColor.YELLOW + "Right-click → Cycle power level"
            ));
            item.setItemMeta(meta);
            inv.setItem(slots[i], item);
        }

        inv.setItem(49, makeItem(Material.ARROW, ChatColor.RED + "← Back", List.of()));
        player.openInventory(inv);
    }

    public static void handleClick(WandRunes plugin, Player player, InventoryClickEvent event) {
        event.setCancelled(true);
        String title = event.getView().getTitle();
        int slot = event.getRawSlot();

        if (title.contains("Admin Panel")) {
            if (slot == 10) openWandManager(plugin, player);
            else if (slot == 12) openRuneManager(plugin, player);
            else if (slot == 14) openGiveItems(plugin, player);
            else if (slot == 16) {
                plugin.getConfigManager().reload();
                player.sendMessage(plugin.getConfigManager().getPrefix() + ChatColor.GREEN + "Config reloaded!");
            }
        }
        else if (title.contains("Wand Manager")) {
            WandType[] wands = WandType.values();
            int[] slots = {10, 11, 12, 13, 14, 15, 16, 29, 30, 31};
            for (int i = 0; i < Math.min(wands.length, slots.length); i++) {
                if (slot == slots[i]) {
                    WandType w = wands[i];
                    if (event.isLeftClick()) {
                        boolean current = plugin.getConfigManager().isWandEnabled(w.getId());
                        plugin.getConfig().set("wands." + w.getId(), !current);
                        plugin.saveConfig();
                        plugin.getConfigManager().reload();
                        player.sendMessage(plugin.getConfigManager().getPrefix()
                            + ChatColor.YELLOW + w.getDisplayName() + " is now "
                            + (!current ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                        openWandManager(plugin, player);
                    } else if (event.isRightClick()) {
                        String current = plugin.getConfigManager().getWandPower(w.getId());
                        String next = current.equals("NORMAL") ? "STRONG" : current.equals("STRONG") ? "WEAK" : "NORMAL";
                        plugin.getConfig().set("wands." + w.getId() + "-power", next);
                        plugin.saveConfig();
                        plugin.getConfigManager().reload();
                        player.sendMessage(plugin.getConfigManager().getPrefix()
                            + ChatColor.YELLOW + w.getDisplayName() + " power set to " + getPowerColor(next) + next);
                        openWandManager(plugin, player);
                    }
                    return;
                }
            }
            if (slot == 49) openMain(plugin, player);
        }
        else if (title.contains("Rune Manager")) {
            RuneType[] runes = RuneType.values();
            int[] slots = {10, 11, 12, 13, 14, 15, 16, 22};
            for (int i = 0; i < Math.min(runes.length, slots.length); i++) {
                if (slot == slots[i]) {
                    RuneType r = runes[i];
                    if (event.isLeftClick()) {
                        boolean current = plugin.getConfigManager().isRuneEnabled(r.getId());
                        plugin.getConfig().set("runes." + r.getId(), !current);
                        plugin.saveConfig();
                        plugin.getConfigManager().reload();
                        player.sendMessage(plugin.getConfigManager().getPrefix()
                            + ChatColor.YELLOW + r.getDisplayName() + " is now "
                            + (!current ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                        openRuneManager(plugin, player);
                    } else if (event.isRightClick()) {
                        String current = plugin.getConfigManager().getRunePower(r.getId());
                        String next = current.equals("NORMAL") ? "STRONG" : current.equals("STRONG") ? "WEAK" : "NORMAL";
                        plugin.getConfig().set("runes." + r.getId() + "-power", next);
                        plugin.saveConfig();
                        plugin.getConfigManager().reload();
                        player.sendMessage(plugin.getConfigManager().getPrefix()
                            + ChatColor.YELLOW + r.getDisplayName() + " power set to " + getPowerColor(next) + next);
                        openRuneManager(plugin, player);
                    }
                    return;
                }
            }
            if (slot == 49) openMain(plugin, player);
        }
        else if (title.contains("Give Items")) {
            if (slot == 49) openMain(plugin, player);
        }
    }

    private static void openGiveItems(WandRunes plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_RED + "⚙ Give Items");
        fillBorder(inv, Material.RED_STAINED_GLASS_PANE);

        int slot = 10;
        for (WandType w : WandType.values()) {
            if (slot >= 17) break;
            inv.setItem(slot++, makeItem(w.getMaterial(), ChatColor.GOLD + "Give: " + w.getDisplayName(),
                List.of(ChatColor.GRAY + "Click to give yourself this wand")));
        }

        inv.setItem(49, makeItem(Material.ARROW, ChatColor.RED + "← Back", List.of()));
        player.openInventory(inv);
    }

    private static ChatColor getPowerColor(String power) {
        return switch (power) {
            case "STRONG" -> ChatColor.RED;
            case "WEAK" -> ChatColor.GRAY;
            default -> ChatColor.YELLOW;
        };
    }

    private static void fillBorder(Inventory inv, Material mat) {
        ItemStack glass = new ItemStack(mat);
        ItemMeta gm = glass.getItemMeta(); gm.setDisplayName(" "); glass.setItemMeta(gm);
        int size = inv.getSize();
        for (int i = 0; i < 9; i++) inv.setItem(i, glass);
        for (int i = size - 9; i < size; i++) inv.setItem(i, glass);
        for (int i = 9; i < size - 9; i += 9) inv.setItem(i, glass);
        for (int i = 17; i < size - 9; i += 9) inv.setItem(i, glass);
    }

    private static ItemStack makeItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
