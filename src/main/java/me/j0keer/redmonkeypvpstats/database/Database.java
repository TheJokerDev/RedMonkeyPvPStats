package me.j0keer.redmonkeypvpstats.database;

import lombok.Getter;
import me.j0keer.redmonkeypvpstats.Main;
import me.j0keer.redmonkeypvpstats.type.DataPlayer;

import java.util.UUID;

public abstract class Database {
    @Getter public final Main plugin;

    public Database(Main plugin) {
        this.plugin = plugin;
    }

    protected abstract String getType();

    public abstract void connect();

    public abstract void disconnect();

    public abstract UUID getUUIDFromName(String name);

    public abstract boolean getOnlineMode(UUID uuid);
    public abstract String getPassword(UUID uuid);
    public abstract void resetPassword(UUID uuid);
    public abstract void setPassword(UUID uuid, String password);
    public abstract void setOnlineMode(UUID uuid, boolean onlineMode);

    public abstract void loadUser(DataPlayer user);
    public abstract void saveUser(DataPlayer user);

    public abstract void deleteUser(UUID uuid);


    public abstract String getLastIp(UUID uuid);
}
