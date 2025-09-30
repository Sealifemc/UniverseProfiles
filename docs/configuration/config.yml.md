# ⚙️ config.yml

```yaml
# ====================================
# UniverseProfiles - Main Configuration
# ====================================
# Plugin for displaying player profiles with customizable GUI

# ====================================
# DATABASE SETTINGS
# ====================================
# Storage type: SQLITE, MYSQL, or REDIS
# - SQLITE: Local file database (recommended for small servers)
# - MYSQL: External MySQL database (recommended for networks)
# - REDIS: Redis cache (requires MYSQL as fallback)
database:
  type: SQLITE  # Options: SQLITE, MYSQL, REDIS

  # MySQL database configuration (used when type is MYSQL or REDIS)
  mysql:
    host: localhost
    port: 3306
    database: universe_profiles
    username: root
    password: ""

  # Redis cache configuration (used when type is REDIS)
  redis:
    host: localhost
    port: 6379
    password: ""

# ====================================
# LANGUAGE SETTINGS
# ====================================
# Language file to use (located in lang/ folder)
# Default: en_US
language: en_US

# ====================================
# PLUGIN SETTINGS
# ====================================
settings:
  # Enable debug mode
  # Shows detailed logs for troubleshooting (cosmetics, GUI actions, etc.)
  debug: false

  # Cache offline player data in memory
  # Set to false to always fetch from database
  cache_offline_players: true

  # Auto-save interval in minutes
  # How often to save cached data to database
  save_interval_minutes: 5

  # Allow players to give themselves stars
  # Set to false to prevent self-starring
  allow_self_star: false

  # Cosmetics plugin integration
  # Options: NONE, HMCCOSMETICS, ECOSMETICS
  # Set to HMCCOSMETICS to display player cosmetics in GUI
  cosmetics_plugin: HMCCOSMETICS

  # Cached placeholders (version 1.3+)
  # These placeholders will be saved when player disconnects
  # and reused when viewing offline player profiles
  cached_placeholders:
    - "%hmccosmetics_total%"
    - "%hmccosmetics_equipped%"
    - "%player_level%"

```
