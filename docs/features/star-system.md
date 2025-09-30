---
description: Complete guide to the star rating system
---

# ⭐ Star System

## Overview

The star system allows players to give recognition to other players by awarding them stars. It's a social feature to build reputation and community engagement.

## How it Works

### Giving Stars

Players can give stars by:
1. Opening a profile: `/profile <player>`
2. Clicking the star button in the GUI
3. **Left-click** to give a star
4. **Right-click** to remove a star (if already given)

{% hint style="success" %}
**Each player can give one star per profile**
{% endhint %}

### Receiving Stars

When a player receives a star:
- ⭐ Star count increases by 1
- 📢 Notification message sent to the player
- 💾 Data saved to database

{% code title="Example notification" %}
```
Steve gave you a star! ⭐
```
{% endcode %}

## Configuration

### Allow Self-Starring

{% code title="config.yml" %}
```yaml
settings:
  allow_self_star: false  # Set to true to allow self-starring
```
{% endcode %}

| Value | Behavior |
|-------|----------|
| `false` | Players cannot give themselves stars (recommended) |
| `true` | Players can give themselves stars |

{% hint style="warning" %}
**Recommendation:** Keep `allow_self_star: false` to prevent abuse
{% endhint %}

### Star Button in GUI

{% code title="gui.yml - Star button configuration" %}
```yaml
customs_items:
  star_button:
    slot: 31
    material: NETHER_STAR
    display_name: "<gold>⭐ <#ffd13b><bold>GIVE STAR"
    lore:
      - "<yellow>Stars: %uprofiles_star%"
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
```
{% endcode %}

## Actions

### ADD_STAR

Gives a star to the profile owner.

{% code title="Action: ADD_STAR" %}
```yaml
actions:
  LEFT_CLICK:
    action_1:
      action: ADD_STAR
```
{% endcode %}

**Checks:**
- ✅ Viewer is not the profile owner (if `allow_self_star: false`)
- ✅ Viewer hasn't already given a star
- ✅ Player has permission `uprofiles.open`

**Results:**
- Success: Star count +1, notification sent
- Already starred: Error message
- Self-star disabled: Error message

### REMOVE_STAR

Removes a previously given star.

{% code title="Action: REMOVE_STAR" %}
```yaml
actions:
  RIGHT_CLICK:
    action_1:
      action: REMOVE_STAR
```
{% endcode %}

**Checks:**
- ✅ Viewer has given a star to this profile
- ✅ Star exists in database

**Results:**
- Success: Star count -1
- No star to remove: Error message

## Language Messages

{% code title="languages/en_US.yml" %}
```yaml
messages:
  star_given: "<green>You gave a star to %player%!</green>"
  star_received: "<gold>%player% gave you a star! ⭐</gold>"
  already_starred: "<red>You already gave a star to this player!</red>"
  cannot_star_self: "<red>You cannot give yourself a star!</red>"
```
{% endcode %}

## Placeholders

Display star count anywhere with PlaceholderAPI:

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%uprofiles_star%` | Number of stars | `42` |
| `%uprofiles_stars%` | Alias of `star` | `42` |

{% code title="Usage example" %}
```yaml
# In GUI
lore:
  - "<yellow>⭐ Stars: %uprofiles_star%"

# In scoreboard
- "<gold>%player_name%"
- "<yellow>Stars: %uprofiles_star%"

# In chat
format: "&7[%uprofiles_star%⭐] &f%player_name%&8: &7%message%"
```
{% endcode %}

## Use Cases

### Reputation System

Players with more stars get benefits:

{% code title="ConditionalEvents - Reward high-star players" %}
```yaml
events:
  star_milestone_50:
    type: player_command
    conditions:
      - "%uprofiles_star% == 50"
    actions:
      default:
        - "give %player% diamond 10"
        - "broadcast &6%player% reached 50 stars!"
```
{% endcode %}

### Leaderboard

Create a hologram showing top players:

{% code title="DecentHolograms - Top stars" %}
```yaml
holograms:
  top_stars:
    lines:
      - "<gold><bold>TOP STARS"
      - ""
      - "<yellow>#1 %topstars_1_player% - %topstars_1_stars%⭐"
      - "<gray>#2 %topstars_2_player% - %topstars_2_stars%⭐"
      - "<gray>#3 %topstars_3_player% - %topstars_3_stars%⭐"
```
{% endcode %}

### Ranks Based on Stars

{% code title="LuckPerms - Auto-rank promotion" %}
```bash
# Promote players to "Trusted" at 100 stars
# Using a plugin like AutoRank or custom script

if %uprofiles_star% >= 100:
  lp user %player% parent add trusted
```
{% endcode %}

### Daily Rewards

Give rewards based on star count:

{% code title="DailyRewards integration" %}
```yaml
rewards:
  star_bonus:
    enabled: true
    conditions:
      - "%uprofiles_star% >= 25"
    rewards:
      - "eco give %player% 1000"
      - "message: &aBonus for 25+ stars!"
```
{% endcode %}

## Database Structure

Stars are stored in the database:

{% code title="Database schema (example)" %}
```sql
CREATE TABLE profiles (
    uuid VARCHAR(36) PRIMARY KEY,
    player_name VARCHAR(16),
    stars INT DEFAULT 0,
    is_public BOOLEAN DEFAULT 1
);

CREATE TABLE stars_given (
    giver_uuid VARCHAR(36),
    receiver_uuid VARCHAR(36),
    timestamp BIGINT,
    PRIMARY KEY (giver_uuid, receiver_uuid)
);
```
{% endcode %}

## Examples

### Minimalist Star Button

{% code title="Simple star button" %}
```yaml
star_button:
  slot: 31
  material: NETHER_STAR
  display_name: "<gold>⭐ Star"
  lore:
    - "<yellow>%uprofiles_star% stars"
  actions:
    LEFT_CLICK:
      action_1:
        action: ADD_STAR
```
{% endcode %}

### Detailed Star Button

{% code title="Detailed star button with instructions" %}
```yaml
star_button:
  slot: 31
  material: NETHER_STAR
  custom_model_data: 100
  display_name: "<gradient:#FFD700:#FFA500>⭐ GIVE STAR</gradient>"
  lore:
    - ""
    - "<yellow>Total Stars: %uprofiles_star%</yellow>"
    - ""
    - "<gray>Give this player recognition"
    - "<gray>by awarding them a star!"
    - ""
    - "<green>▶ Left Click</green> <dark_gray>to give star</dark_gray>"
    - "<red>▶ Right Click</red> <dark_gray>to remove star</dark_gray>"
    - ""
    - "<gold>⚠ You can only give one star per player"
  actions:
    LEFT_CLICK:
      action_1:
        action: ADD_STAR
    RIGHT_CLICK:
      action_1:
        action: REMOVE_STAR
```
{% endcode %}

### Star Display Item (No Click)

{% code title="Display-only star count" %}
```yaml
star_display:
  slot: 4
  material: NETHER_STAR
  display_name: "<gold>Reputation"
  lore:
    - "<yellow>⭐ Stars: %uprofiles_star%"
    - ""
    - "<gray>This player has been starred"
    - "<gray>by %uprofiles_star% other players!"
  actions:
    LEFT_CLICK:
      action_1:
        action: NONE
```
{% endcode %}

## Troubleshooting

### Cannot Give Star

{% hint style="danger" %}
```
You cannot give yourself a star!
```
{% endhint %}

**Solution:** This is normal if `allow_self_star: false`. Cannot star your own profile.

### Already Starred

{% hint style="warning" %}
```
You already gave a star to this player!
```
{% endhint %}

**Solution:** Each player can only give one star per profile. Use right-click to remove it first.

### Star Not Saving

{% hint style="danger" %}
Star count resets after server restart
{% endhint %}

**Solutions:**
1. Check database connection in `config.yml`
2. Verify auto-save is working: `save_interval_minutes: 5`
3. Check console for database errors

## See Also

{% content-ref url="../usage/placeholders.md" %}
[placeholders.md](../usage/placeholders.md)
{% endcontent-ref %}

{% content-ref url="../configuration/gui.yml.md" %}
[gui.yml.md](../configuration/gui.yml.md)
{% endcontent-ref %}
