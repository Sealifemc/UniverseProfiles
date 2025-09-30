package fr.ax_dev.universeProfiles.gui;

import fr.ax_dev.universeProfiles.UniverseProfiles;
import fr.ax_dev.universeProfiles.integrations.ECosmeticsIntegration;
import fr.ax_dev.universeProfiles.integrations.HMCCosmeticsIntegration;
import fr.ax_dev.universeProfiles.models.PlayerProfile;
import fr.ax_dev.universeProfiles.util.InventoryUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final UniverseProfiles plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final PlayerProfile profile;
    private final Player viewer;

    public ItemBuilder(UniverseProfiles plugin, PlayerProfile profile, Player viewer) {
        this.plugin = plugin;
        this.profile = profile;
        this.viewer = viewer;
    }

    public ItemStack buildActionItem(ConfigurationSection item) {
        Material material = Material.valueOf(item.getString("material", "STONE"));
        String displayName = item.getString("display_name", "Custom Button");
        List<String> lore = item.getStringList("lore");
        boolean hideTooltip = item.getBoolean("hideTooltip", false);
        boolean hideAttributes = item.getBoolean("hideAttributes", false);
        String head = item.getString("head", "");
        int customModelData = item.getInt("custom_model_data", 0);
        String itemModel = item.getString("item_model", "");

        displayName = processPlaceholders(displayName);

        ItemStack actionItem = new ItemStack(material);
        ItemMeta meta = actionItem.getItemMeta();

        if (material == Material.PLAYER_HEAD && !head.isEmpty()) {
            SkullMeta skullMeta = (SkullMeta) meta;
            String processedHead = processPlaceholders(head);
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(processedHead));
            meta = skullMeta;
        }

        if (customModelData > 0) meta.setCustomModelData(customModelData);
        applyItemModel(meta, itemModel);

        if (hideAttributes) {
            try { meta.setHideTooltip(true); } catch (NoSuchMethodError ignored) {}
        }

        if (!hideTooltip) {
            InventoryUtil.setDisplayName(meta, miniMessage.deserialize("<!italic><white>" + displayName));
            List<Component> finalLore = new ArrayList<>();
            for (String line : lore) {
                String processed = processPlaceholders(line);
                for (String subLine : processed.split("\n")) {
                    finalLore.add(miniMessage.deserialize("<!italic><white>" + subLine));
                }
            }
            InventoryUtil.setLore(meta, finalLore);
        } else {
            meta.setHideTooltip(true);
        }

        actionItem.setItemMeta(meta);
        return actionItem;
    }

    public ItemStack buildEmptyItem(ConfigurationSection emptyConfig, String defaultName) {
        if (emptyConfig == null) {
            ItemStack emptySlot = new ItemStack(Material.BARRIER);
            ItemMeta meta = emptySlot.getItemMeta();
            InventoryUtil.setDisplayName(meta, miniMessage.deserialize("<!italic><white><gray>" + defaultName + "</gray>"));
            emptySlot.setItemMeta(meta);
            return emptySlot;
        }

        Material material = Material.valueOf(emptyConfig.getString("material", "BARRIER"));
        String displayName = emptyConfig.getString("display_name", "<gray>" + defaultName + "</gray>");
        List<String> lore = emptyConfig.getStringList("lore");
        int customModelData = emptyConfig.getInt("custom_model_data", 0);
        String itemModel = emptyConfig.getString("item_model", "");
        boolean hideTooltip = emptyConfig.getBoolean("hideTooltip", false);

        ItemStack emptySlot = new ItemStack(material);
        ItemMeta meta = emptySlot.getItemMeta();

        if (!hideTooltip) {
            InventoryUtil.setDisplayName(meta, miniMessage.deserialize("<!italic><white>" + displayName));
            if (!lore.isEmpty()) {
                List<Component> finalLore = new ArrayList<>();
                for (String line : lore) finalLore.add(miniMessage.deserialize("<!italic><white>" + line));
                InventoryUtil.setLore(meta, finalLore);
            }
        } else {
            try { meta.setHideTooltip(true); } catch (NoSuchMethodError ignored) {}
        }

        if (customModelData > 0) meta.setCustomModelData(customModelData);
        applyItemModel(meta, itemModel);

        emptySlot.setItemMeta(meta);
        return emptySlot;
    }

    private void applyItemModel(ItemMeta meta, String itemModel) {
        if (itemModel == null || itemModel.isEmpty()) return;
        try {
            NamespacedKey key = NamespacedKey.fromString(itemModel);
            if (key != null) meta.setItemModel(key);
        } catch (NoSuchMethodError ignored) {}
    }

    public String processPlaceholders(String text) {
        text = text.replace("%player_name%", profile.getPlayerName());
        text = text.replace("%uprofiles_star%", String.valueOf(profile.getStars()));
        text = text.replace("%uprofiles_bio%", formatBio(profile.getBio()));

        String state = profile.isPublic() ?
                plugin.getLanguageManager().getMessage("privacy.public") :
                plugin.getLanguageManager().getMessage("privacy.private");
        text = text.replace("%uprofiles_state%", state);

        Player targetPlayer = Bukkit.getPlayer(profile.getUuid());
        text = processCosmeticPlaceholders(text, targetPlayer);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            text = plugin.getPlaceholderCacheManager().getPlaceholder(profile.getUuid(), text, targetPlayer);
        }
        return text;
    }

    private String formatBio(String bio) {
        if (bio == null || bio.isEmpty()) {
            return plugin.getConfigManager().getString("settings.bio.default_text", "No bio set");
        }
        int lineLength = plugin.getConfigManager().getInt("settings.bio.line_length", 50);
        StringBuilder result = new StringBuilder();
        int index = 0;
        while (index < bio.length()) {
            int end = Math.min(index + lineLength, bio.length());
            if (end < bio.length() && bio.charAt(end) != ' ') {
                int lastSpace = bio.lastIndexOf(' ', end);
                if (lastSpace > index) {
                    end = lastSpace;
                }
            }
            if (index > 0) result.append("\n");
            result.append(bio, index, end);
            index = end;
            if (index < bio.length() && bio.charAt(index) == ' ') index++;
        }
        return result.toString();
    }

    private String processCosmeticPlaceholders(String text, Player targetPlayer) {
        HMCCosmeticsIntegration hmcIntegration = plugin.getHMCCosmeticsIntegration();
        ECosmeticsIntegration eIntegration = plugin.getECosmeticsIntegration();

        if (hmcIntegration != null && hmcIntegration.isEnabled() && targetPlayer != null) {
            text = text.replace("%hmccosmetics_total%", String.valueOf(hmcIntegration.getTotalCosmeticsCount(targetPlayer)))
                    .replace("%hmccosmetics_equipped%", String.valueOf(hmcIntegration.getEquippedCosmetics(targetPlayer).size()));
        } else if (eIntegration != null && eIntegration.isEnabled() && targetPlayer != null) {
            text = text.replace("%hmccosmetics_total%", String.valueOf(eIntegration.getTotalCosmeticsCount(targetPlayer)))
                    .replace("%hmccosmetics_equipped%", String.valueOf(eIntegration.getEquippedCosmetics(targetPlayer).size()));
        } else {
            text = text.replace("%hmccosmetics_total%", "N/A").replace("%hmccosmetics_equipped%", "N/A");
        }
        return text;
    }

    public String processTitlePlaceholders(String text, Player profileOwner) {
        text = text.replace("%player_name%", profile.getPlayerName());
        text = text.replace("%viewer_name%", viewer.getName());
        text = text.replace("%uprofiles_star%", String.valueOf(profile.getStars()));

        String state = profile.isPublic() ?
                plugin.getLanguageManager().getMessage("privacy.public") :
                plugin.getLanguageManager().getMessage("privacy.private");
        text = text.replace("%uprofiles_state%", state);

        Player targetPlayer = profileOwner != null ? profileOwner : Bukkit.getPlayer(profile.getUuid());
        text = processCosmeticPlaceholders(text, targetPlayer);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            text = PlaceholderAPI.setPlaceholders(viewer, text);
        }
        return text;
    }

    public PlayerProfile getProfile() { return profile; }
    public Player getViewer() { return viewer; }
    public MiniMessage getMiniMessage() { return miniMessage; }
}
