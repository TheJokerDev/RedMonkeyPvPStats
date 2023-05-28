package me.j0keer.redmonkeypvpstats.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.j0keer.redmonkeypvpstats.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    private final Main plugin;

    public Utils(Main plugin){
        this.plugin = plugin;
    }

    /*----| Related to Strings |----*/
    public String ct(String in){
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public String getPrefix(){
        return ct(plugin.getConfig().getString("settings.prefix"));
    }

    public String formatMSG(CommandSender sender, String in){
        String out = in;

        if (in.contains(".") && !in.contains(" ")){
            out = getKey(in);
        }

        if (out.contains("{prefix}")){
            out = out.replace("{prefix}", getPrefix());
        }

        out = PlaceholderAPI.setPlaceholders(sender != null ? sender instanceof Player ? (Player) sender: null : null, out);

        return ct(out);
    }

    public String getKey(String key){
        return getMessages().get(key)!=null ? getMessages().getString(key) : key;
    }

    private FileConfiguration messages;

    public FileConfiguration getMessages(){
        if (messages == null) {
            File file = new File(plugin.getDataFolder(), "messages.yml");
            if (!file.exists()){
                plugin.saveResource("messages.yml", false);
            }
            messages =  YamlConfiguration.loadConfiguration(file);
        }
        return messages;
    }

    public void reloadMessages(){
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()){
            plugin.saveResource("messages.yml", false);
        }
        if (messages != null){
            try {
                messages.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        messages =  YamlConfiguration.loadConfiguration(file);
    }

    /*----| Related to ArrayLists |----*/
    public List<String> ct(List<String> in){
        return in.stream().map(this::ct).collect(Collectors.toList());
    }

    /*----| Related to Void executors |----*/
    public void sendMSG(CommandSender sender, String msg){
        msg = formatMSG(sender, msg);

        if (msg.contains("\\n")){
            msg = msg.replace("\\n", "\n");
        }

        if (msg.contains("\n")){
            Arrays.stream(msg.split("\n")).forEach(s -> sendMSG(sender, s));
            return;
        }

        if (sender instanceof Player) {
            sender.sendMessage(msg);
        } else {
            plugin.getServer().getConsoleSender().sendMessage(msg);
        }
    }

}
