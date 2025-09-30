---
description: Complete permissions reference for UniverseProfiles
---

# 🔐 Permissions

## Overview

UniverseProfiles uses a simple permission system to control access to features.

## Permission List

### Player Permissions

| Permission | Description | Default | Command |
|------------|-------------|---------|----------|
| `uprofiles.open` | Open profiles | `op` | `/profile [player]` |
| `uprofiles.public` | Make profile public | `op` | `/profile public` |
| `uprofiles.private` | Make profile private | `op` | `/profile private` |

### Admin Permissions

| Permission | Description | Default | Feature |
|------------|-------------|---------|---------|
| `uprofiles.admin` | Access to all admin commands | `op` | All commands |
| `uprofiles.reload` | Reload configuration | `op` | `/profile reload` |
| `uprofiles.private.bypass` | View private profiles | `op` | Bypass privacy |

## Recommended LuckPerms Setup

{% code title="Complete LuckPerms Setup" %}
```bash
# === PLAYERS (default) ===
lp group default permission set uprofiles.open true
lp group default permission set uprofiles.public true
lp group default permission set uprofiles.private true

# === HELPERS/MODERATORS ===
lp group helper permission set uprofiles.private.bypass true

# === ADMINS ===
lp group admin permission set uprofiles.admin true
lp group admin permission set uprofiles.reload true
```
{% endcode %}

## Permission Details

### `uprofiles.open`

Allows players to open their own profile and view others' profiles.

**Use cases:**
- ✅ Open own profile: `/profile`
- ✅ View other profiles: `/profile Steve`
- ❌ View private profiles (requires `uprofiles.private.bypass`)

{% tabs %}
{% tab title="LuckPerms" %}
{% code title="Give to all players" %}
```bash
lp group default permission set uprofiles.open true
```
{% endcode %}
{% endtab %}

{% tab title="PermissionsEx" %}
{% code title="Give to all players" %}
```bash
pex group default add uprofiles.open
```
{% endcode %}
{% endtab %}
{% endtabs %}

### `uprofiles.public` & `uprofiles.private`

Allows players to control their profile privacy.

{% code title="LuckPerms - Recommended" %}
```bash
lp group default permission set uprofiles.public true
lp group default permission set uprofiles.private true
```
{% endcode %}

### `uprofiles.private.bypass`

Allows viewing all profiles, even private ones.

**Recommendation:** Only give to moderators and administrators.

{% code title="LuckPerms - Staff only" %}
```bash
lp group moderator permission set uprofiles.private.bypass true
lp group admin permission set uprofiles.private.bypass true
```
{% endcode %}

### `uprofiles.reload`

Allows reloading the plugin configuration without restart.

{% code title="LuckPerms - Admins only" %}
```bash
lp group admin permission set uprofiles.reload true
```
{% endcode %}

### `uprofiles.admin`

**Wildcard permission** - grants all plugin permissions.

{% code title="LuckPerms - Admin wildcard" %}
```bash
lp group admin permission set uprofiles.admin true
```
{% endcode %}

## Configuration Examples

### Public Server

All players can view and manage their profiles:

{% code title="LuckPerms - Public Server Setup" %}
```bash
# Regular players
lp group default permission set uprofiles.open true
lp group default permission set uprofiles.public true
lp group default permission set uprofiles.private true

# Moderators
lp group moderator permission set uprofiles.private.bypass true

# Admins
lp group admin permission set uprofiles.admin true
```
{% endcode %}

### VIP/Premium Server

Only VIP members can view profiles:

{% code title="LuckPerms - VIP Only Setup" %}
```bash
# VIP members only
lp group vip permission set uprofiles.open true

# Staff can bypass
lp group helper permission set uprofiles.private.bypass true

# Admins get all
lp group admin permission set uprofiles.admin true
```
{% endcode %}

### BungeeCord Network

Sync permissions across all servers:

{% code title="LuckPerms - Network Setup" %}
```bash
# Global for all servers
lp group default permission set uprofiles.open true

# Or per-server
lp group default permission set uprofiles.open true server=lobby
lp group default permission set uprofiles.open true server=survival
```
{% endcode %}

## Permission Hierarchy

```
uprofiles.admin (wildcard - grants all)
├── uprofiles.reload
├── uprofiles.private.bypass
├── uprofiles.open
├── uprofiles.public
└── uprofiles.private
```

{% hint style="info" %}
**Note:** `uprofiles.admin` automatically inherits all other permissions.
{% endhint %}

## Testing Permissions

### In-game

Test if a player has a permission:

```bash
/lp user Steve permission check uprofiles.open
/pex user Steve has uprofiles.private.bypass
```

### Via console

```bash
# LuckPerms
lp user Steve permission info

# PermissionsEx
pex user Steve
```

## Troubleshooting

### Error: "No permission"

{% hint style="danger" %}
```
You don't have permission to use this command!
```
{% endhint %}

**Solutions:**
1. Check player has `uprofiles.open` permission
2. Verify permission plugin is installed (LuckPerms recommended)
3. Test with OP: `/op PlayerName`

### Error: "Profile is private"

{% hint style="warning" %}
```
This player's profile is private!
```
{% endhint %}

**Solutions:**
1. Profile is private → Ask player to do `/profile public`
2. Give viewer `uprofiles.private.bypass` to bypass

### Permissions not applying

{% hint style="info" %}
**Checklist:**
{% endhint %}

- [ ] Permission plugin installed? (LuckPerms recommended)
- [ ] Permission given to correct group?
- [ ] Player in correct group?
- [ ] Server restarted after changes?
- [ ] Tested with `/op`?

## See Also

{% content-ref url="commands.md" %}
[commands.md](commands.md)
{% endcontent-ref %}

{% content-ref url="../features/star-system.md" %}
[star-system.md](../features/star-system.md)
{% endcontent-ref %}
