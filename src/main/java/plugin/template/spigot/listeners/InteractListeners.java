package plugin.template.spigot.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import plugin.template.spigot.Main;
import plugin.template.spigot.type.Button;
import plugin.template.spigot.type.DataPlayer;

public class InteractListeners implements Listener {
    private Main plugin;

    public InteractListeners(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        DataPlayer dp = plugin.getDataManager().getDataPlayer(p);
        ItemStack click = e.getItem();

        if (click == null){
            return;
        }
        if (click.getType() == Material.AIR){
            return;
        }
        if (dp.getItems().keySet().size()>0){
            for (Button b : dp.getItems().values()){
                if (b.getItem().build(p).isSimilar(click)){
                    b.executePhysicallyItemsActions(e);
                    break;
                }
            }
        }
    }

}
