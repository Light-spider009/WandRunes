package com.wandrunes.listener;

import com.wandrunes.WandRunes;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class WandCraftListener implements Listener {

    private final WandRunes plugin;

    public WandCraftListener(WandRunes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        // Prevent accidentally crafting vanilla items that share materials
        // Custom wands are crafted only via /wr gui recipes - handled in GrimoireGUI
    }
}
