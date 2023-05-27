package plugin.template.spigot.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import plugin.template.spigot.Main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigUtil {
    private Main plugin;

    public HashMap<String, FileConfiguration> configFiles;

    public ConfigUtil(Main plugin){
        this.plugin = plugin;
        configFiles = new HashMap<>();
        configFiles.put("config", plugin.getConfig());
        createConfig("items");
        createConfig("messages");
    }

    public FileConfiguration getConfig(){
        return configFiles.get("config");
    }

    public FileConfiguration getItems(){
        return configFiles.get("items");
    }
    public FileConfiguration getMessages(){
        return configFiles.get("messages");
    }

    public FileConfiguration getMenu(String menu){
        FileConfiguration config = configFiles.get("menus/"+menu);
        if (config == null){
            createConfig("menus/"+menu);
        }
        return configFiles.get("menus/"+menu);
    }

    public void createConfig(String name) {
        File configFile = new File(plugin.getDataFolder(), name+".yml");
        if (!configFile.exists()){
            try {
                plugin.saveResource(name+".yml", false);
            } catch (Exception e) {
                try {
                    configFile.createNewFile();
                } catch (IOException e2) {
                    return;
                }
            }
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (InvalidConfigurationException | IOException e) {
            plugin.console("{prefix}Failed loading config file: "+name+".");
            return;
        }
        configFiles.put(name, config);
    }

    public void saveAll(){
        for (Map.Entry<String, FileConfiguration> config : configFiles.entrySet()){
            try {
                config.getValue().save(new File(plugin.getDataFolder(), config.getKey()+".yml"));
            } catch (IOException e) {
                plugin.console("{prefix}Failed loading saving file: "+config.getKey()+".");
            }
        }
    }
    public void reloadAll(){
        List<String> configs = configFiles.keySet().stream().collect(Collectors.toList());
        configFiles.clear();
        for (String config : configs){
            if (config.equals("config")){
                plugin.reloadConfig();
                configFiles.put("config", plugin.getConfig());
                continue;
            }
            createConfig(config);
        }
    }

    public void save(String name){
        if (configFiles.containsKey(name)){
            FileConfiguration config = configFiles.get(name);
            try {
                config.save(new File(plugin.getDataFolder(), name+".yml"));
            } catch (IOException e) {
                plugin.console("{prefix}Failed loading saving file: "+config+".");
            }
        }
    }

    public void reload(String name){
        if (configFiles.containsKey(name)){
            configFiles.remove(name);
            createConfig(name);
        }
    }
}
