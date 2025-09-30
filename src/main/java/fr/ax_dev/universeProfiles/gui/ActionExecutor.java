package fr.ax_dev.universeProfiles.gui;

import fr.ax_dev.universeProfiles.UniverseProfiles;
import fr.ax_dev.universeProfiles.models.PlayerProfile;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class ActionExecutor {

    private final UniverseProfiles plugin;
    private final PlayerProfile profile;
    private final Player player;

    public ActionExecutor(UniverseProfiles plugin, PlayerProfile profile, Player player) {
        this.plugin = plugin;
        this.profile = profile;
        this.player = player;
    }

    public void executeActions(ConfigurationSection clickActions) {
        for (String actionKey : clickActions.getKeys(false)) {
            ConfigurationSection actionSection = clickActions.getConfigurationSection(actionKey);
            if (actionSection == null) continue;

            String action = actionSection.getString("action", "NONE");
            executeAction(action, actionSection);
        }
    }

    private void executeAction(String action, ConfigurationSection actionSection) {
        switch (action) {
            case "ADD_STAR" -> plugin.getProfileManager().giveStar(player, profile.getUuid());
            case "REMOVE_STAR" -> {}
            case "STATE_TOGGLE", "PRIVACY" -> togglePrivacy();
            case "PUBLIC" -> setPublic(true);
            case "PRIVATE" -> setPublic(false);
            case "COMMAND" -> executeCommands(actionSection.getStringList("commands"), true);
            case "COMMAND_PLAYER" -> executeCommands(actionSection.getStringList("commands"), false);
            case "CLOSE" -> player.closeInventory();
            case "SOUND" -> plugin.getSoundManager().playSoundFromAction(player, actionSection);
        }
    }

    private void togglePrivacy() {
        if (player.getUniqueId().equals(profile.getUuid())) {
            plugin.getProfileManager().togglePrivacy(player);
        }
    }

    private void setPublic(boolean isPublic) {
        if (player.getUniqueId().equals(profile.getUuid())) {
            profile.setPublic(isPublic);
        }
    }

    private void executeCommands(List<String> commands, boolean asConsole) {
        for (String command : commands) {
            command = command.replace("%player_name%", profile.getPlayerName())
                    .replace("%viewer_name%", player.getName());

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                command = PlaceholderAPI.setPlaceholders(player, command);
            }

            if (asConsole) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            } else {
                player.performCommand(command);
            }
        }
    }
}
