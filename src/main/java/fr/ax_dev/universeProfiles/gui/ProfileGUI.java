package fr.ax_dev.universeProfiles.gui;

import fr.ax_dev.universeProfiles.UniverseProfiles;
import fr.ax_dev.universeProfiles.integrations.ECosmeticsIntegration;
import fr.ax_dev.universeProfiles.integrations.HMCCosmeticsIntegration;
import fr.ax_dev.universeProfiles.models.PlayerProfile;
import fr.ax_dev.universeProfiles.util.InventoryUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ProfileGUI implements InventoryHolder, Listener {

    private final UniverseProfiles plugin;
    private final Player viewer;
    private final PlayerProfile profile;
    private final Inventory inventory;
    private final ItemBuilder itemBuilder;
    private final Map<Integer, List<String>> slotItemKeys = new HashMap<>();
    private final Map<Integer, String> playerInvSlots = new HashMap<>();
    private final boolean usePlayerInventory;

    public ProfileGUI(UniverseProfiles plugin, Player viewer, PlayerProfile profile) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.profile = profile;
        this.itemBuilder = new ItemBuilder(plugin, profile, viewer);
        this.usePlayerInventory = plugin.getGuiConfigManager().getConfig().getBoolean("player-inv", false);

        Player profileOwner = Bukkit.getPlayer(profile.getUuid());
        if (profileOwner != null && profileOwner.isOnline()) {
            profile.updateFromPlayer(profileOwner);
        }

        String title = resolveTitle(profileOwner);
        title = itemBuilder.processTitlePlaceholders(title, profileOwner);

        int size = plugin.getGuiConfigManager().getSize();
        this.inventory = InventoryUtil.createInventory(this, size, itemBuilder.getMiniMessage().deserialize(title));

        if (usePlayerInventory) {
            plugin.getInventoryBackupManager().backupAndClear(viewer);
        }

        setupItems();
        setupPlayerInventoryItems();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private String resolveTitle(Player target) {
        ConfigurationSection titles = plugin.getGuiConfigManager().getTitles();
        if (titles == null) {
            return plugin.getGuiConfigManager().getTitle();
        }

        String defaultTitle = titles.getString("default", plugin.getGuiConfigManager().getTitle());

        List<Map.Entry<String, ConfigurationSection>> conditionalTitles = new ArrayList<>();
        for (String key : titles.getKeys(false)) {
            if (key.equals("default")) continue;
            ConfigurationSection titleSection = titles.getConfigurationSection(key);
            if (titleSection != null && titleSection.contains("title")) {
                conditionalTitles.add(Map.entry(key, titleSection));
            }
        }

        conditionalTitles.sort((a, b) -> b.getValue().getInt("priority", 0) - a.getValue().getInt("priority", 0));

        for (Map.Entry<String, ConfigurationSection> entry : conditionalTitles) {
            ConfigurationSection titleSection = entry.getValue();
            ConfigurationSection conditions = titleSection.getConfigurationSection("conditions");
            if (plugin.getConditionManager().checkConditions(conditions, viewer, target)) {
                return titleSection.getString("title", defaultTitle);
            }
        }

        return defaultTitle;
    }

    private void setupItems() {
        setupEquipmentItems();
        setupCustomItems();
    }

    private void setupPlayerInventoryItems() {
        if (!usePlayerInventory) return;

        ConfigurationSection customItems = plugin.getGuiConfigManager().getCustomItems();
        if (customItems == null) return;

        Player target = Bukkit.getPlayer(profile.getUuid());

        for (String key : customItems.getKeys(false)) {
            ConfigurationSection item = customItems.getConfigurationSection(key);
            if (item == null || !item.getBoolean("player-inv", false)) continue;

            if (!checkConditions(item, target)) continue;

            int playerSlot = item.getInt("player-slot", -1);
            if (playerSlot < 0 || playerSlot > 35) continue;

            ItemStack builtItem = itemBuilder.buildActionItem(item);
            viewer.getInventory().setItem(playerSlot, builtItem);
            playerInvSlots.put(playerSlot, key);
        }
    }

    private void setupEquipmentItems() {
        ConfigurationSection equipments = plugin.getGuiConfigManager().getEquipments();
        if (equipments == null) return;

        for (String equipmentType : equipments.getKeys(false)) {
            ConfigurationSection equipment = equipments.getConfigurationSection(equipmentType);
            if (equipment == null) continue;

            int slot = equipment.getInt("slot", -1);
            if (slot == -1) continue;

            ItemStack equipmentItem = getEquipmentItem(equipmentType);
            if (equipmentItem != null) {
                inventory.setItem(slot, equipmentItem);
            } else {
                inventory.setItem(slot, itemBuilder.buildEmptyItem(
                    equipment.getConfigurationSection("empty_config"), equipmentType));
            }
        }
    }

    private ItemStack getEquipmentItem(String equipmentType) {
        return switch (equipmentType) {
            case "helmet" -> profile.getArmorContents() != null ? profile.getArmorContents()[3] : null;
            case "chestplate" -> profile.getArmorContents() != null ? profile.getArmorContents()[2] : null;
            case "leggings" -> profile.getArmorContents() != null ? profile.getArmorContents()[1] : null;
            case "boots" -> profile.getArmorContents() != null ? profile.getArmorContents()[0] : null;
            case "main_hand" -> profile.getMainHand();
            case "off_hand" -> profile.getOffHand();
            default -> null;
        };
    }

    private void setupCustomItems() {
        ConfigurationSection customItems = plugin.getGuiConfigManager().getCustomItems();
        if (customItems == null) return;

        Map<Integer, List<Map.Entry<String, ConfigurationSection>>> slotItems = new HashMap<>();

        for (String key : customItems.getKeys(false)) {
            ConfigurationSection item = customItems.getConfigurationSection(key);
            if (item == null) continue;

            List<Integer> slots = getItemSlots(item);
            for (int slot : slots) {
                slotItems.computeIfAbsent(slot, k -> new ArrayList<>()).add(Map.entry(key, item));
            }
        }

        Player target = Bukkit.getPlayer(profile.getUuid());

        for (Map.Entry<Integer, List<Map.Entry<String, ConfigurationSection>>> entry : slotItems.entrySet()) {
            int slot = entry.getKey();
            List<Map.Entry<String, ConfigurationSection>> items = entry.getValue();

            items.sort((a, b) -> b.getValue().getInt("priority", 0) - a.getValue().getInt("priority", 0));

            for (Map.Entry<String, ConfigurationSection> itemEntry : items) {
                ConfigurationSection item = itemEntry.getValue();

                if (!checkConditions(item, target)) continue;

                slotItemKeys.computeIfAbsent(slot, k -> new ArrayList<>()).add(itemEntry.getKey());
                setupSingleItem(item, slot);
                break;
            }
        }
    }

    private boolean checkConditions(ConfigurationSection item, Player target) {
        ConfigurationSection conditions = item.getConfigurationSection("conditions");
        if (conditions == null) {
            ConfigurationSection viewReqs = item.getConfigurationSection("view_requirements");
            return plugin.getConditionManager().checkConditions(viewReqs, viewer, target);
        }
        return plugin.getConditionManager().checkConditions(conditions, viewer, target);
    }

    private List<Integer> getItemSlots(ConfigurationSection item) {
        List<Integer> result = new ArrayList<>();
        if (item.contains("slots")) {
            for (String slotRange : item.getStringList("slots")) {
                if (slotRange.contains("-")) {
                    String[] parts = slotRange.split("-");
                    int start = Integer.parseInt(parts[0]);
                    int end = Integer.parseInt(parts[1]);
                    for (int i = start; i <= end; i++) result.add(i);
                } else {
                    result.add(Integer.parseInt(slotRange));
                }
            }
        } else if (item.contains("slot")) {
            result.add(item.getInt("slot"));
        }
        return result;
    }

    private void setupSingleItem(ConfigurationSection item, int slot) {
        if (item.contains("your_slot")) {
            setupInventorySlot(item, slot);
        } else if (item.contains("type")) {
            setupCosmeticItem(item, slot);
        } else {
            inventory.setItem(slot, itemBuilder.buildActionItem(item));
        }
    }

    private void setupInventorySlot(ConfigurationSection item, int slot) {
        int yourSlot = item.getInt("your_slot", -1);
        ItemStack[] playerInventory = profile.getInventory();

        if (playerInventory != null && yourSlot >= 0 && yourSlot < playerInventory.length) {
            ItemStack stackItem = playerInventory[yourSlot];
            if (stackItem != null && stackItem.getType() != Material.AIR) {
                ItemStack clonedItem = stackItem.clone();
                if (item.getBoolean("hideTooltip", false)) {
                    ItemMeta meta = clonedItem.getItemMeta();
                    if (meta != null) {
                        try { meta.setHideTooltip(true); } catch (NoSuchMethodError ignored) {}
                        clonedItem.setItemMeta(meta);
                    }
                }
                inventory.setItem(slot, clonedItem);
                return;
            }
        }
        inventory.setItem(slot, itemBuilder.buildEmptyItem(item.getConfigurationSection("empty_config"), "Empty"));
    }

    private void setupCosmeticItem(ConfigurationSection item, int slot) {
        String type = item.getString("type", "");
        Player profileOwner = Bukkit.getPlayer(profile.getUuid());

        if (profileOwner == null) {
            inventory.setItem(slot, itemBuilder.buildEmptyItem(item.getConfigurationSection("empty_config"), type));
            return;
        }

        ItemStack cosmeticItem = getCosmeticItem(profileOwner, type);
        if (cosmeticItem == null || cosmeticItem.getType() == Material.AIR) {
            inventory.setItem(slot, itemBuilder.buildEmptyItem(item.getConfigurationSection("empty_config"), type));
            return;
        }

        ItemStack clonedItem = cosmeticItem.clone();
        ItemMeta meta = clonedItem.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                Component displayName = InventoryUtil.getDisplayName(meta);
                if (displayName != null) {
                    InventoryUtil.setDisplayName(meta, itemBuilder.getMiniMessage().deserialize("<!italic><white>").append(displayName));
                }
            }
            if (meta.hasLore()) {
                List<Component> lore = InventoryUtil.getLore(meta);
                if (lore != null) {
                    List<Component> newLore = new ArrayList<>();
                    for (Component line : lore) {
                        newLore.add(itemBuilder.getMiniMessage().deserialize("<!italic><white>").append(line));
                    }
                    InventoryUtil.setLore(meta, newLore);
                }
            }
            clonedItem.setItemMeta(meta);
        }
        inventory.setItem(slot, clonedItem);
    }

    private ItemStack getCosmeticItem(Player player, String type) {
        HMCCosmeticsIntegration hmcIntegration = plugin.getHMCCosmeticsIntegration();
        ECosmeticsIntegration eIntegration = plugin.getECosmeticsIntegration();

        if (hmcIntegration != null && hmcIntegration.isEnabled()) {
            return hmcIntegration.getCosmeticItemBySlot(player, type);
        } else if (eIntegration != null && eIntegration.isEnabled()) {
            return eIntegration.getCosmeticItemBySlot(player, type);
        }
        return null;
    }

    public void open() {
        viewer.openInventory(inventory);
        plugin.getSoundManager().playOpenSound(viewer);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!player.equals(viewer)) return;
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        if (event.getClickedInventory() != null && event.getClickedInventory().equals(player.getInventory())) {
            if (usePlayerInventory && playerInvSlots.containsKey(event.getSlot())) {
                handlePlayerInvClick(player, event.getSlot(), event.getClick());
            }
            return;
        }

        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(inventory)) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        handleCustomClick(player, event.getSlot(), event.getClick());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!player.equals(viewer)) return;
        if (!event.getInventory().equals(inventory)) return;
        event.setCancelled(true);
    }

    private void handlePlayerInvClick(Player player, int slot, ClickType clickType) {
        String key = playerInvSlots.get(slot);
        if (key == null) return;

        ConfigurationSection customItems = plugin.getGuiConfigManager().getCustomItems();
        if (customItems == null) return;

        ConfigurationSection item = customItems.getConfigurationSection(key);
        if (item == null) return;

        if (!canClickItem(item, player)) return;

        ConfigurationSection actions = item.getConfigurationSection("actions");
        if (actions == null) return;

        String clickTypeName = getClickTypeName(clickType);
        ConfigurationSection clickActions = actions.getConfigurationSection(clickTypeName);
        if (clickActions != null) {
            plugin.getSoundManager().playClickSound(player);
            new ActionExecutor(plugin, profile, player).executeActions(clickActions);
        }
    }

    private void handleCustomClick(Player player, int slot, ClickType clickType) {
        List<String> keys = slotItemKeys.get(slot);
        if (keys == null || keys.isEmpty()) return;

        ConfigurationSection customItems = plugin.getGuiConfigManager().getCustomItems();
        if (customItems == null) return;

        for (String key : keys) {
            ConfigurationSection item = customItems.getConfigurationSection(key);
            if (item == null) continue;

            if (!canClickItem(item, player)) return;

            ConfigurationSection actions = item.getConfigurationSection("actions");
            if (actions == null) return;

            String clickTypeName = getClickTypeName(clickType);
            ConfigurationSection clickActions = actions.getConfigurationSection(clickTypeName);
            if (clickActions != null) {
                plugin.getSoundManager().playClickSound(player);
                new ActionExecutor(plugin, profile, player).executeActions(clickActions);
                setupCustomItems();
            }
            return;
        }
    }

    private boolean canClickItem(ConfigurationSection item, Player player) {
        ConfigurationSection clickReqs = item.getConfigurationSection("click_requirements");
        if (clickReqs == null) return true;

        for (String reqKey : clickReqs.getKeys(false)) {
            if (reqKey.equals("permission")) {
                String permission = clickReqs.getString("permission", "");
                if (!permission.isEmpty() && !player.hasPermission(permission)) return false;
            } else {
                ConfigurationSection req = clickReqs.getConfigurationSection(reqKey);
                if (req == null) continue;
                String permission = req.getString("permission", "");
                if (!permission.isEmpty() && !player.hasPermission(permission)) return false;
            }
        }
        return true;
    }

    private String getClickTypeName(ClickType clickType) {
        return switch (clickType) {
            case LEFT -> "LEFT_CLICK";
            case SHIFT_LEFT -> "SHIFT_LEFT_CLICK";
            case RIGHT -> "RIGHT_CLICK";
            case SHIFT_RIGHT -> "SHIFT_RIGHT_CLICK";
            case MIDDLE -> "MIDDLE_CLICK";
            default -> "LEFT_CLICK";
        };
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!player.equals(viewer)) return;
        if (usePlayerInventory) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) return;

        if (usePlayerInventory && !viewer.isDead()) {
            dropNonPluginItems();
            plugin.getInventoryBackupManager().restore(viewer);
        }
        HandlerList.unregisterAll(this);
    }

    private void dropNonPluginItems() {
        for (int i = 0; i < 36; i++) {
            if (playerInvSlots.containsKey(i)) continue;

            ItemStack item = viewer.getInventory().getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                viewer.getWorld().dropItemNaturally(viewer.getLocation(), item);
                viewer.getInventory().setItem(i, null);
            }
        }

        ItemStack offhand = viewer.getInventory().getItemInOffHand();
        if (offhand != null && offhand.getType() != Material.AIR) {
            viewer.getWorld().dropItemNaturally(viewer.getLocation(), offhand);
            viewer.getInventory().setItemInOffHand(null);
        }
    }

    public void forceClose() {
        if (usePlayerInventory) {
            plugin.getInventoryBackupManager().restore(viewer);
        }
        HandlerList.unregisterAll(this);
    }

    public Player getViewer() {
        return viewer;
    }

    public boolean usesPlayerInventory() {
        return usePlayerInventory;
    }

    @Override
    public Inventory getInventory() { return inventory; }
}
