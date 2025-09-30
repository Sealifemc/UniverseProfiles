# Bio System

Players can set a personal biography that displays on their profile.

## Commands

| Command | Description |
|---------|-------------|
| `/profile bio <text>` | Set your bio |
| `/profile clearbio` | Clear your bio |

## Configuration

In `config.yml`:

```yaml
settings:
  bio:
    max_length: 200        # Maximum bio length in characters
    line_length: 50        # Characters per line (auto line-break)
    default_text: "No bio set"  # Text shown when no bio is set
```

## Placeholder

Use `%uprofiles_bio%` to display the bio in GUI items.

```yaml
bio_display:
  slot: 10
  material: BOOK
  display_name: "<gold>%player_name%'s Bio"
  lore:
    - "<gray>%uprofiles_bio%"
```

## Auto Line-Break

The bio automatically wraps to multiple lines based on `line_length` setting.

Example with `line_length: 50`:
```
Input: "Hello, I am a Minecraft player who loves building and exploring new worlds!"

Output in lore:
- "Hello, I am a Minecraft player who loves"
- "building and exploring new worlds!"
```

The system tries to break at spaces to avoid cutting words.

## Storage

Bios are stored in the database with a maximum of 500 characters (VARCHAR).

## Examples

### Bio display item

```yaml
player_bio:
  slot: 22
  material: WRITABLE_BOOK
  display_name: "<yellow>Biography"
  lore:
    - ""
    - "<gray>%uprofiles_bio%"
    - ""
    - "<dark_gray>Use /profile bio to set yours!"
```

### Conditional bio display

```yaml
# Show bio if player has one
bio_with_content:
  slot: 22
  priority: 10
  material: WRITTEN_BOOK
  display_name: "<yellow>Biography"
  lore:
    - "<gray>%uprofiles_bio%"
  conditions:
    has_bio:
      type: placeholder
      target: target
      placeholder: "%uprofiles_bio%"
      operation: "not_equals"
      value: "No bio set"

# Show prompt if no bio
bio_empty:
  slot: 22
  priority: 0
  material: WRITABLE_BOOK
  display_name: "<gray>No Biography"
  lore:
    - "<dark_gray>This player hasn't set a bio yet."
```
