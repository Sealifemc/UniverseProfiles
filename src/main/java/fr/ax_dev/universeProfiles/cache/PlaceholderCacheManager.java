package fr.ax_dev.universeProfiles.cache;

import fr.ax_dev.universeProfiles.UniverseProfiles;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.*;

public class PlaceholderCacheManager {

    private final UniverseProfiles plugin;
    private Connection connection;
    private final List<String> cachedPlaceholders;

    public PlaceholderCacheManager(UniverseProfiles plugin) {
        this.plugin = plugin;
        this.cachedPlaceholders = plugin.getConfig().getStringList("settings.cached_placeholders");
        initialize();
    }

    private void initialize() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            File dbFile = new File(dataFolder, "placeholder_cache.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS placeholder_cache (" +
                        "uuid TEXT NOT NULL, " +
                        "placeholder TEXT NOT NULL, " +
                        "value TEXT, " +
                        "updated_at INTEGER, " +
                        "PRIMARY KEY (uuid, placeholder))");
            }

            plugin.getLogger().info("PlaceholderCacheManager initialized with " + cachedPlaceholders.size() + " placeholders");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize placeholder cache: " + e.getMessage());
        }
    }

    public void cachePlaceholders(UUID uuid, Player player) {
        if (cachedPlaceholders.isEmpty()) return;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return;

        Map<String, String> values = new HashMap<>();
        for (String placeholder : cachedPlaceholders) {
            String value = PlaceholderAPI.setPlaceholders(player, placeholder);
            values.put(placeholder, value);
        }

        savePlaceholders(uuid, values);
    }

    private void savePlaceholders(UUID uuid, Map<String, String> values) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO placeholder_cache (uuid, placeholder, value, updated_at) VALUES (?, ?, ?, ?)")) {

            long timestamp = System.currentTimeMillis();
            for (Map.Entry<String, String> entry : values.entrySet()) {
                stmt.setString(1, uuid.toString());
                stmt.setString(2, entry.getKey());
                stmt.setString(3, entry.getValue());
                stmt.setLong(4, timestamp);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save placeholders: " + e.getMessage());
        }
    }

    public String getPlaceholder(UUID uuid, String placeholder, Player fallbackPlayer) {
        String cached = getCachedPlaceholder(uuid, placeholder);

        if (cached != null) {
            return cached;
        }

        if (fallbackPlayer != null && fallbackPlayer.isOnline() &&
            Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(fallbackPlayer, placeholder);
        }

        return placeholder;
    }

    private String getCachedPlaceholder(UUID uuid, String placeholder) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT value FROM placeholder_cache WHERE uuid = ? AND placeholder = ?")) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, placeholder);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get cached placeholder: " + e.getMessage());
        }
        return null;
    }

    public void clearCache(UUID uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM placeholder_cache WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to clear cache: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to close placeholder cache: " + e.getMessage());
        }
    }
}
