package plugin.template.spigot;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.template.spigot.config.ConfigUtil;
import plugin.template.spigot.listeners.InteractListeners;
import plugin.template.spigot.managers.CMDManager;
import plugin.template.spigot.managers.DataManager;
import plugin.template.spigot.managers.ItemsManager;
import plugin.template.spigot.managers.MenusManager;
import plugin.template.spigot.utils.Utils;

import java.util.Arrays;

@Getter
public final class Main extends JavaPlugin {
    private static Main plugin;
    private PluginManager pluginManager;

    //Class declaration
    private Utils utils;
    private CMDManager cmdManager;
    private ItemsManager itemsManager;
    private MenusManager menusManager;
    private DataManager dataManager;
    private ConfigUtil configUtil;

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
        }
        console(" &e» &aDependencies correctly detected.", "");

        configUtil = new ConfigUtil(this);

        console(" &e» &fRegistering managers");
        cmdManager = new CMDManager(this);
        itemsManager = new ItemsManager(this);
        menusManager = new MenusManager(this);
        dataManager = new DataManager(this);
        listener(new InteractListeners(this));
        console(" &e» &aManagers correctly loaded.", "");


        ms = System.currentTimeMillis()-ms;
        console("{prefix}&fPlugin loaded successfully in &e"+ms+"&fms.");

        Bukkit.getOnlinePlayers().forEach(player -> getDataManager().getDataPlayer(player));
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

        ms = System.currentTimeMillis()-ms;
        console("{prefix}&fPlugin disabled successfully in &e"+ms+"&fms.");
    }
}
