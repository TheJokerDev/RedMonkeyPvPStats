package me.j0keer.redmonkeypvpstats.database.type;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import me.j0keer.redmonkeypvpstats.Main;
import me.j0keer.redmonkeypvpstats.database.Database;
import me.j0keer.redmonkeypvpstats.type.DataPlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.UUID;

public class MySQL extends Database {

    private HikariDataSource ds;
    private final String TABLE_DATA = "player_data";

    public MySQL(Main plugin) {
        super(plugin);
    }

    @Override
    protected String getType() {
        return "MySQL";
    }

    @Override
    public void connect() {
        try {
            this.setConnectionArguments();
        } catch (RuntimeException var3) {
            if (var3 instanceof IllegalArgumentException) {
                plugin.console("{prefix}&4&lERROR: &cInvalid database arguments! Please check your configuration!",
                        "If this error persists, please report it to the developer!");
                throw new IllegalArgumentException(var3);
            }

            if (var3 instanceof HikariPool.PoolInitializationException) {
                plugin.console("{prefix}&4&lERROR: &cCan't initialize database connection! Please check your configuration!",
                        "If this error persists, please report it to the developer!");
                throw new HikariPool.PoolInitializationException(var3);
            }

            plugin.console("{prefix}&4&lERROR: Can't use the Hikari Connection Pool! Please, report this error to the developer!");
            throw var3;
        }


        try {
            this.setupConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupConnection() throws SQLException {
        Connection var1 = ds.getConnection();
        Throwable var2 = null;

        try {
            Statement var3 = var1.createStatement();
            var3.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS `%s` (`id` INT NOT NULL AUTO_INCREMENT, `uuid` VARCHAR(36) NOT NULL UNIQUE, `name` VARCHAR(16), `worldstats` VARCHAR(255), PRIMARY KEY (id));", this.TABLE_DATA));
            this.addColumn( "uuid", "VARCHAR(36) NOT NULL UNIQUE", "id");
            this.addColumn( "name", "VARCHAR(16) DEFAULT NULL", "uuid");
            this.addColumn( "worldstats", "VARCHAR(255)", "name");
            var3.close();
            DatabaseMetaData var5 = var1.getMetaData();
            ResultSet var4 = var5.getIndexInfo(null, null, this.TABLE_DATA, true, false);
            boolean var6 = false;

            while(var4.next()) {
                String var7 = var4.getString("COLUMN_NAME");
                String var8 = var4.getString("INDEX_NAME");
                if (var8 != null && var8.startsWith("name_")) {
                    var3 = var1.createStatement();
                    var3.executeUpdate(String.format("DROP INDEX %s ON %s", var8, this.TABLE_DATA));
                    var3.close();
                }

                if (var7 != null && var8 != null && var7.equalsIgnoreCase("name_") && var8.equalsIgnoreCase("name_")) {
                    var6 = true;
                }
            }

            var4.close();
            if (!var6) {
                var3 = var1.createStatement();
                var3.executeUpdate(String.format("ALTER TABLE %s ADD UNIQUE (name);", this.TABLE_DATA));
                var3.close();
            }
        } catch (Throwable var16) {
            var2 = var16;
            throw var16;
        } finally {
            if (var1 != null) {
                if (var2 != null) {
                    try {
                        var1.close();
                    } catch (Throwable var15) {
                        var2.addSuppressed(var15);
                    }
                } else {
                    var1.close();
                }
            }

        }

        plugin.debug("MySQL setup finished");
    }

    private synchronized void setConnectionArguments() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("database.mysql");
        String host;
        int port;
        if (section.getString("host").contains(":")){
            String [] hostA = section.getString("host").split(":");
            host = hostA[0];
            port = Integer.parseInt(hostA[1]);
        } else {
            host = section.getString("host");
            port = 3306;
        }
        String database = section.getString("database");
        String username = section.getString("user");
        String password = section.getString("password");
        this.ds = new HikariDataSource();
        this.ds.setPoolName("SPAuth MySQL");
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);

        this.ds.addDataSourceProperty("cachePrepStmts", "true");
        this.ds.addDataSourceProperty("prepStmtCacheSize", "250");
        this.ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.ds.addDataSourceProperty("characterEncoding", "utf8");
        this.ds.addDataSourceProperty("encoding", "UTF-8");
        this.ds.addDataSourceProperty("useUnicode", "true");
        this.ds.addDataSourceProperty("useSSL", "false");
        this.ds.setUsername(username);
        this.ds.setPassword(password);
        this.ds.setMaxLifetime(180000L);
        this.ds.setIdleTimeout(60000L);
        this.ds.setMinimumIdle(1);
        this.ds.setMaximumPoolSize(64);
    }

    private void addColumn(String var2, String var3, String var4) {
        ResultSet var5 = null;
        Statement var6 = null;

        try {
            Connection var7 = ds.getConnection();
            Throwable var8 = null;

            try {
                var6 = var7.createStatement();
                DatabaseMetaData var9 = var7.getMetaData();
                var5 = var9.getColumns(null, null, TABLE_DATA, var2);
                if (!var5.next()) {
                    var6.executeUpdate(String.format("ALTER TABLE %s ADD COLUMN %s %s AFTER %s;", TABLE_DATA, var2, var3, var4));
                }
            } catch (Throwable var26) {
                var8 = var26;
                throw var26;
            } finally {
                if (var7 != null) {
                    if (var8 != null) {
                        try {
                            var7.close();
                        } catch (Throwable var25) {
                            var8.addSuppressed(var25);
                        }
                    } else {
                        var7.close();
                    }
                }

            }
        } catch (SQLException var28) {
            var28.printStackTrace();
        } finally {
            this.close(var5);
            this.close(var6);
        }

    }


    @Override
    public void disconnect() {
        ds.close();
        plugin.console("{prefix}&7Disconnected from database.");
    }

    public void close(AutoCloseable var1) {
        if (var1 != null) {
            try {
                var1.close();
            } catch (Exception ignored) {}
        }
    }

    @Override
    public UUID getUUIDFromName(String name) {
        PreparedStatement var1 = null;
        ResultSet var2 = null;

        try {
            var1 = ds.getConnection().prepareStatement(String.format("SELECT * FROM %s WHERE name = ?;", TABLE_DATA));
            var1.setString(1, name);
            var2 = var1.executeQuery();
            if (var2.next()) {
                return UUID.fromString(var2.getString("uuid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(var2);
            this.close(var1);
        }
        return null;
    }

    @Override
    public boolean getOnlineMode(UUID uuid) {
        PreparedStatement var1 = null;
        ResultSet var2 = null;

        try {
            var1 = ds.getConnection().prepareStatement(String.format("SELECT * FROM %s WHERE uuid = ?;", TABLE_DATA));
            var1.setString(1, uuid.toString());
            var2 = var1.executeQuery();
            if (var2.next()) {
                return var2.getBoolean("onlineMode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(var2);
            this.close(var1);
        }
        return false;
    }

    @Override
    public String getPassword(UUID uuid) {
        PreparedStatement var1 = null;
        ResultSet var2 = null;

        try {
            var1 = ds.getConnection().prepareStatement(String.format("SELECT * FROM %s WHERE uuid = ?;", TABLE_DATA));
            var1.setString(1, uuid.toString());
            var2 = var1.executeQuery();
            if (var2.next()) {
                return var2.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(var2);
            this.close(var1);
        }
        return "";
    }

    @Override
    public void resetPassword(UUID uuid) {
        PreparedStatement var1 = null;

        try {
            var1 = ds.getConnection().prepareStatement(String.format("UPDATE %s SET password = ? WHERE uuid = ?;", TABLE_DATA));
            var1.setString(1, "");
            var1.setString(2, uuid.toString());
            var1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(var1);
        }
    }

    @Override
    public void setPassword(UUID uuid, String password) {
        PreparedStatement var1 = null;

        try {
            var1 = ds.getConnection().prepareStatement(String.format("UPDATE %s SET password = ? WHERE uuid = ?;", TABLE_DATA));
            var1.setString(1, password);
            var1.setString(2, uuid.toString());
            var1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(var1);
        }
    }

    @Override
    public void setOnlineMode(UUID uuid, boolean onlineMode) {
        PreparedStatement var1 = null;

        try {
            var1 = ds.getConnection().prepareStatement(String.format("UPDATE %s SET onlineMode = ? WHERE uuid = ?;", TABLE_DATA));
            var1.setBoolean(1, onlineMode);
            var1.setString(2, uuid.toString());
            var1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(var1);
        }
    }

    @Override
    public void loadUser(DataPlayer user) {
        PreparedStatement var1 = null;
        ResultSet var2 = null;

        try {
            var1 = ds.getConnection().prepareStatement(String.format("SELECT * FROM %s WHERE uuid = ?;", TABLE_DATA));
            var1.setString(1, user.getUuid().toString());
            var2 = var1.executeQuery();
            if (var2.next()) {
                user.deserialize(var2.getString("worldstats"));
                plugin.debug("Loaded user " + user.getName() + " from database.");
            } else {
                var1.close();
                var2.close();
                var1 = ds.getConnection().prepareStatement(String.format("INSERT INTO %s (uuid, name, worldstats) VALUES (?, ?, ?);", TABLE_DATA));
                var1.setString(1, user.getUuid().toString());
                var1.setString(2, user.getName());
                var1.setString(3, user.serialize());
                var1.executeUpdate();
                plugin.debug("User " + user.getUuid() + " not found in database. New entry will created save.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(var2);
            this.close(var1);
        }
    }

    @Override
    public void saveUser(DataPlayer user) {
        PreparedStatement var1 = null;

        try {
            var1 = ds.getConnection().prepareStatement(String.format("UPDATE %s SET name = ?, worldstats = ? WHERE uuid = ?;", TABLE_DATA));
            var1.setString(1, user.getName());
            var1.setString(2, user.serialize());
            var1.setString(3, user.getUuid().toString());
            var1.executeUpdate();
            plugin.debug("Saved user " + user.getName() + " to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(var1);
        }
    }

    @Override
    public void deleteUser(UUID uuid) {
        PreparedStatement var1 = null;

        try {
            var1 = ds.getConnection().prepareStatement(String.format("DELETE FROM %s WHERE uuid = ?;", TABLE_DATA));
            var1.setString(1, uuid.toString());
            var1.executeUpdate();
            plugin.debug("Deleted user " + uuid + " from database.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(var1);
        }
    }

    @Override
    public String getLastIp(UUID uuid) {
        PreparedStatement var1 = null;
        ResultSet var2 = null;

        try {
            var1 = ds.getConnection().prepareStatement(String.format("SELECT lastIp FROM %s WHERE uuid = ?;", TABLE_DATA));
            var1.setString(1, uuid.toString());
            var2 = var1.executeQuery();
            if (var2.next()) {
                return var2.getString("lastIp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(var2);
            this.close(var1);
        }
        return "";
    }
}
