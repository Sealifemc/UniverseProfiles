---
description: All available commands and their usage
---

# 📝 Commands

## Command Overview

UniverseProfiles has a simple command structure focused on viewing profiles.

## Player Commands

### `/profile`

Open your own profile GUI.

{% code title="Command Usage" %}
```
/profile
```
{% endcode %}

**Permission:** `uprofiles.open`
**Aliases:** None

{% hint style="success" %}
**Example:**
Type `/profile` to see your own profile with equipment, stats, and cosmetics.
{% endhint %}

---

### `/profile <player>`

Open another player's profile GUI.

{% code title="Command Usage" %}
```
/profile <player>
```
{% endcode %}

**Permission:** `uprofiles.open`
**Arguments:**
- `<player>` - The player name (online or offline)

{% tabs %}
{% tab title="Example: Online Player" %}
```
/profile Steve
```
Opens Steve's profile (if he's online)
{% endtab %}

{% tab title="Example: Offline Player" %}
```
/profile Notch
```
Opens Notch's profile from database cache
{% endtab %}
{% endtabs %}

{% hint style="info" %}
**Privacy:** Players with private profiles can only be viewed by users with `uprofiles.private.bypass` permission.
{% endhint %}

---

## Admin Commands

### `/profile reload`

Reload all configuration files without restarting the server.

{% code title="Command Usage" %}
```
/profile reload
```
{% endcode %}

**Permission:** `uprofiles.reload`

**What gets reloaded:**
- ✅ `config.yml` - Main configuration
- ✅ `gui.yml` - GUI layout and items
- ✅ Language files (`en_US.yml`, `fr_FR.yml`)
- ❌ Database connections (requires restart)

{% hint style="warning" %}
**Database changes require a server restart!**
{% endhint %}

---

### `/profile public`

Make your profile public (visible to everyone).

{% code title="Command Usage" %}
```
/profile public
```
{% endcode %}

**Permission:** `uprofiles.public`

---

### `/profile private`

Make your profile private (only visible to admins).

{% code title="Command Usage" %}
```
/profile private
```
{% endcode %}

**Permission:** `uprofiles.private`

---

### `/profile bio <text>`

Set your profile biography.

{% code title="Command Usage" %}
```
/profile bio <text>
```
{% endcode %}

**Arguments:**
- `<text>` - Your biography text (max 200 characters by default)

{% hint style="info" %}
The bio automatically wraps to multiple lines in the GUI based on the `line_length` setting.
{% endhint %}

---

### `/profile clearbio`

Clear your profile biography.

{% code title="Command Usage" %}
```
/profile clearbio
```
{% endcode %}

---

### `/profile admin open <player> [viewer]`

Force open a player's profile for a specific viewer.

{% code title="Command Usage" %}
```
/profile admin open <player> [viewer]
```
{% endcode %}

**Permission:** `uprofiles.admin`

**Arguments:**
- `<player>` - The profile owner
- `[viewer]` - Optional, the player who will see the profile (defaults to command sender)

---

### `/profile admin forcepublic <player>`

Force a player's profile to be public.

{% code title="Command Usage" %}
```
/profile admin forcepublic <player>
```
{% endcode %}

**Permission:** `uprofiles.admin`

---

### `/profile admin forceprivate <player>`

Force a player's profile to be private.

{% code title="Command Usage" %}
```
/profile admin forceprivate <player>
```
{% endcode %}

**Permission:** `uprofiles.admin`

---

### `/profile admin give star <player> <amount>`

Give stars to a player (bypasses the one-star-per-player limit).

{% code title="Command Usage" %}
```
/profile admin give star <player> <amount>
```
{% endcode %}

**Permission:** `uprofiles.admin`

**Arguments:**
- `<player>` - The player to receive stars
- `<amount>` - Number of stars to give

---

## Command Examples

### Basic Usage

{% code title="Common Commands" %}
```bash
# View your own profile
/profile

# View another player's profile
/profile Steve

# Make your profile public
/profile public

# Make your profile private
/profile private

# Reload config (admin only)
/profile reload
```
{% endcode %}

### Admin Workflow

{% code title="Admin Commands" %}
```bash
# 1. Edit gui.yml or config.yml
nano plugins/UniverseProfiles/gui.yml

# 2. Reload without restart
/profile reload

# 3. Test changes
/profile Steve

# 4. View private profiles (bypass permission)
/profile PlayerWithPrivateProfile
```
{% endcode %}

## Tab Completion

UniverseProfiles supports tab completion for all commands:

```bash
/profile <TAB>          # Shows online player names + reload/public/private
/profile St<TAB>        # Autocompletes to "Steve"
/profile r<TAB>         # Autocompletes to "reload"
```

## Permission Integration

### Console Commands

You can execute profile commands from console:

{% code title="Console Examples" %}
```bash
# Open Steve's profile for Alex
profile Steve Alex

# Reload config from console
profile reload
```
{% endcode %}

### Command Blocks

{% hint style="info" %}
**Command blocks are supported!**
{% endhint %}

```
/profile @p
/profile Steve
```

## Error Messages

Common error messages and their meaning:

| Error | Meaning | Solution |
|-------|---------|----------|
| `Player not found` | Player name doesn't exist | Check spelling |
| `No permission` | Missing required permission | Add permission node |
| `Profile is private` | Target has private profile | Need `uprofiles.private.bypass` |
| `Database error` | Can't access database | Check database connection |

## Next Steps

{% content-ref url="permissions.md" %}
[permissions.md](permissions.md)
{% endcontent-ref %}

{% content-ref url="../features/star-system.md" %}
[star-system.md](../features/star-system.md)
{% endcontent-ref %}
