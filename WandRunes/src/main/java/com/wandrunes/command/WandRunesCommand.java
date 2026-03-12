package com.wandrunes.command;

import com.wandrunes.WandRunes;
import com.wandrunes.gui.GrimoireGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class WandRunesCommand implements CommandExecutor {
    private final WandRunes plugin;
    public WandRunesCommand(WandRunes plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Players only."); return true; }
        if (!player.hasPermission("wandrunes.use")) { player.sendMessage(plugin.getConfigManager().getNoPermission()); return true; }
        GrimoireGUI.openPage1(plugin, player);
        return true;
    }
}
