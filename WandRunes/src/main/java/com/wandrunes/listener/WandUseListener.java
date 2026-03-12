package com.wandrunes.listener;

import com.wandrunes.WandRunes;
import com.wandrunes.wand.WandType;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class WandUseListener implements Listener {

    private final WandRunes plugin;
    private final Map<UUID, Long> lastCombo = new HashMap<>();
    private final Map<UUID, String> lastWandUsed = new HashMap<>();
    private final Random random = new Random();

    public WandUseListener(WandRunes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWandUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        String wandId = plugin.getWandRegistry().getWandId(item);
        if (wandId == null) return;

        event.setCancelled(true);
        WandType type = WandType.fromId(wandId);
        if (type == null) return;

        // Check enabled
        if (!plugin.getConfigManager().isWandEnabled(type.getId())) {
            player.sendMessage(plugin.getConfigManager().getWandDisabled());
            return;
        }

        // Check world blacklist
        List<String> blacklist = plugin.getConfigManager().getWandBlacklist(type.getId());
        if (blacklist.contains(player.getWorld().getName())) {
            player.sendMessage(plugin.getConfigManager().getWandBlacklisted());
            return;
        }

        // Check overheat
        if (plugin.getWandManager().isOverheated(player.getUniqueId())) {
            int left = plugin.getWandManager().getOverheatSecondsLeft(player.getUniqueId());
            player.sendMessage(plugin.getConfigManager().getOverheatMsg(left));
            return;
        }

        // Auto-discover spell on first use
        if (!plugin.getWandManager().isDiscovered(player.getUniqueId(), wandId)) {
            plugin.getWandManager().discover(player.getUniqueId(), wandId);
        }

        // Check mana
        int mana = plugin.getConfigManager().getWandMana(type.getId(), type.getBaseMana());
        boolean charged = plugin.getWandManager().isCharged(player.getUniqueId());
        int totalMana = charged ? mana * 2 : mana;
        plugin.getWandManager().clearCharge(player.getUniqueId());

        if (!plugin.getManaManager().consumeMana(player.getUniqueId(), totalMana)) {
            player.sendMessage(plugin.getConfigManager().getNotEnoughMana(
                totalMana, plugin.getManaManager().getMana(player.getUniqueId())));
            return;
        }

        // Check combo
        checkCombo(player, type);

        // Cast spell
        double power = plugin.getConfigManager().getWandPower(type.getId()).equals("STRONG") ? 1.5
            : plugin.getConfigManager().getWandPower(type.getId()).equals("WEAK") ? 0.7 : 1.0;
        if (charged) power *= plugin.getConfigManager().getChargedMultiplier();

        castSpell(player, type, power);
        plugin.getWandManager().recordCast(player.getUniqueId());
        plugin.getWandManager().addWandXP(player, item, 5);

        // Slight knockback on caster for powerful spells
        if (mana >= 18) {
            Vector back = player.getLocation().getDirection().multiply(-0.4);
            back.setY(0.2);
            player.setVelocity(back);
        }
    }

    @EventHandler
    public void onWandHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        if (plugin.getWandRegistry().isWand(newItem)) {
            plugin.getWandManager().startCharge(player.getUniqueId());
        } else {
            plugin.getWandManager().clearCharge(player.getUniqueId());
        }
    }

    private void checkCombo(Player player, WandType type) {
        String last = lastWandUsed.get(player.getUniqueId());
        long lastTime = lastCombo.getOrDefault(player.getUniqueId(), 0L);
        boolean inWindow = System.currentTimeMillis() - lastTime < 3000;

        if (last != null && inWindow && !last.equals(type.getId())) {
            String combo = getComboName(last, type.getId());
            if (combo != null) {
                player.sendMessage(plugin.getConfigManager().getPrefix()
                    + ChatColor.GOLD + "✦ COMBO: " + ChatColor.YELLOW + combo + ChatColor.GOLD + " activated!");
                executeCombo(player, last, type.getId());
            }
        }

        lastWandUsed.put(player.getUniqueId(), type.getId());
        lastCombo.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private String getComboName(String a, String b) {
        Set<String> pair = Set.of(a, b);
        if (pair.equals(Set.of("ember", "frost"))) return "Steam Burst";
        if (pair.equals(Set.of("storm", "wind"))) return "Thunderstorm";
        if (pair.equals(Set.of("soul", "void"))) return "Dark Nova";
        if (pair.equals(Set.of("blink", "arcane"))) return "Arcane Surge";
        if (pair.equals(Set.of("venom", "gravity"))) return "Toxic Rain";
        return null;
    }

    private void executeCombo(Player player, String a, String b) {
        Set<String> pair = Set.of(a, b);
        Location loc = player.getLocation();

        if (pair.equals(Set.of("ember", "frost"))) {
            // Steam Burst - large area damage
            for (Entity e : loc.getWorld().getNearbyEntities(loc, 6, 4, 6)) {
                if (e instanceof LivingEntity le && !e.equals(player)) {
                    le.damage(8, player);
                    le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 2));
                }
            }
            loc.getWorld().spawnParticle(Particle.CLOUD, loc.clone().add(0, 1, 0), 40, 3, 2, 3, 0.05);
        }
        else if (pair.equals(Set.of("storm", "wind"))) {
            // Thunderstorm - strike all nearby enemies
            for (Entity e : loc.getWorld().getNearbyEntities(loc, 8, 4, 8)) {
                if (e instanceof Monster) {
                    loc.getWorld().strikeLightning(e.getLocation());
                }
            }
        }
        else if (pair.equals(Set.of("soul", "void"))) {
            // Dark Nova - massive pull + wither
            for (Entity e : loc.getWorld().getNearbyEntities(loc, 10, 5, 10)) {
                if (e instanceof LivingEntity le && !e.equals(player)) {
                    Vector pull = loc.toVector().subtract(e.getLocation().toVector()).normalize().multiply(2);
                    e.setVelocity(pull);
                    if (le instanceof Monster) le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 2));
                }
            }
            loc.getWorld().spawnParticle(Particle.SOUL, loc.clone().add(0, 1, 0), 50, 5, 2, 5, 0.1);
        }
        else if (pair.equals(Set.of("blink", "arcane"))) {
            // Arcane Surge - refill 30 mana instantly
            plugin.getManaManager().addMana(player.getUniqueId(), 30);
            player.sendMessage(plugin.getConfigManager().getPrefix()
                + ChatColor.DARK_AQUA + "Arcane Surge restored &b30 mana!");
            player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, player.getLocation().add(0, 1, 0), 30, 1, 1, 1, 0.5);
        }
        else if (pair.equals(Set.of("venom", "gravity"))) {
            // Toxic Rain - all enemies poisoned and launched
            for (Entity e : loc.getWorld().getNearbyEntities(loc, 7, 4, 7)) {
                if (e instanceof Monster m) {
                    m.setVelocity(new Vector(0, 3, 0));
                    m.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 2));
                }
            }
        }
    }

    private void castSpell(Player player, WandType type, double power) {
        Location loc = player.getLocation();
        World world = player.getWorld();

        switch (type) {
            case EMBER -> {
                // Fireball that explodes
                Fireball fb = player.launchProjectile(Fireball.class);
                fb.setVelocity(player.getLocation().getDirection().multiply(1.5 * power));
                fb.setIsIncendiary(true);
                fb.setYield((float)(1.5 * power));
                world.spawnParticle(Particle.FLAME, loc.clone().add(0, 1, 0), 10, 0.2, 0.2, 0.2, 0.05);
            }
            case STORM -> {
                // Lightning on target or nearest entity
                Entity target = getNearestEntity(player, 20);
                Location strike = (target != null) ? target.getLocation() : getTargetLocation(player, 20);
                world.strikeLightning(strike);
                if (power > 1.5) world.strikeLightningEffect(strike.clone().add(1, 0, 0));
                world.spawnParticle(Particle.ELECTRIC_SPARK, loc.clone().add(0, 1, 0), 15, 0.3, 0.3, 0.3, 0.05);
            }
            case FROST -> {
                // Freeze nearest entity
                Entity target = getNearestEntity(player, 15);
                if (target instanceof LivingEntity le) {
                    int dur = (int)(80 * power);
                    le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, dur, 3));
                    le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, dur, 2));
                    le.setFreezeTicks((int)(80 * power));
                    world.spawnParticle(Particle.SNOWFLAKE, le.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.01);
                }
            }
            case BLINK -> {
                // Teleport to look direction
                Location target2 = getTargetLocation(player, (int)(15 * power));
                world.spawnParticle(Particle.PORTAL, loc.clone().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1);
                player.teleport(target2);
                world.spawnParticle(Particle.PORTAL, target2.clone().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1);
            }
            case VENOM -> {
                Entity target = getNearestEntity(player, 15);
                if (target instanceof LivingEntity le) {
                    int dur = (int)(100 * power);
                    le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, dur, 1));
                    le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int)(60 * power), 0));
                    world.spawnParticle(Particle.SPELL_MOB, le.getLocation().add(0, 1, 0), 15, 0.3, 0.3, 0.3, 0.01);
                }
            }
            case GRAVITY -> {
                Entity target = getNearestEntity(player, 15);
                if (target != null) {
                    double lift = 2.5 * power;
                    target.setVelocity(new Vector(0, lift, 0));
                    world.spawnParticle(Particle.CLOUD, target.getLocation().add(0, 1, 0), 15, 0.3, 0.3, 0.3, 0.05);
                }
            }
            case SOUL -> {
                Entity target = getNearestEntity(player, 12);
                if (target instanceof LivingEntity le) {
                    double dmg = 6 * power;
                    le.damage(dmg, player);
                    player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + dmg * 0.5));
                    world.spawnParticle(Particle.SOUL, target.getLocation().add(0, 1, 0), 15, 0.3, 0.5, 0.3, 0.05);
                }
            }
            case VOID -> {
                double range = 8 * power;
                for (Entity e : world.getNearbyEntities(loc, range, range / 2, range)) {
                    if (e instanceof LivingEntity && !e.equals(player)) {
                        Vector pull = loc.toVector().subtract(e.getLocation().toVector()).normalize().multiply(1.5 * power);
                        pull.setY(0.3);
                        e.setVelocity(pull);
                    }
                }
                world.spawnParticle(Particle.ASH, loc.clone().add(0, 1, 0), 40, 3, 2, 3, 0.02);
            }
            case WIND -> {
                double range = 7 * power;
                for (Entity e : world.getNearbyEntities(loc, range, range / 2, range)) {
                    if (!e.equals(player)) {
                        Vector push = e.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(2.5 * power);
                        push.setY(0.5);
                        e.setVelocity(push);
                    }
                }
                world.spawnParticle(Particle.SWEEP_ATTACK, loc.clone().add(0, 1, 0), 20, 2, 1, 2, 0.05);
            }
            case ARCANE -> {
                // Random spell from other wands
                WandType[] others = {WandType.EMBER, WandType.STORM, WandType.FROST,
                    WandType.BLINK, WandType.VENOM, WandType.GRAVITY, WandType.SOUL};
                WandType picked = others[random.nextInt(others.length)];
                player.sendMessage(plugin.getConfigManager().getPrefix()
                    + ChatColor.GOLD + "Arcane picks: " + picked.getDisplayName());
                castSpell(player, picked, power);
            }
        }
    }

    private Entity getNearestEntity(Player player, double range) {
        Location loc = player.getLocation();
        return player.getWorld().getNearbyEntities(loc, range, range / 2, range)
            .stream()
            .filter(e -> e instanceof LivingEntity && !e.equals(player))
            .min(Comparator.comparingDouble(e -> e.getLocation().distanceSquared(loc)))
            .orElse(null);
    }

    private Location getTargetLocation(Player player, int maxDistance) {
        return player.getTargetBlock(null, maxDistance).getLocation().add(0, 1, 0);
    }
}
