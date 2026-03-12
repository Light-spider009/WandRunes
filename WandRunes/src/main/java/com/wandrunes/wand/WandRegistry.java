package com.wandrunes.wand;

import com.wandrunes.WandRunes;
import com.wandrunes.rune.RuneType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WandRegistry {

    private final WandRunes plugin;
    public static final String WAND_KEY = "wandrunes_wand_id";
    public static final String WAND_LEVEL_KEY = "wandrunes_wand_level";
    public static final String WAND_XP_KEY = "wandrunes_wand_xp";
    public static final String WAND_BAG_KEY = "wandrunes_wand_bag";
    public static final String RUNE_KEY = "wandrunes_rune_id";

    public static final String MAT_ARCANE_DUST = "arcane_dust";
    public static final String MAT_SOUL_ESSENCE = "soul_essence";
    public static final String MAT_MANA_CRYSTAL = "mana_crystal";
    public static final String MAT_VOID_FRAGMENT = "void_fragment";
    public static final String MAT_CHRONO_SHARD = "chrono_shard";
    public static final String MAT_STORM_CORE = "storm_core";
    public static final String MAT_FROST_HEART = "frost_heart";
    public static final String MAT_EMBER_CORE = "ember_core";
    public static final String CUSTOM_MAT_KEY = "wandrunes_material_id";

    public WandRegistry(WandRunes plugin) {
        this.plugin = plugin;
    }

    private void applyGlow(ItemMeta meta) {
        // Use DURABILITY as a safe glowing enchant across all 1.20 builds
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    public ItemStack createWand(WandType type, int level) {
        ItemStack item = new ItemStack(type.getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(type.getDisplayName() + ChatColor.GRAY + " [Lv." + level + "]");

        int mana = plugin.getConfigManager().getWandMana(type.getId(), type.getBaseMana());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻⸻");
        lore.add(ChatColor.WHITE + "Spell: " + type.getColor() + type.getDescription());
        lore.add(ChatColor.WHITE + "Mana Cost: " + ChatColor.DARK_AQUA + mana);
        lore.add(ChatColor.WHITE + "Level: " + ChatColor.GOLD + level + ChatColor.GRAY + "/" + type.getMaxLevel());
        lore.add(ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻⸻");
        lore.add(ChatColor.YELLOW + "▶ Right-click to cast");
        lore.add(ChatColor.YELLOW + "▶ Hold right-click to charge (2x power)");
        lore.add(ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻⸻");
        meta.setLore(lore);
        applyGlow(meta);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        NamespacedKey wandKey = new NamespacedKey(plugin, WAND_KEY);
        NamespacedKey levelKey = new NamespacedKey(plugin, WAND_LEVEL_KEY);
        NamespacedKey xpKey = new NamespacedKey(plugin, WAND_XP_KEY);
        meta.getPersistentDataContainer().set(wandKey, PersistentDataType.STRING, type.getId());
        meta.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
        meta.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, 0);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createRuneItem(RuneType type) {
        ItemStack item = new ItemStack(type.getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(type.getDisplayName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻⸻");
        lore.add(ChatColor.WHITE + "Effect: " + type.getColor() + type.getDescription());
        lore.add(ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻⸻");
        lore.add(ChatColor.YELLOW + "▶ Place on ground to activate");
        lore.add(ChatColor.GRAY + "⸻⸻⸻⸻⸻⸻⸻");
        meta.setLore(lore);
        applyGlow(meta);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        NamespacedKey key = new NamespacedKey(plugin, RUNE_KEY);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, type.getId());
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createMaterial(String matId, String name, Material mat, String description) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "✦ " + name);
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + description,
            ChatColor.DARK_PURPLE + "✨ WandRunes Material"
        ));
        applyGlow(meta);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        NamespacedKey key = new NamespacedKey(plugin, CUSTOM_MAT_KEY);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, matId);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createWandBag() {
        ItemStack item = new ItemStack(Material.BUNDLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "✦ Wand Bag");
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + "Stores all your wands in one slot",
            ChatColor.YELLOW + "▶ Right-click to open",
            ChatColor.DARK_PURPLE + "✨ WandRunes Item"
        ));
        applyGlow(meta);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        NamespacedKey key = new NamespacedKey(plugin, WAND_BAG_KEY);
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);
        return item;
    }

    public String getWandId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        NamespacedKey key = new NamespacedKey(plugin, WAND_KEY);
        var pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(key, PersistentDataType.STRING) ? pdc.get(key, PersistentDataType.STRING) : null;
    }

    public String getRuneId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        NamespacedKey key = new NamespacedKey(plugin, RUNE_KEY);
        var pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(key, PersistentDataType.STRING) ? pdc.get(key, PersistentDataType.STRING) : null;
    }

    public String getMaterialId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        NamespacedKey key = new NamespacedKey(plugin, CUSTOM_MAT_KEY);
        var pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(key, PersistentDataType.STRING) ? pdc.get(key, PersistentDataType.STRING) : null;
    }

    public boolean isWand(ItemStack item) { return getWandId(item) != null; }
    public boolean isRune(ItemStack item) { return getRuneId(item) != null; }

    public boolean isWandBag(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        NamespacedKey key = new NamespacedKey(plugin, WAND_BAG_KEY);
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER);
    }

    public int getWandLevel(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 1;
        NamespacedKey key = new NamespacedKey(plugin, WAND_LEVEL_KEY);
        var pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(key, PersistentDataType.INTEGER) ? pdc.get(key, PersistentDataType.INTEGER) : 1;
    }

    public int getWandXP(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        NamespacedKey key = new NamespacedKey(plugin, WAND_XP_KEY);
        var pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(key, PersistentDataType.INTEGER) ? pdc.get(key, PersistentDataType.INTEGER) : 0;
    }
}
