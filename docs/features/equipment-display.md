---
description: Display player equipment in profile GUI
---

# 👔 Equipment Display

## Overview

The equipment display feature shows a player's current armor and held items directly in their profile GUI. This includes helmet, chestplate, leggings, boots, main hand, and off hand items.

## How it Works

When a player opens a profile:
1. The plugin fetches the player's current equipment
2. Displays actual items in configured slots
3. Shows placeholder items when slots are empty
4. Updates in real-time if player is online

## Configuration

### Equipment Slots

{% code title="gui.yml - Equipment Configuration" lineNumbers="true" %}
```yaml
equipments:
  helmet:
    slot: 6
    empty_config:
      material: LEATHER_HELMET
      custom_model_data: 1
      display_name: "<gray>No Helmet</gray>"
      lore:
        - "<dark_gray>No helmet equipped</dark_gray>"

  chestplate:
    slot: 15
    empty_config:
      material: LEATHER_CHESTPLATE
      custom_model_data: 1
      display_name: "<gray>No Chestplate</gray>"
      lore:
        - "<dark_gray>No chestplate equipped</dark_gray>"

  leggings:
    slot: 24
    empty_config:
      material: LEATHER_LEGGINGS
      custom_model_data: 1
      display_name: "<gray>No Leggings</gray>"
      lore:
        - "<dark_gray>No leggings equipped</dark_gray>"

  boots:
    slot: 33
    empty_config:
      material: LEATHER_BOOTS
      custom_model_data: 1
      display_name: "<gray>No Boots</gray>"
      lore:
        - "<dark_gray>No boots equipped</dark_gray>"

  main_hand:
    slot: 17
    empty_config:
      material: STICK
      custom_model_data: 1
      display_name: "<gray>No Weapon</gray>"
      lore:
        - "<dark_gray>No weapon equipped</dark_gray>"

  off_hand:
    slot: 26
    empty_config:
      material: SHIELD
      custom_model_data: 1
      display_name: "<gray>No Shield</gray>"
      lore:
        - "<dark_gray>No offhand item equipped</dark_gray>"
```
{% endcode %}

## Equipment Types

### Armor Slots

| Slot | Type | Default Empty Item |
|------|------|-------------------|
| `helmet` | Head armor | `LEATHER_HELMET` |
| `chestplate` | Chest armor | `LEATHER_CHESTPLATE` |
| `leggings` | Leg armor | `LEATHER_LEGGINGS` |
| `boots` | Foot armor | `LEATHER_BOOTS` |

### Hand Slots

| Slot | Type | Default Empty Item |
|------|------|-------------------|
| `main_hand` | Right hand item | `STICK` |
| `off_hand` | Left hand item | `SHIELD` |

## GUI Layout

Visual representation of default equipment layout:

```
┌─────────────────────────────────────┐
│  [ ]  [ ]  [ ]  [ ]  [ ]  [⛑]  [ ] │  Row 1 - Helmet (slot 6)
│  [ ]  [ ]  [ ]  [ ]  [ ]  [ ]  [ ] │
│  [ ]  [ ]  [ ]  [ ]  [ ]  [👕][⚔] │  Row 2 - Chestplate (15) + Main Hand (17)
│  [ ]  [ ]  [ ]  [ ]  [ ]  [ ]  [ ] │
│  [ ]  [ ]  [ ]  [ ]  [ ]  [👖][🛡] │  Row 3 - Leggings (24) + Off Hand (26)
│  [ ]  [ ]  [ ]  [ ]  [ ]  [ ]  [ ] │
│  [ ]  [ ]  [ ]  [ ]  [ ]  [👞][ ] │  Row 4 - Boots (33)
└─────────────────────────────────────┘
```

## Empty Item Configuration

### Basic Empty Item

{% code title="Simple empty placeholder" %}
```yaml
equipments:
  helmet:
    slot: 6
    empty_config:
      material: BARRIER
      display_name: "<red>Empty Slot</red>"
```
{% endcode %}

### Detailed Empty Item

{% code title="Detailed empty placeholder" %}
```yaml
equipments:
  main_hand:
    slot: 17
    empty_config:
      material: STICK
      custom_model_data: 999
      display_name: "<gradient:#FF5733:#C70039>No Weapon</gradient>"
      lore:
        - ""
        - "<gray>This player has no weapon"
        - "<gray>equipped in their main hand."
        - ""
        - "<dark_gray>Empty slot</dark_gray>"
```
{% endcode %}

### Resource Pack Integration

Use custom model data for unique empty items:

{% code title="Custom model empty items" %}
```yaml
equipments:
  helmet:
    slot: 6
    empty_config:
      material: LEATHER_HELMET
      custom_model_data: 100  # Your custom model ID
      display_name: "<gray>No Helmet</gray>"

  chestplate:
    slot: 15
    empty_config:
      material: LEATHER_CHESTPLATE
      custom_model_data: 101
      display_name: "<gray>No Chestplate</gray>"
```
{% endcode %}

## Features

### Real-time Display

For online players:
- ✅ Shows current equipped items
- ✅ Includes enchantments
- ✅ Displays durability
- ✅ Shows custom names/lore
- ✅ Supports custom model data

### Offline Players

For offline players:
- ✅ Shows last known equipment (if cached)
- ❌ May not show latest changes

### Item Details Preserved

When displaying equipment:
- **Enchantments:** All enchantments are shown
- **Durability:** Damage is preserved
- **Custom Names:** Player-renamed items keep their names
- **Lore:** Custom lore is displayed
- **Attributes:** Item attributes are shown
- **NBT Data:** All NBT data is preserved

## Examples

### Minimal Equipment Display

{% code title="Minimal configuration" %}
```yaml
equipments:
  helmet:
    slot: 10
  chestplate:
    slot: 11
  leggings:
    slot: 12
  boots:
    slot: 13
  main_hand:
    slot: 14
  off_hand:
    slot: 15
```
{% endcode %}

{% hint style="info" %}
**Note:** Without `empty_config`, empty slots will display as AIR (nothing)
{% endhint %}

### Centered Layout

{% code title="Centered equipment display" %}
```yaml
size: 54  # 6 rows

equipments:
  helmet:
    slot: 13      # Center top
  chestplate:
    slot: 22      # Center middle
  leggings:
    slot: 31      # Center lower
  boots:
    slot: 40      # Center bottom
  main_hand:
    slot: 23      # Right of chestplate
  off_hand:
    slot: 21      # Left of chestplate
```
{% endcode %}

### Vertical Layout

{% code title="Vertical armor display" %}
```yaml
equipments:
  helmet:
    slot: 4       # Top center
  chestplate:
    slot: 13      # Middle center
  leggings:
    slot: 22      # Lower center
  boots:
    slot: 31      # Bottom center
  main_hand:
    slot: 24      # Right side
  off_hand:
    slot: 20      # Left side
```
{% endcode %}

## Use Cases

### PvP Server

Show equipment for combat analysis:

{% code title="PvP-focused display" %}
```yaml
equipments:
  helmet:
    slot: 2
    empty_config:
      material: BARRIER
      display_name: "<red>No Protection</red>"

  chestplate:
    slot: 11
    empty_config:
      material: BARRIER
      display_name: "<red>No Protection</red>"

  main_hand:
    slot: 15
    empty_config:
      material: WOODEN_SWORD
      display_name: "<gray>No Weapon</gray>"
      lore:
        - "<red>⚠ Unarmed!</red>"
```
{% endcode %}

### Fashion Show

Display for cosmetic purposes:

{% code title="Fashion-focused display" %}
```yaml
equipments:
  helmet:
    slot: 13
    empty_config:
      material: GLASS
      display_name: "<gradient:#FFD700:#FFA500>Bare Head</gradient>"
      lore:
        - "<gray>No stylish hat equipped!"

  chestplate:
    slot: 22
    empty_config:
      material: LEATHER_CHESTPLATE
      display_name: "<gradient:#FFD700:#FFA500>Default Outfit</gradient>"
```
{% endcode %}

### RPG Server

Show gear with stat emphasis:

{% code title="RPG-focused display" %}
```yaml
equipments:
  main_hand:
    slot: 20
    empty_config:
      material: STICK
      display_name: "<red>No Weapon</red>"
      lore:
        - ""
        - "<gray>Attack: <red>0</red>"
        - "<gray>Speed: <yellow>1.0</yellow>"

  helmet:
    slot: 10
    empty_config:
      material: LEATHER_HELMET
      display_name: "<blue>No Helmet</blue>"
      lore:
        - ""
        - "<gray>Defense: <blue>0</blue>"
```
{% endcode %}

## Visual Examples

### Standard Display

<figure><img src="../.gitbook/assets/equipment-standard.png" alt="Standard equipment display"><figcaption><p>Player with full diamond armor and tools</p></figcaption></figure>

### Empty Slots

<figure><img src="../.gitbook/assets/equipment-empty.png" alt="Empty equipment slots"><figcaption><p>Player with no equipment shows placeholders</p></figcaption></figure>

### Enchanted Items

<figure><img src="../.gitbook/assets/equipment-enchanted.png" alt="Enchanted equipment"><figcaption><p>Equipment with enchantments displayed</p></figcaption></figure>

## Troubleshooting

### Equipment not showing

{% hint style="danger" %}
Equipment slots are empty or show air
{% endhint %}

**Solutions:**
1. Check slot numbers are within GUI size (0-53)
2. Verify `equipments:` section exists in gui.yml
3. Add `empty_config` to show placeholders
4. Check player is online (offline players may not show latest items)

### Empty items not showing

{% hint style="warning" %}
Empty slots show nothing instead of placeholder
{% endhint %}

**Solutions:**
1. Add `empty_config` section to each equipment slot
2. Verify material is valid (e.g., `LEATHER_HELMET` not `HELMET`)
3. Check MiniMessage formatting in display_name

### Items showing wrong slot

{% hint style="info" %}
Equipment appears in unexpected positions
{% endhint %}

**Solutions:**
1. Verify slot numbers in gui.yml
2. Check GUI size (slots must be < size)
3. Use slot reference diagram above

## Slot Reference

For a 36-slot GUI (4 rows):

```
┌───┬───┬───┬───┬───┬───┬───┬───┬───┐
│ 0 │ 1 │ 2 │ 3 │ 4 │ 5 │ 6 │ 7 │ 8 │
├───┼───┼───┼───┼───┼───┼───┼───┼───┤
│ 9 │10 │11 │12 │13 │14 │15 │16 │17 │
├───┼───┼───┼───┼───┼───┼───┼───┼───┤
│18 │19 │20 │21 │22 │23 │24 │25 │26 │
├───┼───┼───┼───┼───┼───┼───┼───┼───┤
│27 │28 │29 │30 │31 │32 │33 │34 │35 │
└───┴───┴───┴───┴───┴───┴───┴───┴───┘
```

## See Also

{% content-ref url="cosmetics-integration.md" %}
[cosmetics-integration.md](cosmetics-integration.md)
{% endcontent-ref %}

{% content-ref url="../configuration/gui.yml.md" %}
[gui.yml.md](../configuration/gui.yml.md)
{% endcontent-ref %}
