package com.wandrunes.manager;

import com.wandrunes.WandRunes;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ManaManager {

    private final WandRunes plugin;
    private final Map<UUID, Integer> manaMap = new HashMap<>();
    private final File dataFile;
    private FileConfiguration dataConfig;

    public ManaManager(WandRunes plugin) {
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "mana.yml");
        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            try { dataFile.getParentFile().mkdirs(); dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        if (dataConfig.contains("mana")) {
            for (String key : dataConfig.getConfigurationSection("mana").getKeys(false)) {
                manaMap.put(UUID.fromString(key), dataConfig.getInt("mana." + key));
            }
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, Integer> entry : manaMap.entrySet()) {
            dataConfig.set("mana." + entry.getKey(), entry.getValue());
        }
        try { dataConfig.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public int getMana(UUID uuid) {
        return manaMap.getOrDefault(uuid, plugin.getConfigManager().getMaxMana());
    }

    public int getMaxMana() {
        return plugin.getConfigManager().getMaxMana();
    }

    public boolean consumeMana(UUID uuid, int amount) {
        int current = getMana(uuid);
        if (current < amount) return false;
        manaMap.put(uuid, current - amount);
        refreshBar(uuid);
        return true;
    }

    public void addMana(UUID uuid, int amount) {
        int max = getMaxMana();
        manaMap.put(uuid, Math.min(getMana(uuid) + amount, max));
        refreshBar(uuid);
    }

    public void setMana(UUID uuid, int amount) {
        manaMap.put(uuid, Math.max(0, Math.min(amount, getMaxMana())));
        refreshBar(uuid);
    }

private void refreshBar(UUID uuid) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) return;
    int mana = getMana(uuid);
    int max = getMaxMana();
    player.setLevel(mana);
    player.setExp(Math.max(0f, Math.min(1f, (float) mana / max)));
    // Show mana as action bar text in light blue above XP bar
    player.sendActionBar(
        net.md_5.bungee.api.ChatColor.of("#00BFFF") + "✦ Mana: " + mana + " / " + max
    );
}
    public void startRegenScheduler() {
        int interval = plugin.getConfigManager().getRegenInterval();
        int amount = plugin.getConfigManager().getRegenAmount();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
addMana(p.getUniqueId(), amount);
// Refresh action bar for all online players every regen tick
int currentMana = getMana(p.getUniqueId());
int maxMana = getMaxMana();
p.sendActionBar(
    net.md_5.bungee.api.ChatColor.of("#00BFFF") + "✦ Mana: " + currentMana + " / " + maxMana
);
                }
            }
        }.runTaskTimer(plugin, 20L, interval * 20L);
    }
}
