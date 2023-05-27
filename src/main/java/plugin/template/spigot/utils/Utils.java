package plugin.template.spigot.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugin.template.spigot.Main;
import plugin.template.spigot.type.Menu;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    private Main plugin;

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
        return plugin.getConfigUtil().getMessages().get(key)!=null ? plugin.getConfigUtil().getMessages().getString(key) : key;
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

    public boolean actions(Player p, List<String> list){
        for (String s : list){
            s = PlaceholderAPI.setPlaceholders(p, s);
            if (s.startsWith("[close]")){
                p.closeInventory();
                continue;
            }
            if (s.startsWith("[sound]")){
                playAudio(p, s.replace("[sound]", ""));
                continue;
            }
            if (s.startsWith("[cmd]")){
                s = s.replace("[cmd]", "");
                p.chat("/"+s);
            }
            if (s.startsWith("[cmd=OP]")){
                s = s.replace("[cmd=OP]", "");
                Bukkit.dispatchCommand(p, s);
            }
            if (s.startsWith("[cmd=Console]")){
                s = s.replace("[cmd=Console]", "");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
            }
            if (s.startsWith("[msg]")){
                s = s.replace("[msg]", "");
                sendMSG(p, s);
            }
            if (s.startsWith("[open]")){
                s = s.replace("[open]", "");
                Menu menu = plugin.getMenusManager().getPlayerMenu(p, s);
                if (menu == null){
                    sendMSG(p, "general.menuNotExists");
                    return true;
                }
                p.openInventory(menu.getInventory());
            }
            if (s.startsWith("[title]")){
                s = s.replace("[title]", "");
                String[] split = s.split("`");
                if (split.length <= 2){
                    Titles.sendTitle(p, formatMSG(p, split[0]), formatMSG(p, split[1]));
                }
                if (split.length == 5){
                    Titles.sendTitle(p, Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), formatMSG(p, split[0]), formatMSG(p, split[1]));
                }
            }

            if (s.startsWith("[actionbar]")){
                s = s.replace("[actionbar]", "");
                String[] split = s.split("`");
                if (split.length == 1){
                    ActionBar.sendActionBar(p, formatMSG(p, split[0]));
                }
                if (split.length == 2){
                    ActionBar.sendActionBar(plugin, p, formatMSG(p, split[0]), Integer.parseInt(split[1]));
                }
            }
        }
        return true;
    }

    public void playAudio(Player p, String id){
        XSound sound;
        String[] split = id.split(",");
        try {
            sound = XSound.valueOf(split[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            return;
        }
        float volume = Float.parseFloat(split[1]);
        float pitch = Float.parseFloat(split[2]);
        sound.play(p, volume, pitch);
    }

    /* ---- Related to Booleans ---- */
    public boolean compareItems(ItemStack item1, ItemStack item2) {
        boolean bool = false;
        if (item1 != null &&item2 != null && item1.getType() != XMaterial.AIR.parseMaterial()) {
            if (item1.getType() == item2.getType() && item1.getAmount() == item2.getAmount()) {
                if (item1.hasItemMeta() && item1.getItemMeta().hasDisplayName()) {
                    if (item1.getItemMeta().getDisplayName().equalsIgnoreCase(item2.getItemMeta().getDisplayName())) {
                        bool = true;
                    }
                }
            }
        }
        return bool;
    }
}
