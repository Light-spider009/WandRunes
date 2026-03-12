package com.wandrunes.listener;

import com.wandrunes.WandRunes;
import com.wandrunes.gui.WandBagGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class WandBagListener implements Listener {

    private final WandRunes plugin;

    public WandBagListener(WandRunes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWandBagOpen(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!plugin.getWandRegistry().isWandBag(item)) return;
        if (plugin.getWandRegistry().isWand(item)) return;
        event.setCancelled(true);
        WandBagGUI.open(plugin, player);
    }

    @EventHandler
    public void onWandBagClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTitle().contains("Wand Bag")) {
            WandBagGUI.handleClick(plugin, player, event);
        }
    }
}
