package com.wandrunes.command;

import com.wandrunes.WandRunes;
import com.wandrunes.gui.AdminGUI;
import com.wandrunes.wand.WandType;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class WandAdminCommand implements CommandExecutor {
    private final WandRunes plugin;
    public WandAdminCommand(WandRunes plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Players only."); return true; }
        if (!player.hasPermission("wandrunes.admin")) { player.sendMessage(plugin.getConfigManager().getNoPermission()); return true; }

        if (args.length >= 3 && args[0].equalsIgnoreCase("givewand")) {
            Player target = plugin.getServer().getPlayer(args[1]);
            if (target == null) { player.sendMessage(ChatColor.RED + "Player not found."); return true; }
            WandType type = WandType.fromId(args[2].toLowerCase());
            if (type == null) { player.sendMessage(ChatColor.RED + "Unknown wand: " + args[2]); return true; }
            target.getInventory().addItem(plugin.getWandRegistry().createWand(type, 1));
            player.sendMessage(plugin.getConfigManager().getPrefix() + ChatColor.GREEN + "Gave " + type.getDisplayName() + " to " + target.getName());
            return true;
        }

        AdminGUI.openMain(plugin, player);
        return true;
    }
}
