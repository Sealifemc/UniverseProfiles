---
description: Step-by-step guide to install UniverseProfiles on your Minecraft server
---

# 📥 Installation

## Requirements

{% hint style="warning" %}
**Before installing, make sure your server meets these requirements:**
{% endhint %}

* **Server Software:** Paper 1.16.5+ or Folia
* **Java Version:** Java 21+
* **Optional Dependencies:**
  * PlaceholderAPI (for placeholder support)
  * HMCCosmetics 2.8.1+ (for cosmetics display)

## Download

1. Download the latest version from [SpigotMC](https://www.spigotmc.org/resources/) or [Modrinth](https://modrinth.com/)
2. Get the `.jar` file: `UniverseProfiles-1.0.jar`

## Installation Steps

### 1. Upload the Plugin

Upload `UniverseProfiles-1.0.jar` to your server's `plugins/` folder:

```bash
/your-server/
├── plugins/
│   ├── UniverseProfiles-1.0.jar  ← Place here
│   ├── PlaceholderAPI.jar (optional)
│   └── HMCCosmetics.jar (optional)
└── ...
```

### 2. Start Your Server

Start or restart your Minecraft server:

```bash
./start.sh
# or
java -jar paper.jar
```

### 3. Verify Installation

Check the console for successful loading:

```log
[UniverseProfiles] Enabling UniverseProfiles v1.0
[UniverseProfiles] Database connected: SQLITE
[UniverseProfiles] Language loaded: en_US
[UniverseProfiles] GUI configuration loaded
[UniverseProfiles] Plugin enabled successfully!
```

### 4. Check Generated Files

The plugin will generate the following structure:

```
plugins/UniverseProfiles/
├── config.yml          # Main configuration
├── gui.yml            # GUI layout and items
├── lang/
│   ├── en_US.yml     # English language file
│   └── fr_FR.yml     # French language file
└── data/
    └── profiles.db    # SQLite database (if using SQLite)
```

## Optional Dependencies

### PlaceholderAPI

For displaying placeholders in your GUI:

{% code title="Installing PlaceholderAPI" %}
```bash
# Download from SpigotMC
wget https://ci.extendedclip.com/job/PlaceholderAPI/lastSuccessfulBuild/artifact/build/libs/PlaceholderAPI.jar

# Place in plugins folder
mv PlaceholderAPI.jar plugins/
```
{% endcode %}

### HMCCosmetics

For cosmetics integration:

{% code title="Installing HMCCosmetics" %}
```bash
# Download HMCCosmetics 2.8.1+
# Place in plugins folder

# Then enable in config.yml:
cosmetics_plugin: HMCCOSMETICS
```
{% endcode %}

## First Launch Configuration

After installation, configure these essential settings:

{% code title="plugins/UniverseProfiles/config.yml" lineNumbers="true" %}
```yaml
database:
  type: SQLITE  # Change to MYSQL for networks

language: en_US  # or fr_FR

settings:
  debug: false
  allow_self_star: false
  cosmetics_plugin: HMCCOSMETICS  # or NONE
```
{% endcode %}

## Verification

Test if the plugin works correctly:

1. Join your server
2. Run the command: `/profile`
3. You should see your profile GUI

{% hint style="success" %}
**Installation complete!** Continue to [Quick Setup](quick-setup.md) for initial configuration.
{% endhint %}

## Troubleshooting

{% hint style="danger" %}
**Plugin not loading?** Check these common issues:
{% endhint %}

### Error: "Unsupported API version"

```log
[ERROR] Could not load 'UniverseProfiles-1.0.jar'
org.bukkit.plugin.InvalidDescriptionException: Unsupported API version 1.21
```

**Solution:** Update your server to Paper 1.21+ or use an older version compatible with your server.

### Error: "Java version mismatch"

```log
[ERROR] UniverseProfiles requires Java 21 or higher
```

**Solution:** Update your Java installation to Java 21+

```bash
# Check your Java version
java -version

# Should show: openjdk version "21.0.x" or higher
```

### Database Connection Failed

```log
[ERROR] Failed to connect to database
```

**Solution:** Check your database credentials in `config.yml`

## Next Steps

{% content-ref url="quick-setup.md" %}
[quick-setup.md](quick-setup.md)
{% endcontent-ref %}

{% content-ref url="../configuration/config.yml.md" %}
[config.yml.md](../configuration/config.yml.md)
{% endcontent-ref %}
