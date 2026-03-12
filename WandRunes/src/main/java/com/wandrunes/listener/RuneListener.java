package com.wandrunes.listener;

import com.wandrunes.WandRunes;
import com.wandrunes.rune.RuneType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class RuneListener implements Listener {

    private final WandRunes plugin;

    public RuneListener(WandRunes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRunePlace(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        String runeId = plugin.getWandRegistry().getRuneId(item);
        if (runeId == null) return;

        RuneType type = RuneType.fromId(runeId);
        if (type == null) return;
        if (!plugin.getConfigManager().isRuneEnabled(type.getId())) {
            player.sendMessage(plugin.getConfigManager().getPrefix()
                + ChatColor.RED + "This rune circle is disabled on this server.");
            return;
        }

        event.setCancelled(true);
        Location loc = event.getClickedBlock().getLocation().add(0, 1, 0);

        plugin.getRuneManager().placeRune(loc, type);
        item.setAmount(item.getAmount() - 1);

        player.sendMessage(plugin.getConfigManager().getPrefix()
            + type.getColor() + type.getDisplayName()
            + ChatColor.GREEN + " placed! Right-click a wand on it to bind.");
        player.getWorld().playSound(loc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 0.8f);
    }
}
