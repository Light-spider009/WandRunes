package com.wandrunes.gui;

import com.wandrunes.WandRunes;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class WandBagGUI {

    public static void open(WandRunes plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, ChatColor.DARK_PURPLE + "✦ Wand Bag");

        ItemStack filler = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta(); fm.setDisplayName(" "); filler.setItemMeta(fm);
        for (int i = 27; i < 36; i++) inv.setItem(i, filler);

        // Copy wands from player inventory into bag view
        int slot = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (plugin.getWandRegistry().isWand(item) && slot < 27) {
                inv.setItem(slot++, item.clone());
            }
        }

        // Info item
        inv.setItem(31, makeItem(Material.BOOK, ChatColor.YELLOW + "Wand Bag",
            List.of(ChatColor.GRAY + "Click a wand to equip it",
                ChatColor.GRAY + "to your main hand")));

        player.openInventory(inv);
    }

    public static void handleClick(WandRunes plugin, Player player, InventoryClickEvent event) {
        event.setCancelled(true);
        int slot = event.getRawSlot();
        if (slot >= 27) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !plugin.getWandRegistry().isWand(clicked)) return;
        player.getInventory().setItemInMainHand(clicked);
        player.sendMessage(plugin.getConfigManager().getPrefix()
            + ChatColor.GREEN + "Equipped: " + clicked.getItemMeta().getDisplayName());
        player.closeInventory();
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
