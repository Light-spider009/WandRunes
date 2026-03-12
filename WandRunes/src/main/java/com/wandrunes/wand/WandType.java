package com.wandrunes.wand;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Color;

public enum WandType {

    EMBER("ember", ChatColor.RED + "✦ Ember Wand",
        Material.BLAZE_ROD, Particle.FLAME,
        "Launches an exploding fireball", 15, 3,
        new String[]{"Blaze Powder x4", "Ember Core x1", "Arcane Dust x3"},
        "[Ember Master]", ChatColor.RED),

    STORM("storm", ChatColor.YELLOW + "✦ Storm Wand",
        Material.LIGHTNING_ROD, Particle.ELECTRIC_SPARK,
        "Strikes target with lightning", 20, 3,
        new String[]{"Storm Core x2", "Gold Rod x1", "Arcane Dust x3"},
        "[Storm Master]", ChatColor.YELLOW),

    FROST("frost", ChatColor.AQUA + "✦ Frost Wand",
        Material.PACKED_ICE, Particle.SNOWFLAKE,
        "Freezes and slows enemy", 12, 2,
        new String[]{"Frost Heart x2", "Blue Ice x4", "Arcane Dust x2"},
        "[Frost Master]", ChatColor.AQUA),

    BLINK("blink", ChatColor.DARK_AQUA + "✦ Blink Wand",
        Material.ENDER_PEARL, Particle.PORTAL,
        "Teleport where you are looking", 10, 2,
        new String[]{"Ender Pearl x4", "Void Fragment x1", "Arcane Dust x2"},
        "[Blink Master]", ChatColor.DARK_AQUA),

    VENOM("venom", ChatColor.DARK_GREEN + "✦ Venom Wand",
        Material.POISONOUS_POTATO, Particle.SPELL_MOB,
        "Poisons and blinds target", 12, 2,
        new String[]{"Spider Eye x4", "Fermented Eye x2", "Arcane Dust x2"},
        "[Venom Master]", ChatColor.DARK_GREEN),

    GRAVITY("gravity", ChatColor.LIGHT_PURPLE + "✦ Gravity Wand",
        Material.PISTON, Particle.CLOUD,
        "Launches enemy high into the air", 18, 3,
        new String[]{"Piston x2", "Slime Ball x4", "Arcane Dust x3"},
        "[Gravity Master]", ChatColor.LIGHT_PURPLE),

    SOUL("soul", ChatColor.DARK_PURPLE + "✦ Soul Wand",
        Material.WITHER_SKELETON_SKULL, Particle.SOUL,
        "Drain enemy health to heal yourself", 25, 4,
        new String[]{"Soul Essence x5", "Wither Skull x1", "Mana Crystal x2"},
        "[Soul Master]", ChatColor.DARK_PURPLE),

    VOID("void", ChatColor.BLACK + "" + ChatColor.BOLD + "✦ " + ChatColor.DARK_GRAY + "Void Wand",
        Material.ECHO_SHARD, Particle.ASH,
        "Pull all nearby enemies toward you", 20, 4,
        new String[]{"Void Fragment x3", "Echo Shard x2", "Mana Crystal x1"},
        "[Void Master]", ChatColor.DARK_GRAY),

    WIND("wind", ChatColor.WHITE + "✦ Wind Wand",
        Material.FEATHER, Particle.SWEEP_ATTACK,
        "Massive knockback blast in all directions", 15, 3,
        new String[]{"Feather x8", "Phantom Membrane x2", "Arcane Dust x3"},
        "[Wind Master]", ChatColor.WHITE),

    ARCANE("arcane", ChatColor.GOLD + "✦ Arcane Wand",
        Material.AMETHYST_SHARD, Particle.ENCHANTMENT_TABLE,
        "Random spell each cast — wildcard!", 8, 1,
        new String[]{"Mana Crystal x1", "Arcane Dust x5", "Amethyst x3"},
        "[Arcane Master]", ChatColor.GOLD);

    private final String id;
    private final String displayName;
    private final Material material;
    private final Particle particle;
    private final String description;
    private final int baseMana;
    private final int maxLevel;
    private final String[] recipe;
    private final String masteryTitle;
    private final ChatColor color;

    WandType(String id, String displayName, Material material, Particle particle,
             String description, int baseMana, int maxLevel, String[] recipe,
             String masteryTitle, ChatColor color) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.particle = particle;
        this.description = description;
        this.baseMana = baseMana;
        this.maxLevel = maxLevel;
        this.recipe = recipe;
        this.masteryTitle = masteryTitle;
        this.color = color;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Material getMaterial() { return material; }
    public Particle getParticle() { return particle; }
    public String getDescription() { return description; }
    public int getBaseMana() { return baseMana; }
    public int getMaxLevel() { return maxLevel; }
    public String[] getRecipe() { return recipe; }
    public String getMasteryTitle() { return masteryTitle; }
    public ChatColor getColor() { return color; }

    public static WandType fromId(String id) {
        for (WandType w : values()) {
            if (w.id.equalsIgnoreCase(id)) return w;
        }
        return null;
    }
}
