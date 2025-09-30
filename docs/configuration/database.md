# Database Configuration

UniverseProfiles supports multiple database backends for storing player profiles.

## Supported Types

| Type | Best For | Requirements |
|------|----------|--------------|
| `SQLITE` | Small servers, single instance | None (built-in) |
| `MYSQL` | Networks, multiple servers | MySQL server |
| `REDIS` | High performance caching | Redis + MySQL |

## SQLite (Default)

No configuration required. Data stored in `plugins/UniverseProfiles/profiles.db`.

```yaml
database:
  type: SQLITE
```

## MySQL

```yaml
database:
  type: MYSQL
  mysql:
    host: localhost
    port: 3306
    database: universe_profiles
    username: root
    password: "your_password"
```

### Features
- HikariCP connection pooling (max 10 connections)
- 30 second connection timeout
- Prepared statement caching

## Redis

Redis acts as a high-performance cache layer with MySQL as persistent storage.

```yaml
database:
  type: REDIS
  mysql:
    host: localhost
    port: 3306
    database: universe_profiles
    username: root
    password: ""
  redis:
    host: localhost
    port: 6379
    password: ""  # Leave empty if no auth
```

### Features
- Fast read/write operations
- Automatic MySQL fallback
- JedisPool with max 10 connections

## Database Schema

### profiles table

```sql
CREATE TABLE profiles (
  uuid VARCHAR(36) PRIMARY KEY,
  player_name VARCHAR(16),
  bio VARCHAR(500) DEFAULT '',
  is_public BOOLEAN DEFAULT TRUE,
  stars INTEGER DEFAULT 0,
  star_givers TEXT,
  inventory TEXT,
  armor TEXT,
  main_hand TEXT,
  off_hand TEXT,
  last_seen BIGINT
)
```

### placeholder_cache table

```sql
CREATE TABLE placeholder_cache (
  uuid TEXT,
  placeholder TEXT,
  value TEXT,
  updated_at INTEGER,
  PRIMARY KEY (uuid, placeholder)
)
```

## Auto-Save

Profiles are automatically saved at a configurable interval:

```yaml
settings:
  save_interval_minutes: 5
```

## Caching

### In-Memory Cache

Profiles are cached in memory using `ConcurrentHashMap` for fast access.

```yaml
settings:
  cache_offline_players: true  # Keep profiles cached after logout
```

### Placeholder Cache

Certain placeholders can be cached when players disconnect for offline profile viewing:

```yaml
settings:
  cached_placeholders:
    - "%hmccosmetics_total%"
    - "%hmccosmetics_equipped%"
    - "%player_level%"
```

## Migration

When changing database types, you'll need to manually migrate data. The plugin does not automatically transfer data between database types.
