package fr.ax_dev.universeProfiles.integrations;

import ru.sculmix.ecosmetics.api.Cosmetic;
import ru.sculmix.ecosmetics.api.CosmeticType;
import ru.sculmix.ecosmetics.api.MagicAPI;
import ru.sculmix.ecosmetics.cache.PlayerData;
import ru.sculmix.ecosmetics.events.CosmeticEquipEvent;
import ru.sculmix.ecosmetics.events.CosmeticUnEquipEvent;
import fr.ax_dev.universeProfiles.UniverseProfiles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ECosmeticsIntegration implements Listener {

    private final UniverseProfiles plugin;
    private boolean enabled = false;

    public ECosmeticsIntegration(UniverseProfiles plugin) {
        this.plugin = plugin;

        if (Bukkit.getPluginManager().getPlugin("ECosmetics") != null) {
            enabled = true;
            Bukkit.getPluginManager().registerEvents(this, plugin);
            plugin.getLogger().info("ECosmetics integration enabled!");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<Cosmetic> getPlayerCosmetics(Player player) {
        if (!enabled) return new ArrayList<>();

        try {
            PlayerData playerData = PlayerData.getPlayer(player);
            if (playerData == null) return new ArrayList<>();

            Map<String, Cosmetic> cosmetics = playerData.getCosmetics();
            return new ArrayList<>(cosmetics.values());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<String> getEquippedCosmetics(Player player) {
        if (!enabled) return new ArrayList<>();

        List<String> equipped = new ArrayList<>();
        try {
            PlayerData playerData = PlayerData.getPlayer(player);
            if (playerData == null) return equipped;

            if (playerData.getHat() != null) {
                equipped.add(playerData.getHat().getId());
            }
            if (playerData.getBag() != null) {
                equipped.add(playerData.getBag().getId());
            }
            if (playerData.getWStick() != null) {
                equipped.add(playerData.getWStick().getId());
            }
            if (playerData.getBalloon() != null) {
                equipped.add(playerData.getBalloon().getId());
            }
            if (playerData.getSpray() != null) {
                equipped.add(playerData.getSpray().getId());
            }
        } catch (Exception e) {
        }
        return equipped;
    }

    public Map<String, List<String>> getCosmeticsByType(Player player) {
        if (!enabled) return new HashMap<>();

        Map<String, List<String>> cosmeticsByType = new HashMap<>();

        try {
            for (Cosmetic cosmetic : getPlayerCosmetics(player)) {
                String type = cosmetic.getCosmeticType().name();
                cosmeticsByType.computeIfAbsent(type, k -> new ArrayList<>()).add(cosmetic.getId());
            }
        } catch (Exception e) {
        }

        return cosmeticsByType;
    }

    public boolean hasCosmetic(Player player, String cosmeticId) {
        if (!enabled) return false;

        try {
            return MagicAPI.hasCosmetic(player, cosmeticId);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean equipCosmetic(Player player, String cosmeticId) {
        if (!enabled) return false;

        try {
            Cosmetic cosmetic = Cosmetic.getCosmetic(cosmeticId);
            if (cosmetic == null) return false;

            if (!hasCosmetic(player, cosmeticId)) return false;

            MagicAPI.EquipCosmetic(player, cosmeticId, null, false);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean unequipCosmetic(Player player, String cosmeticId) {
        if (!enabled) return false;

        try {
            Cosmetic cosmetic = Cosmetic.getCosmetic(cosmeticId);
            if (cosmetic == null) return false;

            MagicAPI.UnEquipCosmetic(player, cosmetic.getCosmeticType());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @EventHandler
    public void onCosmeticEquip(CosmeticEquipEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            plugin.getProfileManager().updateProfile(player);
        }
    }

    @EventHandler
    public void onCosmeticUnequip(CosmeticUnEquipEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            plugin.getProfileManager().updateProfile(player);
        }
    }

    public int getTotalCosmeticsCount(Player player) {
        if (!enabled) return 0;

        return getPlayerCosmetics(player).size();
    }

    public String getCosmeticDisplayName(String cosmeticId) {
        if (!enabled) return cosmeticId;

        try {
            Cosmetic cosmetic = Cosmetic.getCosmetic(cosmeticId);
            if (cosmetic == null) return cosmeticId;

            return cosmetic.getName();
        } catch (Exception e) {
            return cosmeticId;
        }
    }

    private String convertSlotName(String slotName) {
        switch (slotName.toUpperCase()) {
            case "HELMET": return "HAT";
            case "BACKPACK": return "BAG";
            case "OFFHAND": return "WALKING_STICK";
            default: return slotName;
        }
    }

    public ItemStack getCosmeticItemBySlot(Player player, String slotName) {
        if (!enabled) return null;

        boolean debug = plugin.getConfig().getBoolean("settings.debug", false);
        String convertedSlot = convertSlotName(slotName);

        try {
            PlayerData playerData = PlayerData.getPlayer(player);
            if (playerData == null) {
                if (debug) plugin.getLogger().info("[ECosmetics] PlayerData is null for player: " + player.getName());
                return null;
            }

            CosmeticType type = null;
            try {
                type = CosmeticType.valueOf(convertedSlot);
            } catch (IllegalArgumentException e) {
                if (debug) plugin.getLogger().info("[ECosmetics] Invalid slot name: " + slotName + " (converted: " + convertedSlot + ")");
                return null;
            }

            Cosmetic cosmetic = null;
            switch (type) {
                case HAT:
                    cosmetic = playerData.getHat();
                    break;
                case BAG:
                    cosmetic = playerData.getBag();
                    break;
                case WALKING_STICK:
                    cosmetic = playerData.getWStick();
                    break;
                case BALLOON:
                    cosmetic = playerData.getBalloon();
                    break;
                case SPRAY:
                    cosmetic = playerData.getSpray();
                    break;
            }

            if (cosmetic == null) {
                if (debug) plugin.getLogger().info("[ECosmetics] No cosmetic equipped for slot: " + slotName);
                return null;
            }

            ItemStack itemStack = cosmetic.getItemStack();
            if (debug) plugin.getLogger().info("[ECosmetics] Found cosmetic: " + cosmetic.getId() + " for slot: " + slotName);
            return itemStack;
        } catch (Exception e) {
            plugin.getLogger().warning("[ECosmetics] Failed to get cosmetic: " + e.getMessage());
            return null;
        }
    }
}
