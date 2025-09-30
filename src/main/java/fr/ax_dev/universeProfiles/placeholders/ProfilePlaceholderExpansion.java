package fr.ax_dev.universeProfiles.placeholders;

import fr.ax_dev.universeProfiles.UniverseProfiles;
import fr.ax_dev.universeProfiles.models.PlayerProfile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class ProfilePlaceholderExpansion extends PlaceholderExpansion {

    private final UniverseProfiles plugin;

    public ProfilePlaceholderExpansion(UniverseProfiles plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "uprofiles";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        PlayerProfile profile = plugin.getProfileManager().getCachedProfile(player.getUniqueId());
        if (profile == null) {
            return "";
        }

        return switch (identifier.toLowerCase()) {
            case "state" -> {
                if (profile.isPublic()) {
                    yield plugin.getLanguageManager().getMessage("privacy.public");
                } else {
                    yield plugin.getLanguageManager().getMessage("privacy.private");
                }
            }
            case "star", "stars" -> String.valueOf(profile.getStars());
            case "ispublic" -> String.valueOf(profile.isPublic());
            case "isprivate" -> String.valueOf(!profile.isPublic());
            case "player_name" -> profile.getPlayerName();
            default -> null;
        };
    }
}