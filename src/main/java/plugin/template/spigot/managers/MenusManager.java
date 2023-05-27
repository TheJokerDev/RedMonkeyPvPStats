package plugin.template.spigot.managers;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import plugin.template.spigot.Main;
import plugin.template.spigot.type.CustomMenu;
import plugin.template.spigot.type.Menu;

import java.io.File;
import java.util.HashMap;

@Getter
public class MenusManager implements Listener {

    private Main plugin;
    public HashMap<String, HashMap<String, Menu>> menus = new HashMap<>();

    public File folder;

    public MenusManager(Main plugin) {
        this.plugin = plugin;
        folder = new File(plugin.getDataFolder()+"/menus");
        if (!folder.exists()){
            folder.mkdir();
            plugin.getConfigUtil().getMenu("customs/custom1");
        }
        plugin.listener(this);
    }

    public void loadMenus(Player p){
        plugin.debug("Loading menus for "+p.getName());
        File customs = new File(folder+"/customs");
        if (customs.exists()){
            if (customs.listFiles().length != 0){
                File[] files = customs.listFiles();
                for (File f : files){
                    if (!f.getName().endsWith(".yml")){
                        continue;
                    }
                    String name = f.getName().replace(".yml", "");
                    new CustomMenu(plugin, p, name);
                    plugin.debug("Loaded menu: "+name+ "for "+p.getName());
                }
            } else {
                plugin.debug("Customs menu folder is empty!");
            }
        } else {
            plugin.debug("Customs menu folder doesn't exist!");
        }

    }

    public HashMap<String, Menu> getPlayerMenus(Player var0) {
        return menus.containsKey(var0.getName()) ? menus.get(var0.getName()) : new HashMap<>();
    }

    public void set(String player, String id, Menu menu){
        HashMap<String, Menu> var1 = menus.get(player);
        if (var1 == null){
            var1 = new HashMap<>();
        }
        var1.put(id, menu);
        menus.put(player, var1);
    }

    public Menu getPlayerMenu(Player var0, String var1) {
        return getPlayerMenus(var0).getOrDefault(var1, null);
    }

    @EventHandler
    public void onPlayerLeaveInvRemove(PlayerQuitEvent var1) {
        menus.remove(var1.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerKickInvRemove(PlayerKickEvent var1) {
        menus.remove(var1.getPlayer().getName());
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent var1) {

        for (Menu var3 : getPlayerMenus((Player) var1.getPlayer()).values()) {
            if (var1.getView().getTitle().equals(var3.getTitle())) {
                var3.onOpen(var1);
            }
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent var1) {

        for (Menu var3 : getPlayerMenus((Player) var1.getPlayer()).values()) {
            if (var1.getView().getTitle().equals(var3.getTitle())) {
                var3.onClose(var1);
            }
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent var1) {

        for (Menu var3 : getPlayerMenus((Player) var1.getWhoClicked()).values()) {
            if (var1.getView().getTitle().equals(var3.getTitle()) && var1.getCurrentItem() != null) {
                var1.setCancelled(true);
                var3.onClick(var1);
            }
        }

    }
}
