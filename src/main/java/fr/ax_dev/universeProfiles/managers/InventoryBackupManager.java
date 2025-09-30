package fr.ax_dev.universeProfiles.managers;

import fr.ax_dev.universeProfiles.UniverseProfiles;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryBackupManager {

    private final UniverseProfiles plugin;
    private final Map<UUID, ItemStack[]> inventoryBackups = new ConcurrentHashMap<>();
    private final Map<UUID, ItemStack[]> armorBackups = new ConcurrentHashMap<>();
    private final Map<UUID, ItemStack> offhandBackups = new ConcurrentHashMap<>();

    public InventoryBackupManager(UniverseProfiles plugin) {
        this.plugin = plugin;
    }

    public void backupAndClear(Player player) {
        UUID uuid = player.getUniqueId();

        if (hasBackup(uuid)) {
            plugin.getLogger().warning("Player " + player.getName() + " already has a backup, restoring first");
            restore(player);
        }

        inventoryBackups.put(uuid, player.getInventory().getContents().clone());
        armorBackups.put(uuid, player.getInventory().getArmorContents().clone());
        offhandBackups.put(uuid, player.getInventory().getItemInOffHand().clone());

        player.getInventory().clear();

        if (plugin.getConfigManager().getBoolean("settings.debug", false)) {
            plugin.getLogger().info("[Backup] Saved inventory for " + player.getName());
        }
    }

    public void restore(Player player) {
        UUID uuid = player.getUniqueId();

        if (!hasBackup(uuid)) {
            return;
        }

        player.getInventory().clear();

        ItemStack[] contents = inventoryBackups.remove(uuid);
        ItemStack[] armor = armorBackups.remove(uuid);
        ItemStack offhand = offhandBackups.remove(uuid);

        if (contents != null) {
            player.getInventory().setContents(contents);
        }
        if (armor != null) {
            player.getInventory().setArmorContents(armor);
        }
        if (offhand != null) {
            player.getInventory().setItemInOffHand(offhand);
        }

        player.updateInventory();

        if (plugin.getConfigManager().getBoolean("settings.debug", false)) {
            plugin.getLogger().info("[Backup] Restored inventory for " + player.getName());
        }
    }

    public boolean hasBackup(UUID uuid) {
        return inventoryBackups.containsKey(uuid);
    }

    public void restoreAll() {
        for (UUID uuid : inventoryBackups.keySet()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                restore(player);
            }
        }
        inventoryBackups.clear();
        armorBackups.clear();
        offhandBackups.clear();
    }

    public int getBackupCount() {
        return inventoryBackups.size();
    }

    public ItemStack[] getContents(UUID uuid) {
        return inventoryBackups.get(uuid);
    }

    public ItemStack[] getArmor(UUID uuid) {
        return armorBackups.get(uuid);
    }

    public ItemStack getOffhand(UUID uuid) {
        return offhandBackups.get(uuid);
    }

    public void clearBackup(UUID uuid) {
        inventoryBackups.remove(uuid);
        armorBackups.remove(uuid);
        offhandBackups.remove(uuid);
    }
}
