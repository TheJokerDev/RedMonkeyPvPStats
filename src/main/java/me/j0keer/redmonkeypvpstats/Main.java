package me.j0keer.redmonkeypvpstats;

import lombok.Getter;
import me.j0keer.redmonkeypvpstats.database.DatabaseManager;
import me.j0keer.redmonkeypvpstats.hooks.PAPI;
import me.j0keer.redmonkeypvpstats.listeners.GeneralListeners;
import me.j0keer.redmonkeypvpstats.type.DataPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import me.j0keer.redmonkeypvpstats.managers.CMDManager;
import me.j0keer.redmonkeypvpstats.managers.DataManager;
import me.j0keer.redmonkeypvpstats.utils.Utils;

import java.util.Arrays;

@Getter
public final class Main extends JavaPlugin {
    private static Main plugin;
    private PluginManager pluginManager;

    //Class declaration
    private Utils utils;
    private CMDManager cmdManager;
    private DataManager dataManager;
    private DatabaseManager databaseManager;

    private PAPI papi;

    @Override
    public void onEnable() {
        double ms = System.currentTimeMillis();
        plugin = this;
        saveDefaultConfig();
        pluginManager = getServer().getPluginManager();
        utils = new Utils(this);
        console("{prefix}&fInitializing plugin...", "");

        console(" &e» &fChecking dependencies...");
        if (!checkDependencies("PlaceholderAPI")){
            console("{prefix}&cPlease, check the message above and install all dependencies to work.");
            getPluginManager().disablePlugin(this);
            return;
        } else {
            papi = new PAPI(this);
            papi.register();
        }
        console(" &e» &aDependencies correctly detected.", "");

        console(" &e» &fRegistering managers");
        cmdManager = new CMDManager(this);
        dataManager = new DataManager(this);
        String databaseType = getConfig().getString("database.type", "yaml");
        databaseManager = new DatabaseManager(this, databaseType);
        console(" &e» &aManagers correctly loaded.", "");

        listener(new GeneralListeners(this));

        ms = System.currentTimeMillis()-ms;
        console("{prefix}&fPlugin loaded successfully in &e"+ms+"&fms.");

        Bukkit.getOnlinePlayers().forEach(player -> getDataManager().getDataPlayer(player));
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (papi != null) {
            papi.unregister();
            papi.register();
        }
        plugin.getUtils().reloadMessages();
    }

    private boolean checkDependencies(String... dependencies){
        boolean bol = true;
        for (String pl : dependencies) {
            if (getPluginManager().isPluginEnabled(pl)){
                console("   &b→ &fDependency detected: &a"+pl+"&f.");
            } else {
                console("   &b→ &fDependency not detected: &a"+pl+"&f. Please, install to init.");
                bol = false;
            }
        }
        return bol;
    }

    public void listener(Listener... listeners){
        Arrays.stream(listeners).forEach(listener -> {
            getPluginManager().registerEvents(listener, this);
            console("   &b→ &fListener class registered: &a"+listener.getClass().getSimpleName()+"&f.");
        });
    }

    public void console(String... out){
        Arrays.stream(out).forEach(s->utils.sendMSG(getServer().getConsoleSender(), s));
    }

    public void debug(String msg){
        if (!getConfig().getBoolean("settings.debug")){
            return;
        }
        utils.sendMSG(getServer().getConsoleSender(), "{prefix}&e&lDEBUG: &7"+msg);
    }

    public static Main getPlugin() {
        return plugin;
    }

    @Override
    public void onDisable() {
        double ms = System.currentTimeMillis();

        if (dataManager != null){
            dataManager.getPlayers().values().forEach(DataPlayer::forceUpload);
        }

        if (papi != null) {
            papi.unregister();
        }

        ms = System.currentTimeMillis()-ms;
        console("{prefix}&fPlugin disabled successfully in &e"+ms+"&fms.");
    }
}
