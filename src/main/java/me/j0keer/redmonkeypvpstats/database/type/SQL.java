package me.j0keer.redmonkeypvpstats.database.type;

import me.j0keer.redmonkeypvpstats.Main;
import me.j0keer.redmonkeypvpstats.database.Database;
import me.j0keer.redmonkeypvpstats.type.DataPlayer;

import java.sql.*;
import java.util.UUID;

public class SQL extends Database {

    private Connection con;
    private final String TABLE_DATA = "player_data";

    public SQL(Main plugin) {
        super(plugin);
    }

    @Override
    protected String getType() {
        return "SQL";
    }

    @Override
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:"+plugin.getDataFolder()+"/database.db");
            plugin.console("{prefix}&7Connected to database.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Statement var1 = null;

        try {
            var1 = con.createStatement();
            var1.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS '%s' ('id' INTEGER PRIMARY KEY, 'uuid' VARCHAR(36), 'name' VARCHAR(16), worldstats VARCHAR(255)); CREATE INDEX IF NOT EXISTS playerData_UUID ON %s(uuid);", this.TABLE_DATA, this.TABLE_DATA));
            addColumn("uuid", "VARCHAR(36) NOT NULL UNIQUE");
            addColumn("name", "VARCHAR(16) DEFAULT NULL");
            addColumn("worldstats", "VARCHAR(255)");
            var1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(var1);
        }
    }

    private void addColumn(String var2, String var3) {
        ResultSet var4 = null;
        Statement var5 = null;

        try {
            var5 = con.createStatement();
            DatabaseMetaData var6 = con.getMetaData();
            var4 = var6.getColumns(null, null, TABLE_DATA, var2);
            if (!var4.next()) {
                var5.executeUpdate(String.format("ALTER TABLE %s ADD COLUMN %s %s;", "player_data", var2, var3));
            }
        } catch (SQLException var10) {
            var10.printStackTrace();
        } finally {
            this.close(var4);
            this.close(var5);
        }
    }


    @Override
    public void disconnect() {
        close(con);
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
            var1 = con.prepareStatement(String.format("SELECT * FROM %s WHERE name = ?;", TABLE_DATA));
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
            var1 = con.prepareStatement(String.format("SELECT * FROM %s WHERE uuid = ?;", TABLE_DATA));
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
            var1 = con.prepareStatement(String.format("SELECT * FROM %s WHERE uuid = ?;", TABLE_DATA));
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
            var1 = con.prepareStatement(String.format("UPDATE %s SET password = ? WHERE uuid = ?;", TABLE_DATA));
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
            var1 = con.prepareStatement(String.format("UPDATE %s SET password = ? WHERE uuid = ?;", TABLE_DATA));
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
            var1 = con.prepareStatement(String.format("UPDATE %s SET onlineMode = ? WHERE uuid = ?;", TABLE_DATA));
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
            var1 = con.prepareStatement(String.format("SELECT * FROM %s WHERE uuid = ?;", TABLE_DATA));
            var1.setString(1, user.getUuid().toString());
            var2 = var1.executeQuery();
            if (var2.next()) {
                user.deserialize(var2.getString("worldstats"));
                plugin.debug("Loaded user " + user.getName() + " from database.");
            } else {
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
            var1 = con.prepareStatement(String.format("UPDATE %s SET name = ?, worldstats = ? WHERE uuid = ?;", TABLE_DATA));
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
            var1 = con.prepareStatement(String.format("DELETE FROM %s WHERE uuid = ?;", TABLE_DATA));
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
            var1 = con.prepareStatement(String.format("SELECT lastIp FROM %s WHERE uuid = ?;", TABLE_DATA));
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
