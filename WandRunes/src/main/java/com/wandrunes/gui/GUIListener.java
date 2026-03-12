package com.wandrunes.gui;

import com.wandrunes.WandRunes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GUIListener implements Listener {

    private final WandRunes plugin;

    public GUIListener(WandRunes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();

        if (title.contains("Grimoire") || title.contains("Recipe: ")) {
            event.setCancelled(true);
            GrimoireGUI.handleClick(plugin, player, event.getRawSlot(), title);
        }
        else if (title.contains("Admin Panel") || title.contains("Wand Manager")
            || title.contains("Rune Manager") || title.contains("Give Items")) {
            AdminGUI.handleClick(plugin, player, event);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title.contains("Grimoire") || title.contains("Admin") || title.contains("Recipe: ")) {
            event.setCancelled(true);
        }
    }
}
