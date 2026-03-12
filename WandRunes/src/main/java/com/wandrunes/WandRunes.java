package com.wandrunes;

import com.wandrunes.command.ManaCommand;
import com.wandrunes.command.WandAdminCommand;
import com.wandrunes.command.WandBagCommand;
import com.wandrunes.command.WandRunesCommand;
import com.wandrunes.listener.ManaListener;
import com.wandrunes.listener.RuneListener;
import com.wandrunes.listener.WandBagListener;
import com.wandrunes.listener.WandCraftListener;
import com.wandrunes.listener.WandUseListener;
import com.wandrunes.manager.ConfigManager;
import com.wandrunes.manager.DropManager;
import com.wandrunes.manager.ManaManager;
import com.wandrunes.manager.RuneManager;
import com.wandrunes.manager.WandManager;
import com.wandrunes.wand.WandRegistry;
import org.bukkit.plugin.java.JavaPlugin;

public class WandRunes extends JavaPlugin {

    private static WandRunes instance;
    private ConfigManager configManager;
    private ManaManager manaManager;
    private WandManager wandManager;
    private RuneManager runeManager;
    private DropManager dropManager;
    private WandRegistry wandRegistry;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        configManager = new ConfigManager(this);
        wandRegistry = new WandRegistry(this);
        manaManager = new ManaManager(this);
        wandManager = new WandManager(this);
        runeManager = new RuneManager(this);
        dropManager = new DropManager(this);

        getCommand("wandrunes").setExecutor(new WandRunesCommand(this));
        getCommand("mana").setExecutor(new ManaCommand(this));
        getCommand("wanDbag").setExecutor(new WandBagCommand(this));
        getCommand("wandadmin").setExecutor(new WandAdminCommand(this));

        getServer().getPluginManager().registerEvents(new WandUseListener(this), this);
        getServer().getPluginManager().registerEvents(new RuneListener(this), this);
        getServer().getPluginManager().registerEvents(new ManaListener(this), this);
        getServer().getPluginManager().registerEvents(new WandBagListener(this), this);
        getServer().getPluginManager().registerEvents(new WandCraftListener(this), this);
        getServer().getPluginManager().registerEvents(new com.wandrunes.gui.GUIListener(this), this);

        manaManager.startRegenScheduler();
        runeManager.startTickScheduler();
        wandManager.startParticleScheduler();

        getLogger().info("WandRunes enabled! Magic is alive.");
    }

    @Override
    public void onDisable() {
        if (manaManager != null) manaManager.saveAll();
        if (wandManager != null) wandManager.saveAll();
        if (runeManager != null) runeManager.saveAll();
        getLogger().info("WandRunes disabled. Magic stored safely.");
    }

    public static WandRunes getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public ManaManager getManaManager() { return manaManager; }
    public WandManager getWandManager() { return wandManager; }
    public RuneManager getRuneManager() { return runeManager; }
    public DropManager getDropManager() { return dropManager; }
    public WandRegistry getWandRegistry() { return wandRegistry; }
}
