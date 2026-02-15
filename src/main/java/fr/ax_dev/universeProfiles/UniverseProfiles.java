package fr.ax_dev.universeProfiles;

import fr.ax_dev.universeProfiles.cache.PlaceholderCacheManager;
import fr.ax_dev.universeProfiles.commands.ProfileCommand;
import fr.ax_dev.universeProfiles.conditions.ConditionManager;
import fr.ax_dev.universeProfiles.config.ConfigManager;
import fr.ax_dev.universeProfiles.config.GuiConfigManager;
import fr.ax_dev.universeProfiles.config.LanguageManager;
import fr.ax_dev.universeProfiles.database.DatabaseManager;
import fr.ax_dev.universeProfiles.database.DatabaseType;
import fr.ax_dev.universeProfiles.integrations.ECosmeticsIntegration;
import fr.ax_dev.universeProfiles.listeners.PlayerListener;
import fr.ax_dev.universeProfiles.managers.InventoryBackupManager;
import fr.ax_dev.universeProfiles.managers.ProfileManager;
import fr.ax_dev.universeProfiles.managers.SoundManager;
import fr.ax_dev.universeProfiles.placeholders.ProfilePlaceholderExpansion;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class UniverseProfiles extends JavaPlugin {

    private static UniverseProfiles instance;
    private ConfigManager configManager;
    private GuiConfigManager guiConfigManager;
    private LanguageManager languageManager;
    private DatabaseManager databaseManager;
    private ProfileManager profileManager;
    private PlaceholderCacheManager placeholderCacheManager;
    private ConditionManager conditionManager;
    private InventoryBackupManager inventoryBackupManager;
    private SoundManager soundManager;
    private ECosmeticsIntegration eCosmeticsIntegration;

    @Override
    public void onEnable() {
        instance = this;

        int pluginId = 28327;
        new Metrics(this, pluginId);

        loadManagers();
        registerCommands();
        registerListeners();
        registerPlaceholders();
        registerIntegrations();

        getLogger().info("UniverseProfiles enabled!");
    }

    @Override
    public void onDisable() {
        if (inventoryBackupManager != null && inventoryBackupManager.getBackupCount() > 0) {
            getLogger().info("Restoring " + inventoryBackupManager.getBackupCount() + " player inventories...");
            inventoryBackupManager.restoreAll();
        }

        if (profileManager != null) {
            profileManager.saveAllProfiles();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (placeholderCacheManager != null) {
            placeholderCacheManager.close();
        }

        if (databaseManager != null) {
            databaseManager.close();
        }

        getLogger().info("UniverseProfiles disabled!");
    }

    private void loadManagers() {
        configManager = new ConfigManager(this);
        guiConfigManager = new GuiConfigManager(this);
        languageManager = new LanguageManager(this);

        DatabaseType dbType = DatabaseType.valueOf(
            configManager.getConfig().getString("database.type", "SQLITE").toUpperCase()
        );

        databaseManager = new DatabaseManager(this, dbType);
        databaseManager.initialize();

        profileManager = new ProfileManager(this);
        placeholderCacheManager = new PlaceholderCacheManager(this);
        conditionManager = new ConditionManager(this);
        inventoryBackupManager = new InventoryBackupManager(this);
        soundManager = new SoundManager(this);
    }

    private void registerCommands() {
        ProfileCommand profileCommand = new ProfileCommand(this);
        getCommand("profile").setExecutor(profileCommand);
        getCommand("profile").setTabCompleter(profileCommand);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private void registerPlaceholders() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ProfilePlaceholderExpansion(this).register();
        }
    }

    private void registerIntegrations() {
        String cosmeticPlugin = getConfig().getString("settings.cosmetics_plugin", "NONE");

        if (cosmeticPlugin.equalsIgnoreCase("ECOSMETICS")) {
            eCosmeticsIntegration = new ECosmeticsIntegration(this);
        } else {
            getLogger().info("No cosmetics plugin integration enabled");
        }
    }

    public void reload() {
        configManager.reload();
        guiConfigManager.reload();
        languageManager.reload();
        if (profileManager != null) {
            profileManager.clearCache();
        }
        registerIntegrations();
    }

    public static UniverseProfiles getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public GuiConfigManager getGuiConfigManager() {
        return guiConfigManager;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public ECosmeticsIntegration getECosmeticsIntegration() {
        return eCosmeticsIntegration;
    }

    public PlaceholderCacheManager getPlaceholderCacheManager() {
        return placeholderCacheManager;
    }

    public ConditionManager getConditionManager() {
        return conditionManager;
    }

    public InventoryBackupManager getInventoryBackupManager() {
        return inventoryBackupManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }
}
