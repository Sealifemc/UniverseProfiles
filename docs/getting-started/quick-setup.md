---
description: Get UniverseProfiles up and running in 5 minutes
---

# ⚡ Quick Setup

## 5-Minute Setup Guide

Follow these steps to get your profile system working quickly.

## Step 1: Choose Your Database

{% tabs %}
{% tab title="SQLite (Small Servers)" %}
**Best for:** Single servers with < 100 players

{% code title="config.yml" %}
```yaml
database:
  type: SQLITE
```
{% endcode %}

{% hint style="success" %}
**No additional configuration needed!** SQLite works out of the box.
{% endhint %}
{% endtab %}

{% tab title="MySQL (Networks)" %}
**Best for:** BungeeCord/Velocity networks or large servers

{% code title="config.yml" %}
```yaml
database:
  type: MYSQL
  mysql:
    host: localhost
    port: 3306
    database: universe_profiles
    username: your_username
    password: your_password
```
{% endcode %}

{% hint style="warning" %}
**Create the database first:**
{% endhint %}

```sql
CREATE DATABASE universe_profiles;
```
{% endtab %}

{% tab title="Redis + MySQL (High Performance)" %}
**Best for:** Large networks with high traffic

{% code title="config.yml" %}
```yaml
database:
  type: REDIS
  mysql:
    host: localhost
    port: 3306
    database: universe_profiles
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: ""
```
{% endcode %}

{% hint style="info" %}
**Requires both MySQL and Redis server running**
{% endhint %}
{% endtab %}
{% endtabs %}

## Step 2: Configure Basic Settings

{% code title="config.yml" lineNumbers="true" %}
```yaml
language: en_US  # or fr_FR

settings:
  debug: false
  cache_offline_players: true
  save_interval_minutes: 5
  allow_self_star: false  # Prevent players from starring themselves
  cosmetics_plugin: HMCCOSMETICS  # or NONE if not using cosmetics
```
{% endcode %}

## Step 3: Customize GUI Title

{% code title="gui.yml" lineNumbers="true" %}
```yaml
title: "<shift:-8><glyph:gui_profile>"  # For custom resource pack
# OR use simple text:
title: "<gold>Profile - %player_name%"

size: 36  # Must be multiple of 9 (9, 18, 27, 36, 45, 54)
```
{% endcode %}

## Step 4: Set Up Permissions

Give players access to view profiles:

{% code title="LuckPerms Example" %}
```bash
# Allow all players to open profiles
lp group default permission set uprofiles.open true

# Allow admins to bypass private profiles
lp group admin permission set uprofiles.private.bypass true

# Allow admins to reload config
lp group admin permission set uprofiles.reload true
```
{% endcode %}

## Step 5: Test It!

Restart your server and test:

```
/profile          # Open your own profile
/profile Steve    # Open Steve's profile
```

{% hint style="success" %}
**It works!** Your profile system is now ready to use.
{% endhint %}

## Optional: Enable Cosmetics

If you have HMCCosmetics installed:

### 1. Enable in config.yml

{% code title="config.yml" %}
```yaml
settings:
  cosmetics_plugin: HMCCOSMETICS
```
{% endcode %}

### 2. Configure Cosmetic Slots

{% code title="gui.yml" %}
```yaml
customs_items:
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
```
{% endcode %}

## Example: Simple Profile GUI

Here's a minimal working GUI configuration:

{% code title="gui.yml" %}
```yaml
title: "<gold>Profile - %player_name%"
size: 27

# Player head
customs_items:
  player_head:
    material: PLAYER_HEAD
    head: "%player_name%"
    slot: 13
    display_name: "<gold>%player_name%"
    lore:
      - "<yellow>Stars: %uprofiles_star%"
      - "<gray>Status: %uprofiles_state%"

  # Give star button
  star_button:
    material: NETHER_STAR
    slot: 22
    display_name: "<gold>⭐ Give Star"
    lore:
      - "<gray>Click to give a star!"
    actions:
      LEFT_CLICK:
        action_1:
          action: ADD_STAR

# Equipment slots
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

## Quick Reference Card

<table>
  <thead>
    <tr>
      <th width="200">Feature</th>
      <th>Configuration File</th>
      <th>Section</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Database Type</td>
      <td><code>config.yml</code></td>
      <td><code>database.type</code></td>
    </tr>
    <tr>
      <td>Language</td>
      <td><code>config.yml</code></td>
      <td><code>language</code></td>
    </tr>
    <tr>
      <td>GUI Layout</td>
      <td><code>gui.yml</code></td>
      <td><code>customs_items</code></td>
    </tr>
    <tr>
      <td>Star System</td>
      <td><code>config.yml</code></td>
      <td><code>settings.allow_self_star</code></td>
    </tr>
    <tr>
      <td>Cosmetics</td>
      <td><code>config.yml</code></td>
      <td><code>settings.cosmetics_plugin</code></td>
    </tr>
  </tbody>
</table>

## Next Steps

{% content-ref url="../configuration/gui.yml.md" %}
[gui.yml.md](../configuration/gui.yml.md)
{% endcontent-ref %}

{% content-ref url="../features/star-system.md" %}
[star-system.md](../features/star-system.md)
{% endcontent-ref %}

{% content-ref url="../examples/gui-examples.md" %}
[gui-examples.md](../examples/gui-examples.md)
{% endcontent-ref %}
