# Player Click to Open Profile

You can configure the plugin to open a player's profile when clicking on them.

## Configuration

In `config.yml`:

```yaml
settings:
  open_on_player_click: SHIFT_RIGHT
```

## Options

| Option | Description |
|--------|-------------|
| `SHIFT_RIGHT` | Shift + Right-click on player (default) |
| `RIGHT` | Right-click on player |
| `DISABLED` | Disable this feature |

## Permission

Requires `uprofiles.open` permission to use.

## Behavior

1. Player right-clicks (or shift+right-clicks) another player
2. Plugin checks permission
3. Plugin checks if target profile is public (or viewer has bypass)
4. Opens the target player's profile GUI

## Privacy Check

- If target profile is **private** and viewer doesn't have `uprofiles.private.bypass`:
  - Message is sent to viewer
  - GUI is not opened
- If target profile is **public** or viewer has bypass:
  - GUI opens normally
