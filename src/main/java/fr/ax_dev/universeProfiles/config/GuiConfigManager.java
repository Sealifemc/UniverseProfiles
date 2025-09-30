package fr.ax_dev.universeProfiles.config;

import fr.ax_dev.universeProfiles.UniverseProfiles;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class GuiConfigManager {

    private final UniverseProfiles plugin;
    private FileConfiguration guiConfig;
    private File guiFile;

    public GuiConfigManager(UniverseProfiles plugin) {
        this.plugin = plugin;
        loadGuiConfig();
    }

    private void loadGuiConfig() {
        guiFile = new File(plugin.getDataFolder(), "gui.yml");

        if (!guiFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                InputStream inputStream = plugin.getResource("gui.yml");
                if (inputStream != null) {
                    Files.copy(inputStream, guiFile.toPath());
                    plugin.getLogger().info("Created gui.yml from resources");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create gui.yml file: " + e.getMessage());
            }
        }

        guiConfig = YamlConfiguration.loadConfiguration(guiFile);
        plugin.getLogger().info("Loaded gui.yml with " + guiConfig.getKeys(false).size() + " root keys");
    }

    public void reload() {
        try {
            guiConfig = YamlConfiguration.loadConfiguration(guiFile);
        } catch (Exception e) {
            plugin.getLogger().severe("§cErreur lors du rechargement de gui.yml: " + e.getMessage());
            throw new RuntimeException("Erreur lors du rechargement de gui.yml", e);
        }
    }

    public FileConfiguration getConfig() {
        return guiConfig;
    }

    public String getTitle() {
        return guiConfig.getString("title", "Profile");
    }

    public ConfigurationSection getTitles() {
        return guiConfig.getConfigurationSection("titles");
    }

    public String getDefaultTitle() {
        ConfigurationSection titles = getTitles();
        if (titles != null && titles.contains("default")) {
            return titles.getString("default", getTitle());
        }
        return getTitle();
    }

    public int getSize() {
        return guiConfig.getInt("size", 54);
    }

    public ConfigurationSection getEquipments() {
        return guiConfig.getConfigurationSection("equipments");
    }

    public ConfigurationSection getCustomItems() {
        return guiConfig.getConfigurationSection("customs_items");
    }

    public ConfigurationSection getCustomItem(String itemName) {
        ConfigurationSection customs = getCustomItems();
        return customs != null ? customs.getConfigurationSection(itemName) : null;
    }

    public void updateFromResources() {
        if (guiFile.exists()) {
            guiFile.delete();
            plugin.getLogger().info("Deleted old gui.yml");
        }
        loadGuiConfig();
    }

    public String getString(String path, String def) {
        return guiConfig.getString(path, def);
    }

    public int getInt(String path, int def) {
        return guiConfig.getInt(path, def);
    }

    public List<String> getStringList(String path) {
        return guiConfig.getStringList(path);
    }
}