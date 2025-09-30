package fr.ax_dev.universeProfiles.config;

import fr.ax_dev.universeProfiles.UniverseProfiles;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    private final UniverseProfiles plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacySection();
    private FileConfiguration languageConfig;
    private String currentLanguage;
    private static Boolean isPaperServer = null;

    public LanguageManager(UniverseProfiles plugin) {
        this.plugin = plugin;
        loadLanguage();
    }

    private void loadLanguage() {
        currentLanguage = plugin.getConfigManager().getString("language", "EN_US");
        File languageFile = new File(plugin.getDataFolder(), "languages/" + currentLanguage + ".yml");

        if (!languageFile.exists()) {
            plugin.saveResource("languages/" + currentLanguage + ".yml", false);
        }

        languageConfig = YamlConfiguration.loadConfiguration(languageFile);

        InputStream defConfigStream = plugin.getResource("languages/" + currentLanguage + ".yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            languageConfig.setDefaults(defConfig);
        }
    }

    public void reload() {
        try {
            loadLanguage();
        } catch (Exception e) {
            plugin.getLogger().severe("§cErreur lors du rechargement du fichier de langue: " + e.getMessage());
            throw new RuntimeException("Erreur lors du rechargement du fichier de langue", e);
        }
    }

    public String getMessage(String path) {
        return languageConfig.getString(path, "Message not found: " + path);
    }

    public String getMessage(String path, Map<String, String> placeholders) {
        String message = getMessage(path);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return message;
    }

    public Component getComponent(String path) {
        return miniMessage.deserialize(getMessage(path));
    }

    public Component getComponent(String path, Map<String, String> placeholders) {
        return miniMessage.deserialize(getMessage(path, placeholders));
    }

    public void sendMessage(Player player, String path) {
        String message = getMessage("prefix") + getMessage(path);
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        Component component = miniMessage.deserialize(message);
        sendComponentMessage(player, component);
    }

    public void sendMessage(Player player, String path, Map<String, String> placeholders) {
        String message = getMessage("prefix") + getMessage(path, placeholders);
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        Component component = miniMessage.deserialize(message);
        sendComponentMessage(player, component);
    }

    private void sendComponentMessage(Player player, Component component) {
        if (isPaper()) {
            try {
                Method method = Player.class.getMethod("sendMessage", Component.class);
                method.invoke(player, component);
                return;
            } catch (Exception ignored) {
            }
        }
        String legacyMessage = legacySerializer.serialize(component);
        player.sendMessage(legacyMessage);
    }

    private static boolean isPaper() {
        if (isPaperServer == null) {
            try {
                Method method = Player.class.getMethod("sendMessage", Component.class);
                isPaperServer = true;
            } catch (NoSuchMethodException e) {
                isPaperServer = false;
            }
        }
        return isPaperServer;
    }
}