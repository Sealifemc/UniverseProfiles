---
description: PlaceholderAPI integration and available placeholders
---

# 🎯 Placeholders

## Overview

UniverseProfiles integrates with **PlaceholderAPI** to display profile data anywhere on your server (scoreboard, tablist, chat, etc.).

{% hint style="info" %}
**Requirement:** [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) must be installed
{% endhint %}

## Installation

1. Download and install PlaceholderAPI
2. Restart the server
3. UniverseProfiles registers automatically

Verify it works:
```
/papi ecloud download UniverseProfiles
```

## Available Placeholders

### Identifier: `uprofiles`

All placeholders start with `%uprofiles_`

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%uprofiles_state%` | Profile state (Public/Private) | `Public` or `Private` |
| `%uprofiles_star%` | Number of stars | `42` |
| `%uprofiles_stars%` | Alias of `star` | `42` |
| `%uprofiles_ispublic%` | Boolean if public | `true` / `false` |
| `%uprofiles_isprivate%` | Boolean if private | `true` / `false` |
| `%uprofiles_player_name%` | Player name | `Steve` |
| `%uprofiles_bio%` | Player biography | `Hello, I love Minecraft!` |

### GUI-Only Placeholders

These placeholders only work inside the GUI configuration:

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%player_name%` | Profile owner name | `Steve` |
| `%viewer_name%` | GUI viewer name | `Alex` |
| `%hmccosmetics_total%` | Total cosmetics (HMCCosmetics) | `15` |
| `%hmccosmetics_equipped%` | Equipped cosmetics count | `3` |

## Usage Examples

### In GUI

{% hint style="success" %}
**New Feature:** The GUI title now supports PlaceholderAPI placeholders, including `%viewer_name%` to display the viewer's information!
{% endhint %}

{% code title="gui.yml - Using placeholders" %}
```yaml
# Title supports PlaceholderAPI (resolved for the viewer)
title: "<gold>%viewer_name%</gold> viewing <yellow>%player_name%</yellow>"

customs_items:
  player_info:
    material: PLAYER_HEAD
    head: "%player_name%"
    display_name: "<gold>%player_name%"
    lore:
      - ""
      - "<yellow>⭐ Stars: %uprofiles_star%"
      - "<gray>Status: %uprofiles_state%"
      - ""
      - "<gray>Public: %uprofiles_ispublic%"
```
{% endcode %}

{% hint style="warning" %}
**Important:** When using PlaceholderAPI placeholders in the **title**, they are resolved for the **viewer** (the person opening the GUI), not the profile owner. This allows dynamic titles showing who is viewing whose profile.
{% endhint %}

### In Scoreboard

{% code title="Scoreboard (e.g. FeatherBoard)" %}
```yaml
scoreboard:
  lines:
    - "<gold>%player_name%"
    - ""
    - "<yellow>Stars: %uprofiles_star%"
    - "<gray>Profile: %uprofiles_state%"
```
{% endcode %}

### In TAB

{% code title="TAB Plugin" %}
```yaml
tablist:
  header:
    - "<gold>Welcome %player_name%"
    - "<yellow>⭐ %uprofiles_star% stars"
  footer:
    - "<gray>Profile: %uprofiles_state%"
```
{% endcode %}

### In Chat

{% code title="Chat Plugin (e.g. ChatControl)" %}
```yaml
chat-format: "&7[%uprofiles_star%⭐] &f%player_name%&8: &7%message%"
```
{% endcode %}

Result: `[42⭐] Steve: Hello!`

### In DeluxeMenus

{% code title="DeluxeMenus - Custom GUI" %}
```yaml
items:
  profile_info:
    material: NETHER_STAR
    display_name: "<gold>Profile Info"
    lore:
      - "<yellow>Stars: %uprofiles_star%"
      - "<gray>Status: %uprofiles_state%"
```
{% endcode %}

### In Holograms

{% code title="DecentHolograms" %}
```yaml
holograms:
  top_stars:
    lines:
      - "<gold>Top Player"
      - "%player_name%"
      - "<yellow>%uprofiles_star% ⭐"
```
{% endcode %}

## Advanced Examples

### Conditional with ConditionalEvents

{% code title="ConditionalEvents - Star-based reward" %}
```yaml
events:
  reward_if_50_stars:
    type: player_command
    conditions:
      - "%uprofiles_star% >= 50"
    actions:
      default:
        - "give %player% diamond 10"
        - "message: &aYou got diamonds for 50+ stars!"
```
{% endcode %}

### Suffix with LuckPerms

{% code title="LuckPerms - Star-based suffix" %}
```bash
# Add dynamic suffix based on stars
lp user Steve meta setsuffix 100 " &7[%uprofiles_star%⭐]"
```
{% endcode %}

### Animation with Animated Scoreboard

{% code title="Animated Scoreboard" %}
```yaml
scoreboard:
  title: "<gradient:#FF0080:#8000FF>My Server</gradient>"
  lines:
    1:
      - "<gold>%player_name%"
    2:
      - ""
    3:
      - "<yellow>Stars: %uprofiles_star%"
      interval: 20
    4:
      - "<gray>Profile: %uprofiles_state%"
```
{% endcode %}

## Usage in Commands

{% code title="Execute command with placeholder" %}
```yaml
# In gui.yml
customs_items:
  rank_button:
    actions:
      LEFT_CLICK:
        action_1:
          action: COMMAND
          commands:
            - "broadcast %player_name% has %uprofiles_star% stars!"
```
{% endcode %}

## Formatting with MiniMessage

Placeholders can be combined with MiniMessage:

{% code title="Advanced formatting" %}
```yaml
lore:
  - "<gradient:#FFD700:#FFA500>⭐ Stars: %uprofiles_star%</gradient>"
  - "<rainbow>Profile: %uprofiles_state%</rainbow>"
  - "<#FF5733>Player: %uprofiles_player_name%</#FF5733>"
```
{% endcode %}

## Testing Placeholders

### In-game

```bash
/papi parse me %uprofiles_star%
/papi parse me %uprofiles_state%
/papi parse Steve %uprofiles_player_name%
```

### Via console

```bash
papi parse <player> %uprofiles_star%
```

## Combining with Other Placeholders

{% code title="Combine with Vault, LuckPerms, etc." %}
```yaml
customs_items:
  player_stats:
    lore:
      - "<yellow>⭐ Stars: %uprofiles_star%"
      - "<green>💰 Money: $%vault_eco_balance%"
      - "<blue>🎖 Rank: %luckperms_groups%"
      - "<gray>📍 World: %player_world%"
      - "<red>❤ Health: %player_health%"
```
{% endcode %}

## Placeholders in Titles/ActionBar

### Titles

{% code title="DeluxeMenus - Title with placeholder" %}
```yaml
commands:
  - "title %player% title <gold>%uprofiles_star% Stars!"
  - "title %player% subtitle <gray>Profile: %uprofiles_state%"
```
{% endcode %}

### ActionBar

{% code title="ActionBar message" %}
```yaml
- "actionbar: <yellow>⭐ %uprofiles_star% stars | %uprofiles_state%"
```
{% endcode %}

## Troubleshooting

### Placeholder returns empty

{% hint style="danger" %}
Placeholder shows nothing or `%uprofiles_star%` literally
{% endhint %}

**Solutions:**
1. Verify PlaceholderAPI is installed: `/plugins`
2. Check registration: `/papi ecloud placeholders UniverseProfiles`
3. Reload PAPI: `/papi reload`
4. Test: `/papi parse me %uprofiles_star%`

### Placeholder not updating

{% hint style="warning" %}
Placeholder shows old value
{% endhint %}

**Solutions:**
1. Data is cached → Wait for auto-save (`save_interval_minutes`)
2. Force save: `/profile reload`
3. Check cache: `cache_offline_players: true` in config.yml

## Source Code

The placeholder system is implemented in:

{% code title="ProfilePlaceholderExpansion.java" lineNumbers="true" %}
```java
public String onPlaceholderRequest(Player player, String identifier) {
    PlayerProfile profile = plugin.getProfileManager()
        .getCachedProfile(player.getUniqueId());

    return switch (identifier.toLowerCase()) {
        case "state" -> profile.isPublic() ? "Public" : "Private";
        case "star", "stars" -> String.valueOf(profile.getStars());
        case "ispublic" -> String.valueOf(profile.isPublic());
        case "isprivate" -> String.valueOf(!profile.isPublic());
        case "player_name" -> profile.getPlayerName();
        default -> null;
    };
}
```
{% endcode %}

## See Also

{% content-ref url="../features/star-system.md" %}
[star-system.md](../features/star-system.md)
{% endcontent-ref %}

{% content-ref url="../configuration/gui.yml.md" %}
[gui.yml.md](../configuration/gui.yml.md)
{% endcontent-ref %}
