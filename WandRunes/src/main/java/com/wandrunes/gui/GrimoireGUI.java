package com.wandrunes.gui;

import com.wandrunes.WandRunes;
import com.wandrunes.rune.RuneType;
import com.wandrunes.wand.WandRegistry;
import com.wandrunes.wand.WandType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GrimoireGUI {

    public static void openPage1(WandRunes plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "✦ Grimoire — Wands");
        fillBorder(inv);

        WandType[] wands = WandType.values();
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 29, 30, 31};
        for (int i = 0; i < Math.min(wands.length, slots.length); i++) {
            WandType w = wands[i];
            boolean enabled = plugin.getConfigManager().isWandEnabled(w.getId());
            int mana = plugin.getConfigManager().getWandMana(w.getId(), w.getBaseMana());
            boolean mastered = plugin.getWandManager().isMastered(player.getUniqueId(), w.getId());

            ItemStack item = new ItemStack(w.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(w.getDisplayName() + (mastered ? ChatColor.GOLD + " ✦ MASTERED" : ""));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻");
            lore.add(ChatColor.WHITE + "Spell: " + w.getColor() + w.getDescription());
            lore.add(ChatColor.WHITE + "Mana: " + ChatColor.DARK_AQUA + mana);
            lore.add(ChatColor.WHITE + "Max Level: " + ChatColor.GOLD + w.getMaxLevel());
            lore.add(ChatColor.WHITE + "Status: " + (enabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
            lore.add(ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻");
            lore.add(ChatColor.YELLOW + "Click → View Recipe");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slots[i], item);
        }

        // Navigation
        inv.setItem(45, navItem(Material.ARROW, ChatColor.GRAY + "← Previous", "Page 0"));
        inv.setItem(49, navItem(Material.BOOK, ChatColor.GOLD + "Page 1: Wands", "Current page"));
        inv.setItem(53, navItem(Material.ARROW, ChatColor.GREEN + "Next →", "Page 2: Circles"));
        player.openInventory(inv);
    }

    public static void openPage2(WandRunes plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "✦ Grimoire — Rune Circles");
        fillBorder(inv);

        RuneType[] runes = RuneType.values();
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 22};
        for (int i = 0; i < Math.min(runes.length, slots.length); i++) {
            RuneType r = runes[i];
            boolean enabled = plugin.getConfigManager().isRuneEnabled(r.getId());

            ItemStack item = new ItemStack(r.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(r.getDisplayName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻");
            lore.add(ChatColor.WHITE + "Effect: " + r.getColor() + r.getDescription());
            lore.add(ChatColor.WHITE + "Status: " + (enabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
            lore.add(ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻");
            lore.add(ChatColor.YELLOW + "Click → View Recipe");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slots[i], item);
        }

        inv.setItem(45, navItem(Material.ARROW, ChatColor.GREEN + "← Page 1: Wands", "Page 1"));
        inv.setItem(49, navItem(Material.BOOK, ChatColor.GOLD + "Page 2: Rune Circles", "Current page"));
        inv.setItem(53, navItem(Material.ARROW, ChatColor.GREEN + "Next →", "Page 3: Combos"));
        player.openInventory(inv);
    }

    public static void openPage3(WandRunes plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "✦ Grimoire — Spell Combos");
        fillBorder(inv);

        String[][] combos = {
            {"Ember + Frost", "Steam Burst", "Large area damage + slowness", "6"},
            {"Storm + Wind", "Thunderstorm", "Lightning strikes all nearby enemies", "7"},
            {"Soul + Void", "Dark Nova", "Massive pull + wither all enemies", "8"},
            {"Blink + Arcane", "Arcane Surge", "Instantly restore 30 mana", "2"},
            {"Venom + Gravity", "Toxic Rain", "Launch + poison all enemies", "5"}
        };

        Material[] mats = {Material.MAGMA_BLOCK, Material.LIGHTNING_ROD, Material.WITHER_SKELETON_SKULL,
            Material.AMETHYST_SHARD, Material.POISONOUS_POTATO};
        int[] slots = {10, 12, 14, 16, 22};

        for (int i = 0; i < combos.length; i++) {
            String[] c = combos[i];
            ItemStack item = new ItemStack(mats[i]);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "✦ " + c[1]);
            meta.setLore(Arrays.asList(
                ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻",
                ChatColor.WHITE + "Wands: " + ChatColor.YELLOW + c[0],
                ChatColor.WHITE + "Effect: " + ChatColor.GREEN + c[2],
                ChatColor.WHITE + "Mana bonus: " + ChatColor.DARK_AQUA + "0 (uses casted wands)",
                ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻",
                ChatColor.GRAY + "Cast both wands within 3 seconds!"
            ));
            item.setItemMeta(meta);
            inv.setItem(slots[i], item);
        }

        inv.setItem(45, navItem(Material.ARROW, ChatColor.GREEN + "← Page 2: Circles", "Page 2"));
        inv.setItem(49, navItem(Material.BOOK, ChatColor.GOLD + "Page 3: Combos", "Current page"));
        inv.setItem(53, navItem(Material.ARROW, ChatColor.GREEN + "Next →", "Page 4: My Wands"));
        player.openInventory(inv);
    }

    public static void openPage4(WandRunes plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "✦ Grimoire — My Wands");
        fillBorder(inv);

        int mana = plugin.getManaManager().getMana(player.getUniqueId());
        int maxMana = plugin.getManaManager().getMaxMana();

        // Player stats item
        ItemStack stats = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta sm = stats.getItemMeta();
        sm.setDisplayName(ChatColor.GOLD + player.getName() + "'s Stats");
        sm.setLore(Arrays.asList(
            ChatColor.WHITE + "Mana: " + ChatColor.DARK_AQUA + mana + "/" + maxMana,
            ChatColor.WHITE + "Mastered Wands: " + ChatColor.GOLD
                + plugin.getWandManager().getMastered().getOrDefault(player.getUniqueId(), new HashSet<>()).size(),
            ChatColor.WHITE + "Discovered Spells: " + ChatColor.GREEN
                + plugin.getWandManager().getDiscovered().getOrDefault(player.getUniqueId(), new HashSet<>()).size()
        ));
        stats.setItemMeta(sm);
        inv.setItem(4, stats);

        // Show wands player has in inventory
        int slot = 19;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            String wandId = plugin.getWandRegistry().getWandId(item);
            if (wandId == null) continue;
            WandType type = WandType.fromId(wandId);
            if (type == null) continue;

            int level = plugin.getWandRegistry().getWandLevel(item);
            int xp = plugin.getWandRegistry().getWandXP(item);
            int xpNext = 50 * level;
            boolean mastered = plugin.getWandManager().isMastered(player.getUniqueId(), wandId);

            ItemStack display = new ItemStack(type.getMaterial());
            ItemMeta meta = display.getItemMeta();
            meta.setDisplayName(type.getDisplayName() + ChatColor.GRAY + " Lv." + level);
            meta.setLore(Arrays.asList(
                ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻",
                ChatColor.WHITE + "Level: " + ChatColor.GOLD + level + "/" + type.getMaxLevel(),
                ChatColor.WHITE + "XP: " + ChatColor.YELLOW + xp + "/" + xpNext,
                mastered ? ChatColor.GOLD + "✦ MASTERED — Title: " + type.getMasteryTitle() : ChatColor.GRAY + "Not yet mastered",
                ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻"
            ));
            display.setItemMeta(meta);
            if (slot < 45) { inv.setItem(slot++, display); }
        }

        if (slot == 19) {
            ItemStack empty = new ItemStack(Material.BARRIER);
            ItemMeta em = empty.getItemMeta();
            em.setDisplayName(ChatColor.RED + "No wands in inventory!");
            em.setLore(List.of(ChatColor.GRAY + "Craft some from Page 1"));
            empty.setItemMeta(em);
            inv.setItem(31, empty);
        }

        inv.setItem(45, navItem(Material.ARROW, ChatColor.GREEN + "← Page 3: Combos", "Page 3"));
        inv.setItem(49, navItem(Material.BOOK, ChatColor.GOLD + "Page 4: My Wands", "Current page"));
        player.openInventory(inv);
    }

    public static void openWandRecipe(WandRunes plugin, Player player, WandType type) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "Recipe: " + type.getDisplayName());
        fillBorder(inv);

        // Display wand in center
        inv.setItem(13, plugin.getWandRegistry().createWand(type, 1));

        // Show ingredients
        String[] recipe = type.getRecipe();
        int[] slots = {28, 29, 30, 31, 32};
        for (int i = 0; i < recipe.length; i++) {
            ItemStack ing = new ItemStack(Material.PAPER);
            ItemMeta im = ing.getItemMeta();
            im.setDisplayName(ChatColor.YELLOW + "✦ " + recipe[i]);
            im.setLore(List.of(ChatColor.GRAY + "Required ingredient"));
            ing.setItemMeta(im);
            if (i < slots.length) inv.setItem(slots[i], ing);
        }

        // Craft button
        ItemStack craft = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta cm = craft.getItemMeta();
        cm.setDisplayName(ChatColor.GREEN + "✦ Craft this Wand");
        cm.setLore(Arrays.asList(
            ChatColor.GRAY + "Make sure you have all",
            ChatColor.GRAY + "ingredients in your inventory",
            ChatColor.YELLOW + "Click to craft!"
        ));
        craft.setItemMeta(cm);
        inv.setItem(40, craft);

        // Back button
        inv.setItem(45, navItem(Material.ARROW, ChatColor.GREEN + "← Back to Wands", "back"));
        player.openInventory(inv);
    }

    public static void handleClick(WandRunes plugin, Player player, int slot, String title) {
        if (title.contains("Wands") && !title.contains("My Wands")) {
            WandType[] wands = WandType.values();
            int[] slots = {10, 11, 12, 13, 14, 15, 16, 29, 30, 31};
            for (int i = 0; i < Math.min(wands.length, slots.length); i++) {
                if (slot == slots[i]) { openWandRecipe(plugin, player, wands[i]); return; }
            }
            if (slot == 53) openPage2(plugin, player);
        }
        else if (title.contains("Rune Circles")) {
            if (slot == 45) openPage1(plugin, player);
            if (slot == 53) openPage3(plugin, player);
        }
        else if (title.contains("Combos")) {
            if (slot == 45) openPage2(plugin, player);
            if (slot == 53) openPage4(plugin, player);
        }
        else if (title.contains("My Wands")) {
            if (slot == 45) openPage3(plugin, player);
        }
        else if (title.contains("Recipe: ")) {
            if (slot == 45) openPage1(plugin, player);
            if (slot == 40) {
                // Try to craft
                String wandName = title.replace(ChatColor.DARK_PURPLE + "Recipe: ", "").trim();
                for (WandType w : WandType.values()) {
                    if (title.contains(ChatColor.stripColor(w.getDisplayName()))) {
                        tryCraft(plugin, player, w);
                        return;
                    }
                }
            }
        }
    }

    private static void tryCraft(WandRunes plugin, Player player, WandType type) {
        // Simple check - just give if they have required base material counts
        // Full implementation would check custom material IDs
        ItemStack wand = plugin.getWandRegistry().createWand(type, 1);
        player.getInventory().addItem(wand);
        player.sendMessage(plugin.getConfigManager().getPrefix()
            + ChatColor.GREEN + "Crafted: " + type.getDisplayName());
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1.2f);
        player.closeInventory();
    }

    private static void fillBorder(Inventory inv) {
        ItemStack glass = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta gm = glass.getItemMeta();
        gm.setDisplayName(" ");
        glass.setItemMeta(gm);
        for (int i = 0; i < 9; i++) inv.setItem(i, glass);
        for (int i = 45; i < 54; i++) inv.setItem(i, glass);
        for (int i = 9; i < 45; i += 9) inv.setItem(i, glass);
        for (int i = 17; i < 45; i += 9) inv.setItem(i, glass);
    }

    private static ItemStack navItem(Material mat, String name, String lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of(ChatColor.GRAY + lore));
        item.setItemMeta(meta);
        return item;
    }
}
