package fr.ax_dev.universeProfiles.managers;

import fr.ax_dev.universeProfiles.UniverseProfiles;
import fr.ax_dev.universeProfiles.models.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

public class ProfileManager {

    private final UniverseProfiles plugin;
    private final Map<UUID, PlayerProfile> profileCache;

    public ProfileManager(UniverseProfiles plugin) {
        this.plugin = plugin;
        this.profileCache = new ConcurrentHashMap<>();
        startAutoSaveTask();
    }

    private void startAutoSaveTask() {
        int saveInterval = plugin.getConfigManager().getInt("settings.save_interval_minutes", 5) * 20 * 60;
        fr.ax_dev.universeProfiles.util.SchedulerUtil.runTaskTimerAsynchronously(plugin, this::saveAllProfiles, saveInterval, saveInterval);
    }

    public CompletableFuture<PlayerProfile> getProfile(UUID uuid) {
        if (profileCache.containsKey(uuid)) {
            return CompletableFuture.completedFuture(profileCache.get(uuid));
        }

        return plugin.getDatabaseManager().loadProfile(uuid)
                .thenApply(profile -> {
                    if (profile == null) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                        String name = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown";
                        profile = new PlayerProfile(uuid, name);
                    }
                    profileCache.put(uuid, profile);
                    return profile;
                });
    }

    public PlayerProfile getCachedProfile(UUID uuid) {
        return profileCache.get(uuid);
    }

    public void updateProfile(Player player) {
        getProfile(player.getUniqueId()).thenAccept(profile -> {
            profile.updateFromPlayer(player);
            saveProfile(profile);
        });
    }

    public void saveProfile(PlayerProfile profile) {
        plugin.getDatabaseManager().saveProfile(profile);
    }

    public void saveAllProfiles() {
        profileCache.values().forEach(this::saveProfile);
    }

    public void unloadProfile(UUID uuid) {
        PlayerProfile profile = profileCache.remove(uuid);
        if (profile != null) {
            saveProfile(profile);
        }
    }

    public boolean canViewProfile(Player viewer, UUID targetUuid) {
        if (viewer.getUniqueId().equals(targetUuid)) {
            return true;
        }

        if (viewer.hasPermission("uprofiles.private.bypass")) {
            return true;
        }

        PlayerProfile profile = profileCache.get(targetUuid);
        if (profile != null) {
            return profile.isPublic();
        }

        return true;
    }

    public void togglePrivacy(Player player) {
        getProfile(player.getUniqueId()).thenAccept(profile -> {
            profile.setPublic(!profile.isPublic());
            saveProfile(profile);

            String messageKey = profile.isPublic() ? "messages.profile_now_public" : "messages.profile_now_private";
            plugin.getLanguageManager().sendMessage(player, messageKey);
        });
    }

    public void setPrivacy(UUID uuid, boolean isPublic) {
        getProfile(uuid).thenAccept(profile -> {
            profile.setPublic(isPublic);
            saveProfile(profile);
        });
    }

    public void giveStar(Player giver, UUID receiver) {
        if (giver.getUniqueId().equals(receiver)) {
            if (!plugin.getConfigManager().getBoolean("settings.allow_self_star", false)) {
                plugin.getLanguageManager().sendMessage(giver, "messages.cannot_star_self");
                return;
            }
        }

        getProfile(receiver).thenAccept(profile -> {
            if (!profile.canReceiveStarFrom(giver.getUniqueId())) {
                plugin.getLanguageManager().sendMessage(giver, "messages.already_starred");
                return;
            }

            profile.addStar(giver.getUniqueId());
            saveProfile(profile);

            Map<String, String> placeholders = Map.of("%player%", profile.getPlayerName());
            plugin.getLanguageManager().sendMessage(giver, "messages.star_given", placeholders);

            Player target = Bukkit.getPlayer(receiver);
            if (target != null && target.isOnline()) {
                Map<String, String> targetPlaceholders = Map.of("%player%", giver.getName());
                plugin.getLanguageManager().sendMessage(target, "messages.star_received", targetPlaceholders);
            }
        });
    }

    public void addStars(UUID uuid, int amount) {
        getProfile(uuid).thenAccept(profile -> {
            profile.setStars(profile.getStars() + amount);
            saveProfile(profile);
        });
    }

    public void clearCache() {
        saveAllProfiles();
        profileCache.clear();
    }
}