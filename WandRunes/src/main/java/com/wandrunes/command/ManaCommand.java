package com.wandrunes.command;

import com.wandrunes.WandRunes;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ManaCommand implements CommandExecutor {
    private final WandRunes plugin;
    public ManaCommand(WandRunes plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = plugin.getConfigManager().getPrefix();
        if (args.length == 0) {
            if (!(sender instanceof Player player)) { sender.sendMessage("Specify a player."); return true; }
            int mana = plugin.getManaManager().getMana(player.getUniqueId());
            int max = plugin.getManaManager().getMaxMana();
            player.sendMessage(prefix + ChatColor.DARK_AQUA + "Mana: " + ChatColor.AQUA + mana + "/" + max);
        } else {
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) { sender.sendMessage(prefix + ChatColor.RED + "Player not found."); return true; }
            int mana = plugin.getManaManager().getMana(target.getUniqueId());
            int max = plugin.getManaManager().getMaxMana();
            sender.sendMessage(prefix + ChatColor.AQUA + target.getName() + "'s Mana: " + mana + "/" + max);
        }
        return true;
    }
}
