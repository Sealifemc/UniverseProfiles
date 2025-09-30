package fr.ax_dev.universeProfiles.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.ax_dev.universeProfiles.UniverseProfiles;
import fr.ax_dev.universeProfiles.models.PlayerProfile;
import fr.ax_dev.universeProfiles.utils.ItemSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

    private final UniverseProfiles plugin;
    private final DatabaseType type;
    private HikariDataSource dataSource;
    private JedisPool jedisPool;

    public DatabaseManager(UniverseProfiles plugin, DatabaseType type) {
        this.plugin = plugin;
        this.type = type;
    }

    public void initialize() {
        FileConfiguration config = plugin.getConfigManager().getConfig();

        switch (type) {
            case SQLITE -> initializeSQLite();
            case MYSQL -> initializeMySQL(config);
            case REDIS -> initializeRedis(config);
        }

        if (type != DatabaseType.REDIS) {
            createTables();
        }
    }

    private void initializeSQLite() {
        HikariConfig hikariConfig = new HikariConfig();
        File dbFile = new File(plugin.getDataFolder(), "profiles.db");
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        hikariConfig.setDriverClassName("org.sqlite.JDBC");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setConnectionTimeout(30000);
        this.dataSource = new HikariDataSource(hikariConfig);
    }

    private void initializeMySQL(FileConfiguration config) {
        HikariConfig hikariConfig = new HikariConfig();
        String host = config.getString("database.mysql.host", "localhost");
        int port = config.getInt("database.mysql.port", 3306);
        String database = config.getString("database.mysql.database", "universe_profiles");
        String username = config.getString("database.mysql.username", "root");
        String password = config.getString("database.mysql.password", "");

        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true");
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.dataSource = new HikariDataSource(hikariConfig);
    }

    private void initializeRedis(FileConfiguration config) {
        String host = config.getString("database.redis.host", "localhost");
        int port = config.getInt("database.redis.port", 6379);
        String password = config.getString("database.redis.password", "");

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);

        if (password != null && !password.isEmpty()) {
            this.jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
        } else {
            this.jedisPool = new JedisPool(poolConfig, host, port, 2000);
        }
    }

    private void createTables() {
        String createProfilesTable = """
            CREATE TABLE IF NOT EXISTS profiles (
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
            """;

        String addBioColumn = type == DatabaseType.MYSQL
                ? "ALTER TABLE profiles ADD COLUMN IF NOT EXISTS bio VARCHAR(500) DEFAULT ''"
                : "ALTER TABLE profiles ADD COLUMN bio VARCHAR(500) DEFAULT ''";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createProfilesTable);
            try {
                stmt.execute(addBioColumn);
            } catch (SQLException ignored) {}
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create database tables: " + e.getMessage());
        }
    }

    public CompletableFuture<PlayerProfile> loadProfile(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            if (type == DatabaseType.REDIS) {
                return loadFromRedis(uuid);
            } else {
                return loadFromSQL(uuid);
            }
        });
    }

    private PlayerProfile loadFromSQL(UUID uuid) {
        String query = "SELECT * FROM profiles WHERE uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                PlayerProfile profile = new PlayerProfile(uuid, rs.getString("player_name"));
                profile.setBio(rs.getString("bio"));
                profile.setPublic(rs.getBoolean("is_public"));
                profile.setStars(rs.getInt("stars"));

                String starGiversStr = rs.getString("star_givers");
                if (starGiversStr != null && !starGiversStr.isEmpty()) {
                    Set<UUID> starGivers = new HashSet<>();
                    for (String uuidStr : starGiversStr.split(",")) {
                        starGivers.add(UUID.fromString(uuidStr));
                    }
                    profile.setStarGivers(starGivers);
                }

                String inventoryStr = rs.getString("inventory");
                if (inventoryStr != null) {
                    profile.setInventory(ItemSerializer.deserializeInventory(inventoryStr));
                }

                String armorStr = rs.getString("armor");
                if (armorStr != null) {
                    profile.setArmorContents(ItemSerializer.deserializeArmor(armorStr));
                }

                String mainHandStr = rs.getString("main_hand");
                if (mainHandStr != null) {
                    profile.setMainHand(ItemSerializer.deserializeItem(mainHandStr));
                }

                String offHandStr = rs.getString("off_hand");
                if (offHandStr != null) {
                    profile.setOffHand(ItemSerializer.deserializeItem(offHandStr));
                }

                profile.setLastSeen(rs.getLong("last_seen"));
                return profile;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load profile from database: " + e.getMessage());
        }
        return null;
    }

    private PlayerProfile loadFromRedis(UUID uuid) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> data = jedis.hgetAll("profile:" + uuid.toString());
            if (!data.isEmpty()) {
                PlayerProfile profile = new PlayerProfile(uuid, data.get("player_name"));
                profile.setBio(data.getOrDefault("bio", ""));
                profile.setPublic(Boolean.parseBoolean(data.get("is_public")));
                profile.setStars(Integer.parseInt(data.getOrDefault("stars", "0")));

                String starGiversStr = data.get("star_givers");
                if (starGiversStr != null && !starGiversStr.isEmpty()) {
                    Set<UUID> starGivers = new HashSet<>();
                    for (String uuidStr : starGiversStr.split(",")) {
                        starGivers.add(UUID.fromString(uuidStr));
                    }
                    profile.setStarGivers(starGivers);
                }

                String inventoryStr = data.get("inventory");
                if (inventoryStr != null) {
                    profile.setInventory(ItemSerializer.deserializeInventory(inventoryStr));
                }

                String armorStr = data.get("armor");
                if (armorStr != null) {
                    profile.setArmorContents(ItemSerializer.deserializeArmor(armorStr));
                }

                String mainHandStr = data.get("main_hand");
                if (mainHandStr != null) {
                    profile.setMainHand(ItemSerializer.deserializeItem(mainHandStr));
                }

                String offHandStr = data.get("off_hand");
                if (offHandStr != null) {
                    profile.setOffHand(ItemSerializer.deserializeItem(offHandStr));
                }

                profile.setLastSeen(Long.parseLong(data.getOrDefault("last_seen", "0")));
                return profile;
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load profile from Redis: " + e.getMessage());
        }
        return null;
    }

    public CompletableFuture<Void> saveProfile(PlayerProfile profile) {
        return CompletableFuture.runAsync(() -> {
            if (type == DatabaseType.REDIS) {
                saveToRedis(profile);
            } else {
                saveToSQL(profile);
            }
        });
    }

    private void saveToSQL(PlayerProfile profile) {
        String query = """
            INSERT INTO profiles (uuid, player_name, bio, is_public, stars, star_givers, inventory, armor, main_hand, off_hand, last_seen)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(uuid) DO UPDATE SET
                player_name = excluded.player_name,
                bio = excluded.bio,
                is_public = excluded.is_public,
                stars = excluded.stars,
                star_givers = excluded.star_givers,
                inventory = excluded.inventory,
                armor = excluded.armor,
                main_hand = excluded.main_hand,
                off_hand = excluded.off_hand,
                last_seen = excluded.last_seen
            """;

        if (type == DatabaseType.MYSQL) {
            query = query.replace("ON CONFLICT(uuid) DO UPDATE SET", "ON DUPLICATE KEY UPDATE");
            query = query.replace("excluded.", "VALUES(").replaceAll("excluded\\.([a-z_]+)", "VALUES($1)");
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, profile.getUuid().toString());
            stmt.setString(2, profile.getPlayerName());
            stmt.setString(3, profile.getBio());
            stmt.setBoolean(4, profile.isPublic());
            stmt.setInt(5, profile.getStars());

            String starGivers = String.join(",", profile.getStarGivers().stream()
                    .map(UUID::toString).toList());
            stmt.setString(6, starGivers);

            stmt.setString(7, ItemSerializer.serializeInventory(profile.getInventory()));
            stmt.setString(8, ItemSerializer.serializeArmor(profile.getArmorContents()));
            stmt.setString(9, ItemSerializer.serializeItem(profile.getMainHand()));
            stmt.setString(10, ItemSerializer.serializeItem(profile.getOffHand()));
            stmt.setLong(11, profile.getLastSeen());

            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save profile to database: " + e.getMessage());
        }
    }

    private void saveToRedis(PlayerProfile profile) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> data = new HashMap<>();
            data.put("player_name", profile.getPlayerName());
            data.put("bio", profile.getBio());
            data.put("is_public", String.valueOf(profile.isPublic()));
            data.put("stars", String.valueOf(profile.getStars()));

            String starGivers = String.join(",", profile.getStarGivers().stream()
                    .map(UUID::toString).toList());
            data.put("star_givers", starGivers);

            data.put("inventory", ItemSerializer.serializeInventory(profile.getInventory()));
            data.put("armor", ItemSerializer.serializeArmor(profile.getArmorContents()));
            data.put("main_hand", ItemSerializer.serializeItem(profile.getMainHand()));
            data.put("off_hand", ItemSerializer.serializeItem(profile.getOffHand()));
            data.put("last_seen", String.valueOf(profile.getLastSeen()));

            jedis.hset("profile:" + profile.getUuid().toString(), data);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save profile to Redis: " + e.getMessage());
        }
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}