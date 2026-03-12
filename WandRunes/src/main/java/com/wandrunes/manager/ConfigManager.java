package com.wandrunes.manager;

import com.wandrunes.WandRunes;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

public class ConfigManager {

    private final WandRunes plugin;
    private FileConfiguration config;

    public ConfigManager(WandRunes plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    // Mana
    public int getMaxMana() { return config.getInt("mana.max-mana", 100); }
    public int getRegenAmount() { return config.getInt("mana.regen-amount", 1); }
    public int getRegenInterval() { return config.getInt("mana.regen-interval-seconds", 4); }
    public int getMobKillBonus() { return config.getInt("mana.mob-kill-bonus", 5); }
    public int getPlayerKillBonus() { return config.getInt("mana.player-kill-bonus", 15); }
    public int getBossKillBonus() { return config.getInt("mana.boss-kill-bonus", 30); }

    // Wands
    public int getOverheatLimit() { return config.getInt("wands.overheat-cast-limit", 5); }
    public int getOverheatCooldown() { return config.getInt("wands.overheat-cooldown-seconds", 10); }
    public int getChargeTicks() { return config.getInt("wands.charge-time-ticks", 40); }
    public double getChargedMultiplier() { return config.getDouble("wands.charged-power-multiplier", 2.0); }

    public boolean isWandEnabled(String wand) { return config.getBoolean("wands." + wand, true); }
    public String getWandPower(String wand) { return config.getString("wands." + wand + "-power", "NORMAL"); }
    public int getWandMana(String wand, int def) { return config.getInt("wands." + wand + "-mana", def); }
    public List<String> getWandBlacklist(String wand) {
        String raw = config.getString("wands." + wand + "-blacklist", "");
        if (raw == null || raw.isBlank()) return List.of();
        return Arrays.asList(raw.split(","));
    }

    // Runes
    public boolean isRuneEnabled(String rune) { return config.getBoolean("runes." + rune, true); }
    public String getRunePower(String rune) { return config.getString("runes." + rune + "-power", "NORMAL"); }

    // Drops
    public double getSoulEssenceChance() { return config.getDouble("drops.soul-essence-chance", 0.4); }
    public double getVoidFragmentChance() { return config.getDouble("drops.void-fragment-chance", 0.15); }
    public double getChronoShardChance() { return config.getDouble("drops.chrono-shard-chance", 0.20); }
    public double getStormCoreChance() { return config.getDouble("drops.storm-core-chance", 1.0); }
    public double getFrostHeartChance() { return config.getDouble("drops.frost-heart-chance", 0.5); }
    public double getEmberCoreChance() { return config.getDouble("drops.ember-core-chance", 0.6); }

    // Messages
    public String getPrefix() { return c(config.getString("messages.prefix", "&5[&dWandRunes&5] &r")); }
    public String getNoPermission() { return getPrefix() + c("&cNo permission!"); }
    public String getNotEnoughMana(int needed, int current) {
        return getPrefix() + c(config.getString("messages.not-enough-mana", "&cNot enough mana!")
            .replace("{needed}", String.valueOf(needed))
            .replace("{current}", String.valueOf(current)));
    }
    public String getWandDisabled() { return getPrefix() + c(config.getString("messages.wand-disabled", "&cThis wand is disabled.")); }
    public String getWandBlacklisted() { return getPrefix() + c(config.getString("messages.wand-blacklisted", "&cCannot use here.")); }
    public String getOverheatMsg(int seconds) {
        return getPrefix() + c(config.getString("messages.overheat", "&cOverheating! Cooldown: {seconds}s")
            .replace("{seconds}", String.valueOf(seconds)));
    }
    public String getSpellDiscovered(String spell) {
        return getPrefix() + c(config.getString("messages.spell-discovered", "&dDiscovered: &5{spell}!")
            .replace("{spell}", spell));
    }
    public String getWandLevelUp(String wand, int level) {
        return getPrefix() + c(config.getString("messages.wand-levelup", "&d{wand} leveled up to &5Level {level}!")
            .replace("{wand}", wand).replace("{level}", String.valueOf(level)));
    }
    public String getWandMastered(String wand, String title) {
        return getPrefix() + c(config.getString("messages.wand-mastered", "&6✦ Mastered {wand}! Title: &e{title}")
            .replace("{wand}", wand).replace("{title}", title));
    }

    public static String c(String text) {
        return text.replace("&", "\u00a7");
    }
}
