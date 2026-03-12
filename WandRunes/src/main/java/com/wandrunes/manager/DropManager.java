package com.wandrunes.manager;

import com.wandrunes.WandRunes;
import com.wandrunes.wand.WandRegistry;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class DropManager {

    private final WandRunes plugin;
    private final Random random = new Random();

    public DropManager(WandRunes plugin) {
        this.plugin = plugin;
    }

    public ItemStack getDropForEntity(Entity entity) {
        WandRegistry reg = plugin.getWandRegistry();

        if (entity instanceof Blaze) {
            if (roll(plugin.getConfigManager().getEmberCoreChance()))
                return reg.createMaterial(WandRegistry.MAT_EMBER_CORE, "Ember Core",
                    Material.BLAZE_POWDER, "Blazing core of a Blaze");
        }

        if (entity instanceof Stray) {
            if (roll(plugin.getConfigManager().getFrostHeartChance()))
                return reg.createMaterial(WandRegistry.MAT_FROST_HEART, "Frost Heart",
                    Material.BLUE_ICE, "Frozen heart of a Stray");
        }

        // Charged Creeper check (no CreatedFirework class — use isPowered())
        if (entity instanceof Creeper creeper && creeper.isPowered()) {
            if (roll(plugin.getConfigManager().getStormCoreChance()))
                return reg.createMaterial(WandRegistry.MAT_STORM_CORE, "Storm Core",
                    Material.LIGHTNING_ROD, "Core of a Charged Creeper");
        }

        if (entity instanceof Enderman) {
            if (roll(plugin.getConfigManager().getVoidFragmentChance()))
                return reg.createMaterial(WandRegistry.MAT_VOID_FRAGMENT, "Void Fragment",
                    Material.ECHO_SHARD, "Fragment from the void");
        }

        // Normal (non-charged) Creeper
        if (entity instanceof Creeper creeper && !creeper.isPowered()) {
            if (roll(plugin.getConfigManager().getChronoShardChance()))
                return reg.createMaterial(WandRegistry.MAT_CHRONO_SHARD, "Chrono Shard",
                    Material.AMETHYST_SHARD, "Shard of crystallized time");
        }

        // Any mob can drop soul essence
        if (roll(plugin.getConfigManager().getSoulEssenceChance()))
            return reg.createMaterial(WandRegistry.MAT_SOUL_ESSENCE, "Soul Essence",
                Material.GLASS_BOTTLE, "Bottled soul energy");

        return null;
    }

    public ItemStack getArcaneDust() {
        return plugin.getWandRegistry().createMaterial(
            WandRegistry.MAT_ARCANE_DUST, "Arcane Dust",
            Material.GLOWSTONE_DUST, "Magical residue");
    }

    public ItemStack getManaCrystal() {
        return plugin.getWandRegistry().createMaterial(
            WandRegistry.MAT_MANA_CRYSTAL, "Mana Crystal",
            Material.AMETHYST_SHARD, "Solidified mana");
    }

    private boolean roll(double chance) {
        return random.nextDouble() < chance;
    }

    public boolean isBoss(Entity entity) {
        return entity instanceof EnderDragon || entity instanceof Wither;
    }
}
