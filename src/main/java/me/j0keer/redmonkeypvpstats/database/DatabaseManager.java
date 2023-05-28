package me.j0keer.redmonkeypvpstats.database;

import lombok.Getter;
import me.j0keer.redmonkeypvpstats.Main;
import me.j0keer.redmonkeypvpstats.database.type.MongoDB;
import me.j0keer.redmonkeypvpstats.database.type.MySQL;
import me.j0keer.redmonkeypvpstats.database.type.SQL;
import me.j0keer.redmonkeypvpstats.database.type.Yaml;

@Getter
public class DatabaseManager {
    private static Database database;
    private static Main plugin;

    public static Database getDatabase() {
        return database;
    }

    public DatabaseManager(Main plugin, String databaseType) {
        DatabaseManager.plugin = plugin;
        databaseType = databaseType.toLowerCase();
        switch (databaseType) {
            case "sqlite" -> {
                database = new SQL(plugin);
            }
            case "mariadb", "mysql" -> {
                database = new MySQL(plugin);
            }
            case "mongodb" -> {
                database = new MongoDB(plugin);
            }
            default -> {
                plugin.console("{prefix}&cDatabase type not found, using default &7(YAML)");
                database = new Yaml(plugin);
            }
        }
        plugin.console("{prefix}&7Database type set to &e" + database.getType());
        plugin.console("{prefix}&7Connecting to database...");
        database.connect();
    }

    public static Main getPlugin() {
        return plugin;
    }

    public void disconnect() {
        database.disconnect();
    }
}
