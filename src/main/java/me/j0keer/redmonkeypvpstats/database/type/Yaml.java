package me.j0keer.redmonkeypvpstats.database.type;

import me.j0keer.redmonkeypvpstats.Main;
import me.j0keer.redmonkeypvpstats.database.Database;
import me.j0keer.redmonkeypvpstats.type.DataPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Yaml extends Database {
    private FileConfiguration config;

    public Yaml(Main plugin) {
        super(plugin);
    }

    @Override
    public String getType() {
        return "YAML";
    }

    @Override
    public void connect() {
        File file = new File(plugin.getDataFolder(), "database.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        plugin.debug("{prefix}&7Connected to database.");
    }

    @Override
    public void disconnect() {
        try {
            config.save(new File(plugin.getDataFolder(), "database.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        plugin.console("{prefix}&7Disconnected from database.");
    }

    @Override
    public UUID getUUIDFromName(String name) {
        UUID uuid = null;
        if (!config.getKeys(false).isEmpty()) {
            for (String key : config.getKeys(false)) {
                if (config.getString(key + ".name").equalsIgnoreCase(name)) {
                    uuid = UUID.fromString(key);
                    break;
                }
            }
        }
        return uuid;
    }

    public void save() {
        try {
            config.save(new File(plugin.getDataFolder(), "database.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean getOnlineMode(UUID uuid) {
        if (config.get(uuid.toString() + ".online-mode") == null) {
            config.set(uuid + ".online-mode", false);
            save();
        }
        return config.getBoolean(uuid + ".online-mode");
    }

    @Override
    public String getPassword(UUID uuid) {
        if (config.get(uuid.toString() + ".password") == null) {
            config.set(uuid + ".password", "");
            save();
        }
        return config.getString(uuid + ".password");
    }

    @Override
    public void resetPassword(UUID uuid) {
        config.set(uuid + ".password", "");
        save();
    }

    @Override
    public void setPassword(UUID uuid, String password) {
        config.set(uuid + ".password", password);
        save();
    }

    @Override
    public void setOnlineMode(UUID uuid, boolean onlineMode) {
        config.set(uuid + ".online-mode", onlineMode);
        save();
    }

    @Override
    public void loadUser(DataPlayer user) {
        if (config.get(user.getUuid().toString() + ".name") == null) {
            config.set(user.getUuid() + ".name", user.getName());
            config.set(user.getUuid() + ".worldstats", "");
            save();
        }
        user.deserialize(config.getString(user.getUuid() + ".worldstats",""));
    }

    @Override
    public void saveUser(DataPlayer user) {
        config.set(user.getUuid() + ".name", user.getName());
        config.set(user.getUuid() + ".worldstats", user.serialize());
        save();
    }

    @Override
    public void deleteUser(UUID uuid) {
        config.set(uuid.toString(), null);
        save();
    }

    @Override
    public String getLastIp(UUID uuid) {
        return null;
    }
}
