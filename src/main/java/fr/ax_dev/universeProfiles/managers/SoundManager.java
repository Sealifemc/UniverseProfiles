package fr.ax_dev.universeProfiles.managers;

import fr.ax_dev.universeProfiles.UniverseProfiles;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SoundManager {

    private final UniverseProfiles plugin;

    public SoundManager(UniverseProfiles plugin) {
        this.plugin = plugin;
    }

    public void playOpenSound(Player player) {
        ConfigurationSection soundConfig = plugin.getGuiConfigManager().getConfig().getConfigurationSection("sounds.open");
        if (soundConfig == null || !soundConfig.getBoolean("enabled", true)) {
            return;
        }
        playSound(player, soundConfig);
    }

    public void playClickSound(Player player) {
        ConfigurationSection soundConfig = plugin.getGuiConfigManager().getConfig().getConfigurationSection("sounds.click");
        if (soundConfig == null || !soundConfig.getBoolean("enabled", false)) {
            return;
        }
        playSound(player, soundConfig);
    }

    public void playSound(Player player, ConfigurationSection config) {
        if (config == null) return;
        String soundName = config.getString("sound", "UI_BUTTON_CLICK");
        float volume = (float) config.getDouble("volume", 1.0);
        float pitch = (float) config.getDouble("pitch", 1.0);
        playSound(player, soundName, volume, pitch);
    }

    public void playSound(Player player, String soundName, float volume, float pitch) {
        if (player == null || soundName == null || soundName.isEmpty()) return;
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            try {
                player.playSound(player.getLocation(), soundName.toLowerCase(), volume, pitch);
            } catch (Exception ex) {
                if (plugin.getConfig().getBoolean("settings.debug", false)) {
                    plugin.getLogger().warning("Invalid sound: " + soundName);
                }
            }
        }
    }

    public void playSoundFromAction(Player player, ConfigurationSection actionSection) {
        if (actionSection == null) return;
        String soundName = actionSection.getString("sound", "UI_BUTTON_CLICK");
        float volume = (float) actionSection.getDouble("volume", 1.0);
        float pitch = (float) actionSection.getDouble("pitch", 1.0);
        playSound(player, soundName, volume, pitch);
    }
}
