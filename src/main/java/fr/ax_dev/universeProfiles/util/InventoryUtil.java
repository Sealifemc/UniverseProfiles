package fr.ax_dev.universeProfiles.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryUtil {

    private static Boolean isPaperServer = null;
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    public static Inventory createInventory(InventoryHolder holder, int size, Component title) {
        String legacyTitle = LEGACY_SERIALIZER.serialize(title);
        return Bukkit.createInventory(holder, size, legacyTitle);
    }

    public static void setDisplayName(ItemMeta meta, Component component) {
        if (isPaper()) {
            try {
                Method method = ItemMeta.class.getMethod("displayName", Component.class);
                method.invoke(meta, component);
                return;
            } catch (Exception ignored) {
            }
        }
        String legacy = LEGACY_SERIALIZER.serialize(component);
        meta.setDisplayName(legacy);
    }

    public static void setLore(ItemMeta meta, List<Component> lore) {
        if (isPaper()) {
            try {
                Method method = ItemMeta.class.getMethod("lore", List.class);
                method.invoke(meta, lore);
                return;
            } catch (Exception ignored) {
            }
        }
        List<String> legacyLore = lore.stream()
            .map(LEGACY_SERIALIZER::serialize)
            .collect(Collectors.toList());
        meta.setLore(legacyLore);
    }

    public static Component getDisplayName(ItemMeta meta) {
        if (isPaper()) {
            try {
                Method method = ItemMeta.class.getMethod("displayName");
                return (Component) method.invoke(meta);
            } catch (Exception ignored) {
            }
        }
        if (meta.hasDisplayName()) {
            return LEGACY_SERIALIZER.deserialize(meta.getDisplayName());
        }
        return null;
    }

    public static List<Component> getLore(ItemMeta meta) {
        if (isPaper()) {
            try {
                Method method = ItemMeta.class.getMethod("lore");
                return (List<Component>) method.invoke(meta);
            } catch (Exception ignored) {
            }
        }
        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            if (lore != null) {
                return lore.stream()
                    .map(LEGACY_SERIALIZER::deserialize)
                    .collect(Collectors.toList());
            }
        }
        return null;
    }

    private static boolean isPaper() {
        if (isPaperServer == null) {
            try {
                Method method = ItemMeta.class.getMethod("displayName", Component.class);
                isPaperServer = true;
            } catch (NoSuchMethodException e) {
                isPaperServer = false;
            }
        }
        return isPaperServer;
    }
}
