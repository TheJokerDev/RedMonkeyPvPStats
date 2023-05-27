package plugin.template.spigot.type;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import plugin.template.spigot.Main;
import plugin.template.spigot.managers.ItemsManager;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu {
    public Main plugin;
    private String menuId;
    private Inventory inv;
    private Player player;
    private String title;
    private String back;
    public List<Button> buttons;

    private boolean custom = false;

    public Menu(Main plugin, Player var1, String var2, String var3, int var4) {
        this.plugin = plugin;
        this.player = var1;
        this.menuId = var2;
        this.title = plugin.getUtils().ct(var3);
        this.inv = Bukkit.createInventory(null, var4 * 9,title);
        this.setBack("none");
        buttons = new ArrayList<>();
        plugin.getMenusManager().set(var1.getName(), var2, this);
    }

    public boolean compareItem(ItemStack a, ItemStack b){
        return plugin.getUtils().compareItems(a, b);
    }

    public Menu(Main plugin, Player var1, String var2, boolean custom) {
        this.plugin = plugin;
        this.custom = custom;
        this.player = var1;
        this.menuId = var2;
        this.title = plugin.getUtils().ct(getConfig().getString("settings.title"));
        this.inv = Bukkit.createInventory(null, getConfig().getInt("settings.rows") * 9,title);
        this.setBack("none");
        buttons = new ArrayList<>();
        plugin.getMenusManager().set(var1.getName(), var2, this);
    }

    public FileConfiguration getConfig(){
        String menu = custom ? "customs/"+getMenuId() : ""+getMenuId();
        return plugin.getConfigUtil().getMenu(menu);
    }

    public FileConfiguration getConfig2(){
        String menu = "customs/"+getMenuId();
        return plugin.getConfigUtil().getMenu(menu);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = plugin.getUtils().ct(title);
    }

    public Menu(Main plugin, Player var1, String var2, String var3, int var4, String var5) {
        this.plugin = plugin;
        this.player = var1;
        this.menuId = var2;
        title = plugin.getUtils().ct(var3);
        this.inv = Bukkit.createInventory(null, var4 * 9, title);
        this.setBack(var5);
        buttons = new ArrayList<>();
        plugin.getMenusManager().set(var1.getName(), var2, this);
    }

    public Menu addItem(ItemStack var1) {
        this.inv.addItem(var1);
        return this;
    }

    public Menu addItem(SimpleItem var1) {
        return this.addItem(var1.build(getPlayer()));
    }

    public Menu setItem(int var1, SimpleItem var2) {
        this.inv.setItem(var1, var2.build(getPlayer()));
        return this;
    }
    public Menu setItem(List<Integer> var1, SimpleItem var2) {
        for (int i : var1){
            this.inv.setItem(i, ItemsManager.setPlaceHolders(var2.build(getPlayer()), getPlayer()));
        }
        return this;
    }

    public String getPermission(){
        return getConfig().getString("settings.permission", "none");
    }
    public Menu setItem(Button b){
        this.setItem(b.getSlot(), b.getItem());
        return this;
    }
    public Menu setItem(int var1, ItemStack var2) {
        this.inv.setItem(var1, var2);
        return this;
    }

    public Menu setItem(int var1, int var2, SimpleItem var3) {
        this.inv.setItem((var1 - 1) * 9 + (var2 - 1), var3.build(getPlayer()));
        return this;
    }

    public Menu setItem(int var1, int var2, ItemStack var3) {
        this.inv.setItem(var1 * 9 + var2, var3);
        return this;
    }

    public Inventory getInventory() {
        return this.inv;
    }

    public void newInventoryName(String var1) {
        this.inv = Bukkit.createInventory(null, this.inv.getSize(), var1);
    }

    public String getMenuId() {
        return this.menuId;
    }

    public Player getPlayer(){
        return player.getPlayer();
    }

    public String getBack() {
        return this.back;
    }

    public void setBack(String var1) {
        this.back = var1;
    }

    public void addFullLine(int var1, SimpleItem var2) {
        var2.setDisplayName(" &r");

        for(int var3 = 1; var3 < 10; ++var3) {
            this.setItem(var1, var3, var2);
        }

    }

    public abstract void onOpen(InventoryOpenEvent var1);

    public abstract void onClose(InventoryCloseEvent var1);

    public abstract void onClick(InventoryClickEvent var1);

    public abstract void update();

    public abstract void updateLang();
}