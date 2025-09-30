# Conditions System

UniverseProfiles includes a powerful conditions system that allows you to show/hide items and change GUI titles based on permissions or placeholder values.

## Overview

Conditions can be applied to:
- **Custom items** - Show different items on the same slot based on conditions
- **GUI titles** - Display different titles based on player rank/permissions

## Condition Types

### Permission Condition

Check if a player has a specific permission.

```yaml
conditions:
  check_vip:
    type: permission
    target: viewer|target
    permission: "rank.vip"
    negate: false  # Optional, inverts the check
```

### Placeholder Condition

Check PlaceholderAPI placeholder values.

```yaml
conditions:
  check_level:
    type: placeholder
    target: viewer|target
    placeholder: "%player_level%"
    operation: ">="
    value: "50"
```

## Target Options

| Target | Description |
|--------|-------------|
| `viewer` | The player who is viewing the GUI |
| `target` | The profile owner being viewed |

## Operations

### String Operations

| Operation | Description | Example |
|-----------|-------------|---------|
| `equals` | Exact match | `"admin"` = `"admin"` |
| `not_equals` / `!=` | Not equal | `"user"` != `"admin"` |
| `contains` | Contains substring | `"hello world"` contains `"world"` |
| `not_contains` | Does not contain | `"hello"` not contains `"xyz"` |
| `starts_with` | Starts with | `"admin_user"` starts with `"admin"` |
| `ends_with` | Ends with | `"user_admin"` ends with `"admin"` |

### Numeric Operations

| Operation | Description | Example |
|-----------|-------------|---------|
| `greater_than` / `>` | Greater than | `50 > 40` |
| `less_than` / `<` | Less than | `30 < 40` |
| `greater_or_equal` / `>=` | Greater or equal | `40 >= 40` |
| `less_or_equal` / `<=` | Less or equal | `30 <= 40` |

## Conditional Items

Multiple items can be placed on the same slot. The first item whose conditions pass will be displayed.

Use `priority` to control evaluation order (higher = checked first).

```yaml
customs_items:
  # VIP button - shown only for VIP players (priority 10)
  vip_button:
    slot: 32
    priority: 10
    material: DIAMOND
    display_name: "<aqua><bold>VIP Menu"
    conditions:
      is_vip:
        type: permission
        target: viewer
        permission: "vip.access"

  # Normal button - fallback for non-VIP (priority 0)
  normal_button:
    slot: 32
    priority: 0
    material: COAL
    display_name: "<gray>Become VIP"
```

## Conditional Titles

Display different GUI titles based on conditions.

```yaml
titles:
  default: "<shift:-8><glyph:gui_profile>"

  vip_title:
    priority: 10
    title: "<shift:-8><glyph:gui_profile><shift:-80><glyph:vip_icon>"
    conditions:
      is_vip:
        type: permission
        target: target
        permission: "rank.vip"

  admin_title:
    priority: 20
    title: "<shift:-8><glyph:gui_profile><shift:-80><glyph:admin_icon>"
    conditions:
      is_admin:
        type: placeholder
        target: target
        placeholder: "%luckperms_primary_group_name%"
        operation: "equals"
        value: "admin"
```

## Multiple Conditions

You can add multiple conditions to an item. ALL conditions must pass for the item to be shown.

```yaml
conditions:
  check_vip:
    type: permission
    target: viewer
    permission: "vip.access"
  check_level:
    type: placeholder
    target: target
    placeholder: "%player_level%"
    operation: ">="
    value: "10"
```

## Examples

### Show item only to admins viewing their own profile

```yaml
admin_self_item:
  slot: 10
  material: COMMAND_BLOCK
  display_name: "<red>Admin Panel"
  conditions:
    is_admin:
      type: permission
      target: viewer
      permission: "admin.panel"
    is_self:
      type: placeholder
      target: viewer
      placeholder: "%player_name%"
      operation: "equals"
      value: "%player_name%"
```

### Different items based on LuckPerms group

```yaml
# For admin group
admin_badge:
  slot: 5
  priority: 30
  material: NETHER_STAR
  display_name: "<red>Admin"
  conditions:
    group_check:
      type: placeholder
      target: target
      placeholder: "%luckperms_primary_group_name%"
      operation: "equals"
      value: "admin"

# For VIP group
vip_badge:
  slot: 5
  priority: 20
  material: DIAMOND
  display_name: "<aqua>VIP"
  conditions:
    group_check:
      type: placeholder
      target: target
      placeholder: "%luckperms_primary_group_name%"
      operation: "equals"
      value: "vip"

# Default badge
default_badge:
  slot: 5
  priority: 0
  material: STONE
  display_name: "<gray>Member"
```
