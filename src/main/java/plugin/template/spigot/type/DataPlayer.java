package plugin.template.spigot.type;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugin.template.spigot.Main;

import java.util.*;

@Getter
public class DataPlayer {
    private final Main plugin = Main.getPlugin();
    private Player player;
    private String name;
    private UUID uuid;
    @Setter private HashMap<String, Button> items = new HashMap<>();

    public DataPlayer(Player player){
        this.player = player;
        loadItems();
    }

    public DataPlayer(String name){
        this.name = name;
        player = plugin.getServer().getPlayer(name);
        if (player != null){
            uuid = player.getUniqueId();
        }
        loadItems();
    }

    public DataPlayer(UUID uuid){
        this.uuid = uuid;
        player = plugin.getServer().getPlayer(uuid);
        if (player != null){
            name = player.getName();
        }
        loadItems();
    }

    public void loadItems(){
        boolean replace = false;
        List<String> replacing = new ArrayList<>();
        if (items.size() > 0){
            for (ItemStack item : getPlayer().getInventory().getContents()){
                if (item == null) continue;
                for (Map.Entry<String, Button> b : items.entrySet()){
                    if (item.isSimilar(b.getValue().getItem().build(getPlayer()))){
                        replace = true;
                        replacing.add(b.getKey());
                    }
                }
            }
        }
        items.clear();
        if (plugin.getConfigUtil().getItems().getKeys(false).size() > 0){
            for (String key : plugin.getConfigUtil().getItems().getKeys(false)){
                plugin.console("Item "+key+" loading for "+getPlayer().getName()+".");
                Button b = new Button(getPlayer(), plugin.getConfigUtil().getItems(), key);
                items.put(key, b);
                if (replace && replacing.contains(key)){
                    b.getSlot().forEach(integer -> getPlayer().getInventory().setItem(integer, b.getItem().build(getPlayer())));
                }
            }
        }

    }
}
