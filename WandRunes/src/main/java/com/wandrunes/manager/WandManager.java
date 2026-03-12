package com.wandrunes.manager;

import com.wandrunes.WandRunes;
import com.wandrunes.wand.WandType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WandManager {

    private final WandRunes plugin;
    // castCount per player for overheat
    private final Map<UUID, Integer> castCount = new HashMap<>();
    private final Map<UUID, Long> overheatUntil = new HashMap<>();
    // discovered spells per player
    private final Map<UUID, Set<String>> discovered = new HashMap<>();
    // mastered wands
    private final Map<UUID, Set<String>> mastered = new HashMap<>();
    // charging state
    private final Map<UUID, Long> chargeStart = new HashMap<>();

    private final File dataFile;
    private FileConfiguration dataConfig;

    public WandManager(WandRunes plugin) {
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "wands.yml");
        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            try { dataFile.getParentFile().mkdirs(); dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        for (String uuidStr : dataConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            List<String> disc = dataConfig.getStringList(uuidStr + ".discovered");
            discovered.put(uuid, new HashSet<>(disc));
            List<String> mast = dataConfig.getStringList(uuidStr + ".mastered");
            mastered.put(uuid, new HashSet<>(mast));
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, Set<String>> entry : discovered.entrySet()) {
            dataConfig.set(entry.getKey() + ".discovered", new ArrayList<>(entry.getValue()));
        }
        for (Map.Entry<UUID, Set<String>> entry : mastered.entrySet()) {
            dataConfig.set(entry.getKey() + ".mastered", new ArrayList<>(entry.getValue()));
        }
        try { dataConfig.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public boolean isOverheated(UUID uuid) {
        Long until = overheatUntil.get(uuid);
        return until != null && System.currentTimeMillis() < until;
    }

    public int getOverheatSecondsLeft(UUID uuid) {
        Long until = overheatUntil.get(uuid);
        if (until == null) return 0;
        return (int) Math.ceil((until - System.currentTimeMillis()) / 1000.0);
    }

    public void recordCast(UUID uuid) {
        int count = castCount.getOrDefault(uuid, 0) + 1;
        castCount.put(uuid, count);
        int limit = plugin.getConfigManager().getOverheatLimit();
        if (count >= limit) {
            int cooldown = plugin.getConfigManager().getOverheatCooldown();
            overheatUntil.put(uuid, System.currentTimeMillis() + cooldown * 1000L);
            castCount.put(uuid, 0);
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.sendMessage(plugin.getConfigManager().getOverheatMsg(cooldown));
        }
        // Reset cast count after 3 seconds
        new BukkitRunnable() {
            @Override public void run() {
                castCount.merge(uuid, -1, Integer::sum);
            }
        }.runTaskLater(plugin, 60L);
    }

    public boolean isDiscovered(UUID uuid, String wandId) {
        return discovered.getOrDefault(uuid, new HashSet<>()).contains(wandId);
    }

    public void discover(UUID uuid, String wandId) {
        discovered.computeIfAbsent(uuid, k -> new HashSet<>()).add(wandId);
        Player p = Bukkit.getPlayer(uuid);
        WandType type = WandType.fromId(wandId);
        if (p != null && type != null) {
            p.sendMessage(plugin.getConfigManager().getSpellDiscovered(type.getDisplayName()));
        }
    }

    public boolean isMastered(UUID uuid, String wandId) {
        return mastered.getOrDefault(uuid, new HashSet<>()).contains(wandId);
    }

    public void addWandXP(Player player, ItemStack wand, int xp) {
        if (wand == null || !wand.hasItemMeta()) return;
        String wandId = plugin.getWandRegistry().getWandId(wand);
        if (wandId == null) return;
        WandType type = WandType.fromId(wandId);
        if (type == null) return;

        NamespacedKey xpKey = new NamespacedKey(plugin, "wandrunes_wand_xp");
        NamespacedKey lvKey = new NamespacedKey(plugin, "wandrunes_wand_level");
        ItemMeta meta = wand.getItemMeta();
        var pdc = meta.getPersistentDataContainer();

        int currentXP = pdc.getOrDefault(xpKey, PersistentDataType.INTEGER, 0) + xp;
        int currentLevel = pdc.getOrDefault(lvKey, PersistentDataType.INTEGER, 1);
        int xpToNext = 50 * currentLevel;

        while (currentXP >= xpToNext && currentLevel < type.getMaxLevel()) {
            currentXP -= xpToNext;
            currentLevel++;
            xpToNext = 50 * currentLevel;
            player.sendMessage(plugin.getConfigManager().getWandLevelUp(type.getDisplayName(), currentLevel));
        }

        if (currentLevel >= type.getMaxLevel() && !isMastered(player.getUniqueId(), wandId)) {
            mastered.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(wandId);
            player.sendMessage(plugin.getConfigManager().getWandMastered(type.getDisplayName(), type.getMasteryTitle()));
        }

        pdc.set(xpKey, PersistentDataType.INTEGER, currentXP);
        pdc.set(lvKey, PersistentDataType.INTEGER, currentLevel);

        // Update display name with new level
        meta.setDisplayName(type.getDisplayName() + ChatColor.GRAY + " [Lv." + currentLevel + "]");
        wand.setItemMeta(meta);
    }

    public void startCharge(UUID uuid) {
        chargeStart.put(uuid, System.currentTimeMillis());
    }

    public boolean isCharged(UUID uuid) {
        Long start = chargeStart.get(uuid);
        if (start == null) return false;
        int chargeTicks = plugin.getConfigManager().getChargeTicks();
        return System.currentTimeMillis() - start >= (chargeTicks * 50L);
    }

    public void clearCharge(UUID uuid) {
        chargeStart.remove(uuid);
    }

    public void startParticleScheduler() {
        new BukkitRunnable() {
            @Override public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    ItemStack held = p.getInventory().getItemInMainHand();
                    String wandId = plugin.getWandRegistry().getWandId(held);
                    if (wandId != null) {
                        WandType type = WandType.fromId(wandId);
                        if (type != null) {
                            p.getWorld().spawnParticle(type.getParticle(),
                                p.getLocation().add(0, 1.1, 0), 3, 0.1, 0.1, 0.1, 0.01);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 5L, 5L);
    }

    public Map<UUID, Set<String>> getDiscovered() { return discovered; }
    public Map<UUID, Set<String>> getMastered() { return mastered; }
}
