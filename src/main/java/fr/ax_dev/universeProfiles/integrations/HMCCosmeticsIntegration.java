package fr.ax_dev.universeProfiles.integrations;

import com.hibiscusmc.hmccosmetics.api.HMCCosmeticsAPI;
import com.hibiscusmc.hmccosmetics.api.events.PlayerCosmeticEquipEvent;
import com.hibiscusmc.hmccosmetics.api.events.PlayerCosmeticRemoveEvent;
import com.hibiscusmc.hmccosmetics.cosmetic.Cosmetic;
import com.hibiscusmc.hmccosmetics.user.CosmeticUser;
import fr.ax_dev.universeProfiles.UniverseProfiles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class HMCCosmeticsIntegration implements Listener {

    private final UniverseProfiles plugin;
    private boolean enabled = false;

    public HMCCosmeticsIntegration(UniverseProfiles plugin) {
        this.plugin = plugin;

        if (Bukkit.getPluginManager().getPlugin("HMCCosmetics") != null) {
            enabled = true;
            Bukkit.getPluginManager().registerEvents(this, plugin);
            plugin.getLogger().info("HMCCosmetics integration enabled!");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<Cosmetic> getPlayerCosmetics(Player player) {
        if (!enabled) return new ArrayList<>();

        CosmeticUser user = HMCCosmeticsAPI.getUser(player.getUniqueId());
        if (user == null) return new ArrayList<>();

        try {
            Object cosmetics = user.getClass().getMethod("getCosmetics").invoke(user);
            if (cosmetics instanceof Map) {
                return new ArrayList<>(((Map<String, Cosmetic>) cosmetics).values());
            } else if (cosmetics instanceof java.util.Collection) {
                return new ArrayList<>((java.util.Collection<Cosmetic>) cosmetics);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get cosmetics: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<String> getEquippedCosmetics(Player player) {
        if (!enabled) return new ArrayList<>();

        CosmeticUser user = HMCCosmeticsAPI.getUser(player.getUniqueId());
        if (user == null) return new ArrayList<>();

        List<String> equipped = new ArrayList<>();
        for (Cosmetic cosmetic : getPlayerCosmetics(player)) {
            equipped.add(cosmetic.getId());
        }
        return equipped;
    }

    public Map<String, List<String>> getCosmeticsByType(Player player) {
        if (!enabled) return new HashMap<>();

        CosmeticUser user = HMCCosmeticsAPI.getUser(player.getUniqueId());
        if (user == null) return new HashMap<>();

        Map<String, List<String>> cosmeticsByType = new HashMap<>();

        for (Cosmetic cosmetic : getPlayerCosmetics(player)) {
            String type = cosmetic.getSlot().name();
            cosmeticsByType.computeIfAbsent(type, k -> new ArrayList<>()).add(cosmetic.getId());
        }

        return cosmeticsByType;
    }

    public boolean hasCosmetic(Player player, String cosmeticId) {
        if (!enabled) return false;

        for (Cosmetic cosmetic : getPlayerCosmetics(player)) {
            if (cosmetic.getId().equals(cosmeticId)) {
                return true;
            }
        }
        return false;
    }

    public boolean equipCosmetic(Player player, String cosmeticId) {
        if (!enabled) return false;

        CosmeticUser user = HMCCosmeticsAPI.getUser(player.getUniqueId());
        if (user == null) return false;

        Cosmetic cosmetic = HMCCosmeticsAPI.getCosmetic(cosmeticId);
        if (cosmetic == null) return false;

        if (!hasCosmetic(player, cosmeticId)) return false;

        HMCCosmeticsAPI.equipCosmetic(user, cosmetic);
        return true;
    }

    public boolean unequipCosmetic(Player player, String cosmeticId) {
        if (!enabled) return false;

        CosmeticUser user = HMCCosmeticsAPI.getUser(player.getUniqueId());
        if (user == null) return false;

        Cosmetic cosmetic = HMCCosmeticsAPI.getCosmetic(cosmeticId);
        if (cosmetic == null) return false;

        HMCCosmeticsAPI.unequipCosmetic(user, cosmetic);
        return true;
    }

    @EventHandler
    public void onCosmeticEquip(PlayerCosmeticEquipEvent event) {
        Player player = event.getUser().getPlayer();
        if (player != null) {
            plugin.getProfileManager().updateProfile(player);
        }
    }

    @EventHandler
    public void onCosmeticRemove(PlayerCosmeticRemoveEvent event) {
        Player player = event.getUser().getPlayer();
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

        Cosmetic cosmetic = HMCCosmeticsAPI.getCosmetic(cosmeticId);
        if (cosmetic == null) return cosmeticId;

        return cosmetic.getDisplayName();
    }

    public ItemStack getCosmeticItemBySlot(Player player, String slotName) {
        if (!enabled) return null;

        boolean debug = plugin.getConfig().getBoolean("settings.debug", false);

        CosmeticUser user = HMCCosmeticsAPI.getUser(player.getUniqueId());
        if (user == null) {
            if (debug) plugin.getLogger().info("[HMC] CosmeticUser is null for player: " + player.getName());
            return null;
        }

        List<Cosmetic> cosmetics = getPlayerCosmetics(player);
        if (debug) plugin.getLogger().info("[HMC] Player " + player.getName() + " has " + cosmetics.size() + " cosmetics");

        for (Cosmetic cosmetic : cosmetics) {
            try {
                Object slot = cosmetic.getSlot();
                String slotNameStr = null;

                try {
                    slotNameStr = (String) slot.getClass().getMethod("name").invoke(slot);
                } catch (Exception e1) {
                    slotNameStr = slot.toString();
                }

                if (debug) plugin.getLogger().info("[HMC] Checking cosmetic: " + cosmetic.getId() + " with slot: " + slotNameStr);

                if (slotNameStr != null && slotNameStr.equals(slotName)) {
                    if (debug) plugin.getLogger().info("[HMC] Found matching cosmetic: " + cosmetic.getId());
                    try {
                        Object item = cosmetic.getClass().getMethod("getItem").invoke(cosmetic);
                        if (item instanceof ItemStack) {
                            ItemStack itemStack = (ItemStack) item;
                            if (debug) plugin.getLogger().info("[HMC] Cosmetic item type: " + itemStack.getType());
                            return itemStack;
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("[HMC] Failed to get cosmetic item: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("[HMC] Failed to process cosmetic: " + e.getMessage());
            }
        }

        if (debug) plugin.getLogger().info("[HMC] No cosmetic found for slot: " + slotName);
        return null;
    }
}