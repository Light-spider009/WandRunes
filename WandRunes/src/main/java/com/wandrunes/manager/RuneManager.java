package com.wandrunes.manager;

import com.wandrunes.WandRunes;
import com.wandrunes.rune.RuneType;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RuneManager {

    private final WandRunes plugin;
    // Location -> RuneType id
    private final Map<Location, String> activeRunes = new HashMap<>();
    // One-time runes already fired
    private final Set<Location> firedRunes = new HashSet<>();
    private final File dataFile;
    private FileConfiguration dataConfig;

    public RuneManager(WandRunes plugin) {
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "runes.yml");
        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            try { dataFile.getParentFile().mkdirs(); dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        if (dataConfig.contains("runes")) {
            for (String key : dataConfig.getConfigurationSection("runes").getKeys(false)) {
                String[] parts = key.split("_");
                if (parts.length == 4) {
                    World world = Bukkit.getWorld(parts[0]);
                    if (world != null) {
                        Location loc = new Location(world,
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]));
                        activeRunes.put(loc, dataConfig.getString("runes." + key));
                    }
                }
            }
        }
    }

    public void saveAll() {
        dataConfig.set("runes", null);
        for (Map.Entry<Location, String> entry : activeRunes.entrySet()) {
            Location l = entry.getKey();
            String key = l.getWorld().getName() + "_" + l.getBlockX() + "_" + l.getBlockY() + "_" + l.getBlockZ();
            dataConfig.set("runes." + key, entry.getValue());
        }
        try { dataConfig.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public void placeRune(Location loc, RuneType type) {
        if (!plugin.getConfigManager().isRuneEnabled(type.getId())) return;
        activeRunes.put(loc, type.getId());
        spawnRuneParticles(loc, type);
    }

    public void removeRune(Location loc) {
        activeRunes.remove(loc);
        firedRunes.remove(loc);
    }

    public boolean hasRune(Location loc) {
        return activeRunes.containsKey(loc);
    }

    private void spawnRuneParticles(Location loc, RuneType type) {
        Color color = switch (type) {
            case HEALING -> Color.LIME;
            case INFERNO -> Color.ORANGE;
            case STORM -> Color.YELLOW;
            case SPEED -> Color.AQUA;
            case BARRIER -> Color.BLUE;
            case CURSE -> Color.PURPLE;
            case SUMMON -> Color.GRAY;
            case TIME -> Color.WHITE;
        };
        Location center = loc.clone().add(0.5, 0.1, 0.5);
        for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 8) {
            double x = 2 * Math.cos(angle);
            double z = 2 * Math.sin(angle);
            loc.getWorld().spawnParticle(Particle.REDSTONE,
                center.clone().add(x, 0, z), 1,
                new Particle.DustOptions(color, 1.5f));
        }
    }

    public void startTickScheduler() {
        new BukkitRunnable() {
            int tick = 0;
            @Override public void run() {
                tick++;
                for (Map.Entry<Location, String> entry : new HashMap<>(activeRunes).entrySet()) {
                    Location loc = entry.getKey();
                    RuneType type = RuneType.fromId(entry.getValue());
                    if (type == null) continue;
                    if (!plugin.getConfigManager().isRuneEnabled(type.getId())) continue;

                    // Refresh particles every second
                    if (tick % 20 == 0) spawnRuneParticles(loc, type);

                    // Apply effects
                    String power = plugin.getConfigManager().getRunePower(type.getId());
                    int radius = 3;

                    switch (type) {
                        case HEALING -> {
                            if (tick % 40 == 0) { // every 2s
                                for (Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
                                    if (e instanceof Player p) {
                                        int amp = power.equals("STRONG") ? 2 : power.equals("WEAK") ? 0 : 1;
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, amp));
                                    }
                                }
                            }
                        }
                        case INFERNO -> {
                            for (Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
                                if (e instanceof Monster m) {
                                    int fireTicks = power.equals("STRONG") ? 80 : power.equals("WEAK") ? 20 : 40;
                                    m.setFireTicks(fireTicks);
                                }
                            }
                        }
                        case STORM -> {
                            if (tick % 60 == 0) { // every 3s
                                for (Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
                                    if (e instanceof Monster) {
                                        loc.getWorld().strikeLightning(e.getLocation());
                                    }
                                }
                            }
                        }
                        case SPEED -> {
                            if (tick % 20 == 0) {
                                for (Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
                                    if (e instanceof Player p) {
                                        int amp = power.equals("STRONG") ? 3 : power.equals("WEAK") ? 1 : 2;
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, amp));
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, 1));
                                    }
                                }
                            }
                        }
                        case BARRIER -> {
                            for (Entity e : loc.getWorld().getNearbyEntities(loc, radius + 1, radius, radius + 1)) {
                                if (e instanceof Monster m) {
                                    org.bukkit.util.Vector dir = m.getLocation().toVector()
                                        .subtract(loc.toVector()).normalize().multiply(1.5);
                                    dir.setY(0.3);
                                    m.setVelocity(dir);
                                }
                            }
                        }
                        case CURSE -> {
                            for (Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
                                if (e instanceof Monster m) {
                                    m.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1));
                                    m.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 60, 0));
                                }
                            }
                        }
                        case SUMMON -> {
                            if (!firedRunes.contains(loc)) {
                                firedRunes.add(loc);
                                int count = power.equals("STRONG") ? 5 : power.equals("WEAK") ? 2 : 3;
                                for (int i = 0; i < count; i++) {
                                    loc.getWorld().spawnEntity(loc, EntityType.PHANTOM);
                                }
                            }
                        }
                        case TIME -> {
                            if (!firedRunes.contains(loc)) {
                                firedRunes.add(loc);
                                int r2 = power.equals("STRONG") ? 8 : power.equals("WEAK") ? 3 : 5;
                                for (int dx = -r2; dx <= r2; dx++) {
                                    for (int dz = -r2; dz <= r2; dz++) {
                                        for (int dy = -2; dy <= 2; dy++) {
                                            var block = loc.clone().add(dx, dy, dz).getBlock();
                                            if (block.getBlockData() instanceof org.bukkit.block.data.Ageable ag) {
                                                ag.setAge(ag.getMaximumAge());
                                                block.setBlockData(ag);
                                            }
                                        }
                                    }
                                }
                                loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc.clone().add(0, 1, 0), 20, 2, 1, 2, 0.1);
                                removeRune(loc);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 1L);
    }

    public Map<Location, String> getActiveRunes() { return activeRunes; }
}
