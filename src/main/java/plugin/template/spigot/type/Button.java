package plugin.template.spigot.type;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import plugin.template.spigot.Main;
import plugin.template.spigot.enums.ItemRequirements;
import plugin.template.spigot.managers.ItemsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Getter
public class Button {
    private final SimpleItem item;
    private final List<Integer> slot;
    private final Player player;
    private final ConfigurationSection section;
    private final FileConfiguration file;
    private int cooldown = 0;

    private boolean update = false;

    public Button(Player player, FileConfiguration file, String section){
        this.file = file;
        this.player = player;
        this.item = Main.getPlugin().getItemsManager().createItem(player.getPlayer(), file.getConfigurationSection(section), null);
        this.slot = getSlotFromString(file.getConfigurationSection(section).getString("slot"));
        this.section = file.getConfigurationSection(section);
        if (this.section.get("cooldown")!=null){
            this.cooldown = this.section.getInt("cooldown");
        }
        if (this.section.get("update")!=null){
            this.update = this.section.getBoolean("update");
        }
    }

    public boolean hasMetaData(){
        return getItem().getMetaData()!=null;
    }

    public String getMetaData(){
        return getItem().getMetaData();
    }

    public boolean hasRequirements(){
        return section.getConfigurationSection("requirements")!=null;
    }

    public boolean canInteract(){
        return section.getBoolean("interact", false);
    }

    public boolean canView(){
        if (!hasRequirements()){
            return true;
        }
        List<String> keys = new ArrayList<>(section.getConfigurationSection("requirements").getKeys(false));
        List<ConfigurationSection> sections = new ArrayList<>();
        if (!keys.contains("type")){
            for (String key : keys){
                sections.add(section.getConfigurationSection("requirements."+key));
            }
        } else {
            sections.add(section.getConfigurationSection("requirements"));
        }
        Player p = player.getPlayer();
        boolean bol = false;

        for (ConfigurationSection sec : sections){
            ItemRequirements type = ItemRequirements.valueOf(sec.getString("type").toUpperCase());
            String syntax = PlaceholderAPI.setPlaceholders(player.getPlayer(), sec.getString("syntax"));
            String value = sec.getString("value");

            switch (type){
                case INT_GREATER_THAN:{
                    bol = Integer.parseInt(syntax)>=Integer.parseInt(value);
                    break;
                }
                case INT_LESS_THAN:{
                    bol = Integer.parseInt(syntax)<=Integer.parseInt(value);
                    break;
                }
                case INT_EQUALS_TO:{
                    bol = Integer.parseInt(syntax)==Integer.parseInt(value);
                    break;
                }
                case INT_NOT_EQUALS_TO:{
                    bol = Integer.parseInt(syntax)!=Integer.parseInt(value);
                    break;
                }
                case STRING:{
                    bol = syntax.equalsIgnoreCase(value);
                    break;
                }
                case STRING_NOT:{
                    bol = !syntax.equalsIgnoreCase(value);
                    break;
                }
                case BOOLEAN:{
                    bol = Boolean.parseBoolean(syntax);
                    break;
                }
                case BOOLEAN_NOT:{
                    bol = !Boolean.parseBoolean(syntax);
                    break;
                }
                case PERMISSION:{
                    bol = p.hasPermission(syntax);
                    break;
                }
                case PERMISSION_NOT:{
                    bol = !p.hasPermission(syntax);
                    break;
                }
            }
            if (!bol){
                break;
            }
        }
        return bol;
    }

    private List<Integer> getSlotFromString(String var1){
        if (var1 == null){
            return new ArrayList<>(Collections.singletonList(0));
        }
        boolean isOne = !var1.contains(",") && !var1.contains("-");
        List<Integer> slots = new ArrayList<>();
        if (isOne){
            try {
                int i = Integer.parseInt(var1);
                slots.add(i);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            String[] var2 = new String[0];
            if (var1.contains(",")){
                var2 = var1.split(",");
            } else if (var1.contains("-")){
                var2 = var1.split("-");
                for (int i = Integer.parseInt(var2[0]); i <= Integer.parseInt(var2[1]); i++){
                    slots.add(i);
                }
                return slots;
            }
            for (String s : var2){
                slots.addAll(getSlotFromString(s));
            }
        }
        return slots;
    }

    public Button(Player player, FileConfiguration file, String section, HashMap<String, String> pl){
        this.player = player;
        this.file = file;
        this.item = Main.getPlugin().getItemsManager().createItem(player.getPlayer(), file.getConfigurationSection(section), pl);
        this.slot = getSlotFromString(file.getConfigurationSection(section).getString("slot"));
        this.section = file.getConfigurationSection(section);
        if (this.section.get("cooldown")!=null){
            this.cooldown = this.section.getInt("cooldown");
        }
    }

    public SimpleItem getItem() {
        return ItemsManager.setPlaceHolders(item, player.getPlayer());
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean executePhysicallyItemsActions(PlayerInteractEvent e){
        e.setCancelled(true);
        boolean bol = true;
        if (section.get("actions")==null){
            return bol;
        }
        List<String> leftClick = section.getStringList("actions.leftclick");
        List<String> rightClick = section.getStringList("actions.rightclick");
        List<String> shiftClick = section.getStringList("actions.shiftclick");
        List<String> all = section.getStringList("actions.multiclick");

        if (!leftClick.isEmpty() && e.getAction().name().contains("LEFT")){
            bol = Main.getPlugin().getUtils().actions(getPlayer().getPlayer(), leftClick);
        }
        if (!rightClick.isEmpty() && e.getAction().name().contains("RIGHT")){
            bol = Main.getPlugin().getUtils().actions(getPlayer().getPlayer(), rightClick);
        }
        if (!shiftClick.isEmpty() && e.getPlayer().isSneaking()){
            bol = Main.getPlugin().getUtils().actions(getPlayer().getPlayer(), shiftClick);
        }
        if (!all.isEmpty()){
            bol = Main.getPlugin().getUtils().actions(getPlayer().getPlayer(), all);
        }
        return bol;
    }

    public boolean executeItemInMenuActions(InventoryClickEvent e){
        boolean bol = true;
        if (section.get("actions")==null){
            return bol;
        }
        List<String> leftClick = section.getStringList("actions.leftclick");
        List<String> rightClick = section.getStringList("actions.rightclick");
        List<String> middleClick = section.getStringList("actions.middleclick");
        List<String> shiftClick = section.getStringList("actions.shiftclick");
        List<String> all = section.getStringList("actions.multiclick");

        if (!leftClick.isEmpty() && e.getClick() == ClickType.LEFT){
            bol = Main.getPlugin().getUtils().actions(getPlayer().getPlayer(), leftClick);
        }
        if (!rightClick.isEmpty() && e.getClick() == ClickType.RIGHT){
            bol = Main.getPlugin().getUtils().actions(getPlayer().getPlayer(), rightClick);
        }
        if (!middleClick.isEmpty() && e.getClick() == ClickType.MIDDLE){
            bol = Main.getPlugin().getUtils().actions(getPlayer().getPlayer(), middleClick);
        }
        if (!shiftClick.isEmpty() && e.getClick().name().contains("SHIFT")){
            bol = Main.getPlugin().getUtils().actions(getPlayer().getPlayer(), shiftClick);
        }
        if (!all.isEmpty()){
            bol = Main.getPlugin().getUtils().actions(getPlayer().getPlayer(), all);
        }
        if (update){
            for (Menu var3 : Main.getPlugin().getMenusManager().getPlayerMenus((Player) e.getWhoClicked()).values()) {
                if (e.getView().getTitle().equals(var3.getTitle()) && e.getCurrentItem() != null) {
                    e.setCancelled(true);
                    var3.update();
                }
            }
        }
        return bol;
    }
}
