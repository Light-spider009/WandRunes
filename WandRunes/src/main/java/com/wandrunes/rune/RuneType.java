package com.wandrunes.rune;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum RuneType {

    HEALING("healing", ChatColor.GREEN + "✦ Healing Circle",
        Material.GLISTERING_MELON_SLICE,
        "Heals all players inside every 2 seconds",
        new String[]{"Glistering Melon x4", "Soul Essence x2", "Arcane Dust x4"},
        ChatColor.GREEN),

    INFERNO("inferno", ChatColor.RED + "✦ Inferno Circle",
        Material.MAGMA_BLOCK,
        "Burns all enemies that step inside",
        new String[]{"Magma Block x4", "Ember Core x1", "Arcane Dust x3"},
        ChatColor.RED),

    STORM("storm", ChatColor.YELLOW + "✦ Storm Circle",
        Material.LIGHTNING_ROD,
        "Rains lightning on enemies every 3 seconds",
        new String[]{"Storm Core x2", "Lightning Rod x2", "Arcane Dust x4"},
        ChatColor.YELLOW),

    SPEED("speed", ChatColor.AQUA + "✦ Speed Circle",
        Material.SUGAR,
        "Gives Speed III and Jump Boost II to players inside",
        new String[]{"Sugar x8", "Feather x4", "Arcane Dust x2"},
        ChatColor.AQUA),

    BARRIER("barrier", ChatColor.BLUE + "✦ Barrier Circle",
        Material.OBSIDIAN,
        "Pushes enemies away from the circle edge",
        new String[]{"Obsidian x4", "Void Fragment x1", "Mana Crystal x1"},
        ChatColor.BLUE),

    CURSE("curse", ChatColor.DARK_PURPLE + "✦ Curse Circle",
        Material.WITHER_ROSE,
        "Applies Wither and Nausea to enemies inside",
        new String[]{"Wither Rose x4", "Soul Essence x3", "Arcane Dust x3"},
        ChatColor.DARK_PURPLE),

    SUMMON("summon", ChatColor.GRAY + "✦ Summon Circle",
        Material.PHANTOM_MEMBRANE,
        "Spawns Phantoms to fight for you (one time)",
        new String[]{"Phantom Membrane x6", "Soul Essence x4", "Mana Crystal x2"},
        ChatColor.GRAY),

    TIME("time", ChatColor.LIGHT_PURPLE + "✦ Time Circle",
        Material.CLOCK,
        "Instantly grows all crops in radius (one time)",
        new String[]{"Clock x4", "Chrono Shard x2", "Arcane Dust x4"},
        ChatColor.LIGHT_PURPLE);

    private final String id;
    private final String displayName;
    private final Material material;
    private final String description;
    private final String[] recipe;
    private final ChatColor color;

    RuneType(String id, String displayName, Material material,
             String description, String[] recipe, ChatColor color) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.description = description;
        this.recipe = recipe;
        this.color = color;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Material getMaterial() { return material; }
    public String getDescription() { return description; }
    public String[] getRecipe() { return recipe; }
    public ChatColor getColor() { return color; }

    public static RuneType fromId(String id) {
        for (RuneType r : values()) {
            if (r.id.equalsIgnoreCase(id)) return r;
        }
        return null;
    }
}
