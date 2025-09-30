package fr.ax_dev.universeProfiles.config;

import fr.ax_dev.universeProfiles.UniverseProfiles;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final UniverseProfiles plugin;
    private FileConfiguration config;

    public ConfigManager(UniverseProfiles plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public void reload() {
        try {
            plugin.reloadConfig();
            config = plugin.getConfig();
        } catch (Exception e) {
            plugin.getLogger().severe("§cErreur lors du rechargement de config.yml: " + e.getMessage());
            throw new RuntimeException("Erreur lors du rechargement de config.yml", e);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public java.util.List<String> getStringList(String path) {
        return config.getStringList(path);
    }
}