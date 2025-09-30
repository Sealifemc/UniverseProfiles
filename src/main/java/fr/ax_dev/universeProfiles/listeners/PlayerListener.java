package fr.ax_dev.universeProfiles.listeners;

import fr.ax_dev.universeProfiles.UniverseProfiles;
import fr.ax_dev.universeProfiles.gui.ProfileGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private final UniverseProfiles plugin;

    public PlayerListener(UniverseProfiles plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getProfileManager().getProfile(event.getPlayer().getUniqueId()).thenAccept(profile -> {
            profile.updateFromPlayer(event.getPlayer());
            plugin.getProfileManager().saveProfile(profile);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getInventoryBackupManager().hasBackup(player.getUniqueId())) {
            player.closeInventory();
            plugin.getInventoryBackupManager().restore(player);
        }

        plugin.getProfileManager().updateProfile(player);
        plugin.getPlaceholderCacheManager().cachePlaceholders(player.getUniqueId(), player);

        if (plugin.getConfigManager().getBoolean("settings.cache_offline_players", true)) {
            return;
        }

        plugin.getProfileManager().unloadProfile(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!plugin.getInventoryBackupManager().hasBackup(player.getUniqueId())) {
            return;
        }

        ItemStack[] backupContents = plugin.getInventoryBackupManager().getContents(player.getUniqueId());
        ItemStack[] backupArmor = plugin.getInventoryBackupManager().getArmor(player.getUniqueId());
        ItemStack backupOffhand = plugin.getInventoryBackupManager().getOffhand(player.getUniqueId());

        plugin.getInventoryBackupManager().clearBackup(player.getUniqueId());

        event.getDrops().clear();
        player.getInventory().clear();

        boolean keepInv = event.getKeepInventory();

        if (keepInv) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (backupContents != null) player.getInventory().setContents(backupContents);
                if (backupArmor != null) player.getInventory().setArmorContents(backupArmor);
                if (backupOffhand != null) player.getInventory().setItemInOffHand(backupOffhand);
            });
        } else {
            if (backupContents != null) {
                for (ItemStack item : backupContents) {
                    if (item != null && item.getType() != Material.AIR) {
                        event.getDrops().add(item);
                    }
                }
            }
            if (backupArmor != null) {
                for (ItemStack item : backupArmor) {
                    if (item != null && item.getType() != Material.AIR) {
                        event.getDrops().add(item);
                    }
                }
            }
            if (backupOffhand != null && backupOffhand.getType() != Material.AIR) {
                event.getDrops().add(backupOffhand);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof Player target)) return;

        Player viewer = event.getPlayer();

        if (!isCorrectClickType(viewer)) return;
        if (!viewer.hasPermission("uprofiles.open")) return;

        event.setCancelled(true);
        openProfileForTarget(viewer, target);
    }

    private boolean isCorrectClickType(Player player) {
        String clickType = plugin.getConfigManager().getString("settings.open_on_player_click", "SHIFT_RIGHT");

        return switch (clickType.toUpperCase()) {
            case "SHIFT_RIGHT" -> player.isSneaking();
            case "RIGHT" -> true;
            case "DISABLED", "NONE" -> false;
            default -> player.isSneaking();
        };
    }

    private void openProfileForTarget(Player viewer, Player target) {
        plugin.getProfileManager().getProfile(target.getUniqueId()).thenAccept(profile -> {
            if (profile == null) return;

            if (!profile.isPublic() && !viewer.hasPermission("uprofiles.private.bypass")
                    && !viewer.getUniqueId().equals(target.getUniqueId())) {
                String msg = plugin.getLanguageManager().getMessage("profile.private");
                viewer.sendMessage(msg);
                return;
            }

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                new ProfileGUI(plugin, viewer, profile).open();
            });
        });
    }
}
