---
description: HMCCosmetics integration for displaying player cosmetics
---

# ✨ Cosmetics Integration

## Overview

UniverseProfiles integrates with **HMCCosmetics** to display a player's equipped cosmetic items directly in their profile GUI. This includes helmets, backpacks, balloons, offhand items, and more.

{% hint style="info" %}
**Requirement:** [HMCCosmetics 2.8.1+](https://www.spigotmc.org/resources/hmccosmetics.100107) must be installed
{% endhint %}

## Features

- ✅ Display cosmetic items in profile GUI
- ✅ Support for all HMCCosmetics slot types
- ✅ Custom empty placeholders
- ✅ Real-time updates for online players
- ✅ Automatic integration when plugin detected

## Setup

### 1. Install HMCCosmetics

Download and install HMCCosmetics plugin:
- [SpigotMC](https://www.spigotmc.org/resources/hmccosmetics.100107)
- Version 2.8.1 or higher required

### 2. Enable Integration

{% code title="config.yml" %}
```yaml
settings:
  cosmetics_plugin: HMCCOSMETICS  # Enable HMCCosmetics integration
```
{% endcode %}

{% tabs %}
{% tab title="Enabled" %}
```yaml
cosmetics_plugin: HMCCOSMETICS
```
✅ Cosmetics will be displayed in GUI
{% endtab %}

{% tab title="Disabled" %}
```yaml
cosmetics_plugin: NONE
```
❌ No cosmetics integration
{% endtab %}
{% endtabs %}

### 3. Configure Cosmetic Slots

{% code title="gui.yml - Cosmetic Configuration" %}
```yaml
customs_items:
  cosmetic_helmet:
    type: HELMET  # Cosmetic slot type
    slot: 7
    empty_config:
      material: LEATHER_HELMET
      custom_model_data: 1
      display_name: "<gray>No Helmet</gray>"
      lore:
        - "<dark_gray>No cosmetic helmet equipped</dark_gray>"

  cosmetic_backpack:
    type: BACKPACK
    slot: 16
    empty_config:
      material: LEATHER_HORSE_ARMOR
      custom_model_data: 1
      display_name: "<gray>No Backpack</gray>"
      lore:
        - "<dark_gray>No cosmetic backpack equipped</dark_gray>"

  cosmetic_offhand:
    type: OFFHAND
    slot: 25
    empty_config:
      material: STICK
      custom_model_data: 1
      display_name: "<gray>No Offhand Item</gray>"
      lore:
        - "<dark_gray>No cosmetic offhand item equipped</dark_gray>"

  cosmetic_balloon:
    type: BALLOON
    slot: 34
    empty_config:
      material: LEAD
      custom_model_data: 1
      display_name: "<gray>No Balloon</gray>"
      lore:
        - "<dark_gray>No cosmetic balloon equipped</dark_gray>"
```
{% endcode %}

## Available Cosmetic Types

### Armor Cosmetics

| Type | Description | Example Empty Item |
|------|-------------|-------------------|
| `HELMET` | Cosmetic head armor | `LEATHER_HELMET` |
| `CHESTPLATE` | Cosmetic chest armor | `LEATHER_CHESTPLATE` |
| `LEGGINGS` | Cosmetic leg armor | `LEATHER_LEGGINGS` |
| `BOOTS` | Cosmetic foot armor | `LEATHER_BOOTS` |

### Special Cosmetics

| Type | Description | Example Empty Item |
|------|-------------|-------------------|
| `BACKPACK` | Cosmetic backpack | `LEATHER_HORSE_ARMOR` |
| `BALLOON` | Cosmetic balloon | `LEAD` |
| `OFFHAND` | Cosmetic offhand item | `STICK` |

## Configuration Examples

### Full Cosmetics Display

{% code title="gui.yml - Complete cosmetic setup" %}
```yaml
customs_items:
  # Helmet
  cosmetic_helmet:
    type: HELMET
    slot: 7
    empty_config:
      material: LEATHER_HELMET
      custom_model_data: 100
      display_name: "<gradient:#FFD700:#FFA500>No Cosmetic Helmet</gradient>"
      lore:
        - ""
        - "<gray>Equip a cosmetic helmet"
        - "<gray>to customize your look!"

  # Chestplate
  cosmetic_chestplate:
    type: CHESTPLATE
    slot: 16
    empty_config:
      material: LEATHER_CHESTPLATE
      custom_model_data: 101
      display_name: "<gradient:#FFD700:#FFA500>No Cosmetic Chestplate</gradient>"

  # Leggings
  cosmetic_leggings:
    type: LEGGINGS
    slot: 25
    empty_config:
      material: LEATHER_LEGGINGS
      custom_model_data: 102
      display_name: "<gradient:#FFD700:#FFA500>No Cosmetic Leggings</gradient>"

  # Boots
  cosmetic_boots:
    type: BOOTS
    slot: 34
    empty_config:
      material: LEATHER_BOOTS
      custom_model_data: 103
      display_name: "<gradient:#FFD700:#FFA500>No Cosmetic Boots</gradient>"

  # Backpack
  cosmetic_backpack:
    type: BACKPACK
    slot: 8
    empty_config:
      material: LEATHER_HORSE_ARMOR
      custom_model_data: 200
      display_name: "<gradient:#00FF00:#00FFFF>No Backpack</gradient>"
      lore:
        - "<gray>Show off your style with"
        - "<gray>a cosmetic backpack!"

  # Balloon
  cosmetic_balloon:
    type: BALLOON
    slot: 17
    empty_config:
      material: LEAD
      custom_model_data: 300
      display_name: "<rainbow>No Balloon</rainbow>"
      lore:
        - "<gray>Float in style!"

  # Offhand
  cosmetic_offhand:
    type: OFFHAND
    slot: 26
    empty_config:
      material: STICK
      custom_model_data: 400
      display_name: "<gold>No Offhand Cosmetic</gold>"
```
{% endcode %}

### Minimal Cosmetics

{% code title="Only helmet and backpack" %}
```yaml
customs_items:
  cosmetic_helmet:
    type: HELMET
    slot: 10

  cosmetic_backpack:
    type: BACKPACK
    slot: 19
```
{% endcode %}

{% hint style="warning" %}
**Note:** Without `empty_config`, empty cosmetic slots show as AIR (nothing)
{% endhint %}

### Resource Pack Integration

Use custom models for empty placeholders:

{% code title="Custom model empty items" %}
```yaml
customs_items:
  cosmetic_helmet:
    type: HELMET
    slot: 7
    empty_config:
      material: LEATHER_HELMET
      custom_model_data: 9001  # Your custom empty helmet model
      display_name: "<gray>No Helmet</gray>"

  cosmetic_backpack:
    type: BACKPACK
    slot: 16
    empty_config:
      material: LEATHER_HORSE_ARMOR
      custom_model_data: 9002  # Your custom empty backpack model
      display_name: "<gray>No Backpack</gray>"
```
{% endcode %}

## GUI Layout Examples

### Side-by-side Layout

Equipment on left, cosmetics on right:

```
┌─────────────────────────────────────┐
│  [ ]  [ ]  [ ]  [ ]  [ ]  [⛑][🎩] │  Equipment Helmet | Cosmetic Helmet
│  [ ]  [ ]  [ ]  [ ]  [ ]  [ ]  [ ] │
│  [ ]  [ ]  [ ]  [ ]  [ ]  [👕][🎽][🎒]  Equipment | Cosmetic | Backpack
│  [ ]  [ ]  [ ]  [ ]  [ ]  [ ]  [ ] │
│  [ ]  [ ]  [ ]  [ ]  [ ]  [👖][👗] │  Equipment Leggings | Cosmetic
│  [ ]  [ ]  [ ]  [ ]  [ ]  [ ]  [ ] │
│  [ ]  [ ]  [ ]  [ ]  [ ]  [👞][👠][🎈]  Equipment Boots | Cosmetic | Balloon
└─────────────────────────────────────┘
```

### Separated Layout

Armor on left, cosmetics on right:

{% code title="gui.yml - Separated layout" %}
```yaml
size: 54  # 6 rows

# Equipment (left side)
equipments:
  helmet: { slot: 10 }
  chestplate: { slot: 19 }
  leggings: { slot: 28 }
  boots: { slot: 37 }

# Cosmetics (right side)
customs_items:
  cosmetic_helmet:
    type: HELMET
    slot: 16

  cosmetic_backpack:
    type: BACKPACK
    slot: 25

  cosmetic_balloon:
    type: BALLOON
    slot: 34
```
{% endcode %}

## How It Works

### For Online Players

1. Plugin queries HMCCosmetics API
2. Fetches equipped cosmetics for player
3. Displays actual cosmetic items in GUI
4. Updates in real-time if player changes cosmetics

### For Offline Players

1. Shows last known cosmetics (if cached)
2. May not reflect latest changes
3. Depends on cache settings in config.yml

## Item Details

When displaying cosmetics:
- **Custom Model Data:** Preserved from HMCCosmetics
- **Display Name:** Shows cosmetic name
- **Lore:** Shows cosmetic description
- **Enchantments:** Visual enchantments displayed
- **NBT Data:** All cosmetic NBT preserved

## Use Cases

### Fashion Show Server

Display all cosmetics prominently:

{% code title="Fashion-focused display" %}
```yaml
customs_items:
  cosmetic_helmet:
    type: HELMET
    slot: 4
    empty_config:
      material: GLASS
      display_name: "<rainbow>✨ Bare Head ✨</rainbow>"
      lore:
        - "<gray>Show your face!"

  cosmetic_backpack:
    type: BACKPACK
    slot: 22
    empty_config:
      material: CHEST
      display_name: "<gold>⭐ No Backpack ⭐</gold>"
      lore:
        - "<gray>Add some flair to your back!"

  cosmetic_balloon:
    type: BALLOON
    slot: 40
    empty_config:
      material: STRING
      display_name: "<aqua>🎈 No Balloon 🎈</aqua>"
      lore:
        - "<gray>Float away in style!"
```
{% endcode %}

### RPG Server

Minimal cosmetic display:

{% code title="RPG-focused (gear emphasis)" %}
```yaml
customs_items:
  cosmetic_helmet:
    type: HELMET
    slot: 8
    empty_config:
      material: BARRIER
      display_name: "<dark_gray>No Cosmetic</dark_gray>"
```
{% endcode %}

### Hub Server

Full cosmetic showcase:

{% code title="Hub showcase layout" %}
```yaml
size: 54

customs_items:
  # Top row - Armor cosmetics
  cosmetic_helmet:
    type: HELMET
    slot: 11

  cosmetic_chestplate:
    type: CHESTPLATE
    slot: 13

  cosmetic_leggings:
    type: LEGGINGS
    slot: 15

  # Middle row - Special cosmetics
  cosmetic_backpack:
    type: BACKPACK
    slot: 21

  cosmetic_balloon:
    type: BALLOON
    slot: 23

  cosmetic_offhand:
    type: OFFHAND
    slot: 25
```
{% endcode %}

## Visual Examples

### With Cosmetics

<figure><img src="../.gitbook/assets/cosmetics-equipped.png" alt="Player with cosmetics equipped"><figcaption><p>Player profile showing equipped HMCCosmetics items</p></figcaption></figure>

### Without Cosmetics

<figure><img src="../.gitbook/assets/cosmetics-empty.png" alt="Player without cosmetics"><figcaption><p>Empty cosmetic slots showing placeholders</p></figcaption></figure>

## Troubleshooting

### Cosmetics not showing

{% hint style="danger" %}
Cosmetic slots are empty even when player has cosmetics
{% endhint %}

**Solutions:**
1. Verify HMCCosmetics 2.8.1+ is installed
2. Check `cosmetics_plugin: HMCCOSMETICS` in config.yml
3. Restart server after enabling integration
4. Verify cosmetic type names match HMCCosmetics slots
5. Check console for HMCCosmetics API errors

### Wrong cosmetic type

{% hint style="warning" %}
```
[ERROR] Unknown cosmetic type: HELEMT
```
{% endhint %}

**Solutions:**
1. Check spelling (e.g., `HELMET` not `HELEMT`)
2. Use exact type names: `HELMET`, `BACKPACK`, `BALLOON`, `OFFHAND`
3. Refer to Available Cosmetic Types table above

### Empty items not showing

{% hint style="info" %}
Empty slots show nothing instead of placeholder
{% endhint %}

**Solutions:**
1. Add `empty_config` section to each cosmetic item
2. Verify material is valid
3. Check MiniMessage formatting in display_name

## API for Developers

{% code title="Java - Check if cosmetics enabled" %}
```java
// Check if HMCCosmetics integration is enabled
boolean cosmeticsEnabled = plugin.getConfigManager()
    .getCosmeticsPlugin()
    .equals("HMCCOSMETICS");

if (cosmeticsEnabled) {
    // HMCCosmetics integration is active
}
```
{% endcode %}

{% code title="Java - Get player cosmetics" %}
```java
// Using HMCCosmetics API
if (Bukkit.getPluginManager().isPluginEnabled("HMCCosmetics")) {
    CosmeticUser user = HMCCosmeticsAPI.getCosmeticUser(player);

    // Get equipped helmet cosmetic
    if (user != null) {
        ItemStack helmetCosmetic = user.getCosmetic(CosmeticSlot.HELMET);
    }
}
```
{% endcode %}

## Compatibility

| HMCCosmetics Version | UniverseProfiles | Status |
|---------------------|------------------|---------|
| 2.8.1+ | 1.0+ | ✅ Full support |
| 2.7.x | 1.0+ | ⚠️ Limited support |
| < 2.7 | 1.0+ | ❌ Not supported |

## See Also

{% content-ref url="equipment-display.md" %}
[equipment-display.md](equipment-display.md)
{% endcontent-ref %}

{% content-ref url="../configuration/config.yml.md" %}
[config.yml.md](../configuration/config.yml.md)
{% endcontent-ref %}

{% content-ref url="../configuration/gui.yml.md" %}
[gui.yml.md](../configuration/gui.yml.md)
{% endcontent-ref %}
