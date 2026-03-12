package com.wandrunes.manager;

import com.wandrunes.WandRunes;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.chat.ComponentSerializer;
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
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        return true;
    }

    public void addMana(UUID uuid, int amount) {
        int max = getMaxMana();
        manaMap.put(uuid, Math.min(getMana(uuid) + amount, max));
    }

    public void setMana(UUID uuid, int amount) {
        manaMap.put(uuid, Math.max(0, Math.min(amount, getMaxMana())));
    }

    private void sendManaBar(Player player) {
        int mana = getMana(player.getUniqueId());
        int max = getMaxMana();
        String json = "[{\"text\":\"\\u2726 Mana: " + mana + " / " + max + "\",\"color\":\"aqua\"}]";
        player.spigot().sendMessage(
            ChatMessageType.ACTION_BAR,
            ComponentSerializer.parse(json)
        );
    }

    // Regens mana every interval — XP bar is never touched
    public void startRegenScheduler() {
        int interval = plugin.getConfigManager().getRegenInterval();
        int amount = plugin.getConfigManager().getRegenAmount();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    addMana(p.getUniqueId(), amount);
                }
            }
        }.runTaskTimer(plugin, 20L, interval * 20L);
    }

    // Keeps the action bar permanently visible by refreshing every second
    public void startActionBarScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    sendManaBar(p);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}
