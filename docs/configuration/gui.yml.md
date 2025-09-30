---
description: Complete guide to customizing your profile GUI
---

# 🎨 gui.yml

## Overview

The `gui.yml` file controls every aspect of your profile interface, including layout, items, cosmetics, equipment, and interactive buttons.

## Basic Configuration

### Window Settings

{% code title="gui.yml" lineNumbers="true" %}
```yaml
# GUI window title (supports MiniMessage format + PlaceholderAPI)
title: "<shift:-8><glyph:gui_profile>"

# GUI size (must be multiple of 9, max 54)
size: 36

# Player inventory mode (optional)
player-inv: false
```
{% endcode %}

### Player Inventory Mode

{% hint style="warning" %}
**Advanced Feature:** This mode allows placing items in the viewer's inventory (bottom part of the GUI).
{% endhint %}

When `player-inv: true`:
- Items can be placed in the player's hotbar/inventory slots (0-35)
- The player's original inventory is **backed up** and **restored** when:
  - The GUI is closed normally
  - The player quits the server
  - The player dies (respects keepInventory gamerule)
  - The server shuts down
- Item pickup is disabled while in the menu
- Any foreign items (not from the plugin) are dropped on close

{% code title="Player Inventory Item Example" %}
```yaml
player-inv: true

customs_items:
  close_button:
    player-inv: true
    player-slot: 8  # Slot in player's hotbar (0-35)
    material: BARRIER
    display_name: "<red><bold>Close"
    lore:
      - "<gray>Click to close the profile"
    actions:
      LEFT_CLICK:
        action_1:
          action: CLOSE
```
{% endcode %}

**Death Handling:**
- If `keepInventory` is **true**: Backed up items are restored to the player after respawn
- If `keepInventory` is **false**: Backed up items are dropped naturally as death loot

{% hint style="info" %}
**Title supports MiniMessage formatting and PlaceholderAPI!** Learn more at [https://docs.advntr.dev/minimessage/format.html](https://docs.advntr.dev/minimessage/format.html)
{% endhint %}

{% hint style="success" %}
**New in latest version:** The title now supports PlaceholderAPI placeholders, including `%viewer_name%` to display the viewer's name!
{% endhint %}

### Available Placeholders

All placeholders work in `title`, `display_name`, and `lore` fields:

| Placeholder | Description | Example Output | Works in Title |
|-------------|-------------|----------------|----------------|
| `%player_name%` | Profile owner's name | `Steve` | ✅ |
| `%viewer_name%` | Viewer's name | `Alex` | ✅ |
| `%uprofiles_star%` | Player's star count | `42` | ✅ |
| `%uprofiles_bio%` | Player's biography | `Hello!` | ✅ |
| `%uprofiles_state%` | Privacy state | `Public` / `Private` | ✅ |
| PlaceholderAPI | Any PAPI placeholder | `%luckperms_groups%` | ✅ |

{% hint style="warning" %}
**Title Placeholders:** When using PlaceholderAPI placeholders in the title, they are resolved for the **viewer**, not the profile owner. This allows dynamic titles like `<gold>%viewer_name%</gold> viewing <yellow>%player_name%</yellow>`.
{% endhint %}

## Equipment Slots

Display player's armor and held items:

{% code title="gui.yml - Equipment Configuration" lineNumbers="true" %}
```yaml
equipments:
  helmet:
    slot: 6
    empty_config:
      material: LEATHER_HELMET
      custom_model_data: 1
      item_model: ""  # Optional: "namespace:model_name" for 1.21.2+
      display_name: "<gray>No Helmet</gray>"
      lore:
        - "<dark_gray>No helmet equipped</dark_gray>"

  chestplate:
    slot: 15
    empty_config:
      material: LEATHER_CHESTPLATE
      custom_model_data: 1
      item_model: ""  # Optional: Custom item model
      display_name: "<gray>No Chestplate</gray>"

  leggings:
    slot: 24
    empty_config:
      material: LEATHER_LEGGINGS
      custom_model_data: 1
      display_name: "<gray>No Leggings</gray>"

  boots:
    slot: 33
    empty_config:
      material: LEATHER_BOOTS
      custom_model_data: 1
      display_name: "<gray>No Boots</gray>"

  main_hand:
    slot: 17
    empty_config:
      material: STICK
      custom_model_data: 1
      display_name: "<gray>No Weapon</gray>"

  off_hand:
    slot: 26
    empty_config:
      material: SHIELD
      custom_model_data: 1
      display_name: "<gray>No Shield</gray>"
```
{% endcode %}

### Equipment Slots Visual

```
┌─────────────────────────────────────┐
│  [ ]  [ ]  [ ]  [ ]  [ ]  [⛑]  [ ] │  ← Helmet (slot 6)
│  [ ]  [ ]  [ ]  [ ]  [ ]  [ ]  [ ] │
│  [ ]  [ ]  [ ]  [ ]  [ ]  [⚔]  [ ] │  ← Main Hand (slot 17)
│  [ ]  [ ]  [ ]  [ ]  [ ]  [🛡]  [ ] │  ← Off Hand (slot 26)
│  [ ]  [ ]  [ ]  [ ]  [ ]  [ ]  [ ] │
└─────────────────────────────────────┘
```

## Custom Items

### Item Types

There are 2 types of custom items:

#### 1. Cosmetic Items

Display cosmetics from HMCCosmetics:

{% code title="Cosmetic Item Example" %}
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
```
{% endcode %}

**Available cosmetic types:**
- `HELMET`
- `CHESTPLATE`
- `LEGGINGS`
- `BOOTS`
- `BACKPACK`
- `BALLOON`
- `OFFHAND`

#### 2. Action Items

Interactive buttons that execute actions when clicked:

{% code title="Action Item Example" %}
```yaml
customs_items:
  star_button:
    slot: 31
    material: NETHER_STAR
    display_name: "<gold>⭐ <#ffd13b><bold>GIVE STAR"
    lore:
      - "<yellow>Stars: %uprofiles_star%"
      - ""
      - "<gray>Click to give this player a star!"
    actions:
      LEFT_CLICK:
        action_1:
          action: ADD_STAR
      RIGHT_CLICK:
        action_1:
          action: REMOVE_STAR
```
{% endcode %}

## Actions System

### Available Actions

| Action | Description | Example Usage |
|--------|-------------|---------------|
| `COMMAND` | Execute as console | Admin commands |
| `COMMAND_PLAYER` | Execute as viewer | Player commands |
| `ADD_STAR` | Give star to player | Star system |
| `REMOVE_STAR` | Remove star from player | Star system |
| `CLOSE` | Close the GUI | Exit button |
| `NONE` | Do nothing | Decoration items |

### Action Examples

{% tabs %}
{% tab title="Console Command" %}
{% code title="Execute commands as console" %}
```yaml
trade_button:
  slot: 21
  material: GOLD_BLOCK
  display_name: "<#ffb03b><bold>TRADE"
  actions:
    LEFT_CLICK:
      action_1:
        action: COMMAND
        commands:
          - "eco give %viewer_name% 100"
          - "broadcast %viewer_name% got 100 coins!"
```
{% endcode %}
{% endtab %}

{% tab title="Player Command" %}
{% code title="Execute commands as the viewer" %}
```yaml
message_button:
  slot: 28
  material: WRITABLE_BOOK
  display_name: "<aqua>✉ Send Message"
  actions:
    LEFT_CLICK:
      action_1:
        action: COMMAND_PLAYER
        commands:
          - "msg %player_name% Hello!"
```
{% endcode %}
{% endtab %}

{% tab title="Star System" %}
{% code title="Add/Remove stars" %}
```yaml
star_button:
  slot: 31
  material: NETHER_STAR
  display_name: "<gold>⭐ GIVE STAR"
  lore:
    - "<yellow>Stars: %uprofiles_star%"
  actions:
    LEFT_CLICK:
      action_1:
        action: ADD_STAR
    RIGHT_CLICK:
      action_1:
        action: REMOVE_STAR
```
{% endcode %}
{% endtab %}
{% endtabs %}

## Advanced Features

### Multiple Slots

Fill multiple slots with the same item:

{% code title="Multiple Slots Example" %}
```yaml
customs_items:
  info_bar:
    slots:  # Use 'slots' instead of 'slot'
      - 3-5  # Fills slots 3, 4, 5
      - 10-12  # Fills slots 10, 11, 12
    material: SUGAR
    custom_model_data: 991
    display_name: "<#8cdbab><bold>INFORMATION"
```
{% endcode %}

### Player Heads

Display custom player heads:

{% code title="Player Head Example" %}
```yaml
customs_items:
  player_head:
    material: PLAYER_HEAD
    head: "%player_name%"  # Player name for skull texture
    slot: 0
    display_name: "<gold>%player_name%"
    lore:
      - "<gray>Rank: %luckperms_groups%"
      - "<gray>Balance: $%vault_eco_balance%"
```
{% endcode %}

### Hide Tooltips & Attributes

{% code title="Clean Item Display" %}
```yaml
customs_items:
  decoration:
    material: PLAYER_HEAD
    head: "%player_name%"
    slot: 0
    hideTooltip: true      # Hide entire tooltip
    hideAttributes: true   # Hide attribute modifiers
```
{% endcode %}

### Resource Pack Integration

#### Custom Model Data (1.20.5+)

Use custom model data for resource pack items:

{% code title="Custom Model Data" %}
```yaml
customs_items:
  custom_icon:
    material: SUGAR
    custom_model_data: 991  # Your custom model ID
    slot: 3
    display_name: "<gradient:#00ff00:#00ffff>Custom Item"
```
{% endcode %}

#### Item Model (1.21.2+)

{% hint style="success" %}
**New Feature:** Minecraft 1.21.2+ introduces `item_model` as a replacement for `custom_model_data`, providing better resource pack support.
{% endhint %}

Use item models for modern Minecraft versions:

{% code title="Item Model Example" %}
```yaml
customs_items:
  custom_sword:
    material: DIAMOND_SWORD
    item_model: "mynamespace:custom_sword"  # Format: "namespace:model_name"
    slot: 10
    display_name: "<gradient:#ff0000:#ffaa00>Legendary Sword"
    lore:
      - "<gray>A custom weapon from a resource pack"
```
{% endcode %}

{% hint style="info" %}
**Item Model Format:**
- Must use namespaced key format: `"namespace:model_name"`
- Example: `"minecraft:custom_helmet"` or `"mypack:special_item"`
- Backward compatible: Falls back gracefully on older versions
{% endhint %}

**Both methods supported:**
```yaml
customs_items:
  legacy_support:
    material: SUGAR
    custom_model_data: 991      # For 1.20.5+
    item_model: "mypack:icon"   # For 1.21.2+
    slot: 5
    display_name: "<green>Universal Custom Item"
```

The plugin will automatically use the appropriate method based on the server version.

## Complete GUI Example

{% code title="Example: Social Profile GUI" %}
```yaml
title: "<shift:-8><glyph:gui_profile>"
size: 36

# Equipment Display
equipments:
  helmet:
    slot: 6
  chestplate:
    slot: 15
  leggings:
    slot: 24
  boots:
    slot: 33
  main_hand:
    slot: 17
  off_hand:
    slot: 26

# Custom Items
customs_items:
  # Player Head
  player_head:
    material: PLAYER_HEAD
    head: "%player_name%"
    hideAttributes: true
    slot: 0
    display_name: "<gold><bold>%player_name%"
    lore:
      - ""
      - "<gray>Rank: <yellow>%luckperms_groups%"
      - "<gray>Balance: <green>$%vault_eco_balance%"
      - ""
      - "<yellow>⭐ Stars: %uprofiles_star%"

  # Cosmetics
  cosmetic_helmet:
    type: HELMET
    slot: 7
    empty_config:
      material: LEATHER_HELMET
      custom_model_data: 1
      display_name: "<gray>No Helmet</gray>"

  cosmetic_backpack:
    type: BACKPACK
    slot: 16
    empty_config:
      material: LEATHER_HORSE_ARMOR
      display_name: "<gray>No Backpack</gray>"

  # Action Buttons
  star_button:
    slot: 31
    material: NETHER_STAR
    display_name: "<gold>⭐ GIVE STAR"
    lore:
      - "<yellow>Total Stars: %uprofiles_star%"
      - ""
      - "<green>Left Click</green> <gray>to give star"
      - "<red>Right Click</red> <gray>to remove star"
    actions:
      LEFT_CLICK:
        action_1:
          action: ADD_STAR
      RIGHT_CLICK:
        action_1:
          action: REMOVE_STAR

  friend_button:
    slot: 22
    material: PLAYER_HEAD
    head: "%player_name%"
    display_name: "<green>👤 Add Friend"
    lore:
      - "<gray>Send a friend request"
    actions:
      LEFT_CLICK:
        action_1:
          action: COMMAND
          commands:
            - "friend add %player_name%"

  trade_button:
    slot: 29
    material: EMERALD
    display_name: "<yellow>💰 Trade"
    actions:
      LEFT_CLICK:
        action_1:
          action: COMMAND
          commands:
            - "trade %player_name%"
```
{% endcode %}

## Slot Numbers Reference

{% hint style="info" %}
**GUI Slot Layout** (36 slots):
{% endhint %}

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

## Screenshots

<figure><img src="../.gitbook/assets/gui-example-1.png" alt="Profile GUI with Equipment"><figcaption><p>Example profile showing equipment and cosmetics</p></figcaption></figure>

<figure><img src="../.gitbook/assets/gui-example-2.png" alt="Profile GUI with Actions"><figcaption><p>Example profile with action buttons</p></figcaption></figure>

## Next Steps

{% content-ref url="../features/custom-items.md" %}
[custom-items.md](../features/custom-items.md)
{% endcontent-ref %}

{% content-ref url="../examples/gui-examples.md" %}
[gui-examples.md](../examples/gui-examples.md)
{% endcontent-ref %}
