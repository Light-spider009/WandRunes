package com.wandrunes.listener;

import com.wandrunes.WandRunes;
import com.wandrunes.rune.RuneType;
import com.wandrunes.wand.WandRegistry;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class ManaListener implements Listener {

    private final WandRunes plugin;

    public ManaListener(WandRunes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        int bonus;
        if (plugin.getDropManager().isBoss(event.getEntity())) {
            bonus = plugin.getConfigManager().getBossKillBonus();
        } else if (event.getEntity() instanceof Player) {
            bonus = plugin.getConfigManager().getPlayerKillBonus();
        } else {
            bonus = plugin.getConfigManager().getMobKillBonus();
        }

        plugin.getManaManager().addMana(killer.getUniqueId(), bonus);
        killer.sendMessage(plugin.getConfigManager().getPrefix()
            + ChatColor.DARK_AQUA + "+" + bonus + " mana");

        // Drop custom materials
        ItemStack drop = plugin.getDropManager().getDropForEntity(event.getEntity());
        if (drop != null) {
            event.getDrops().add(drop);
        }
    }
}
