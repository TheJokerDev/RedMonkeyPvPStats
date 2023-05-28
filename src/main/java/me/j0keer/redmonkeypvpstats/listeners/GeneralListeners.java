package me.j0keer.redmonkeypvpstats.listeners;

import me.j0keer.redmonkeypvpstats.Main;
import me.j0keer.redmonkeypvpstats.type.DataPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GeneralListeners implements Listener {
    private final Main plugin;

    public GeneralListeners(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        DataPlayer dp = plugin.getDataManager().getDataPlayer(p);

        if (dp == null) return;

        Player killer = null;
        if (e.getEntity().getKiller() != null){
            killer = e.getEntity().getKiller();
        }

        int deaths = dp.getWorldStatistic(p.getWorld().getName()).addDeath();
        if (killer != null){
            DataPlayer dpKiller = plugin.getDataManager().getDataPlayer(killer);
            if (dpKiller != null){
                int kills = dpKiller.getWorldStatistic(killer.getWorld().getName()).addKill();
                executeRewards(p.getWorld().getName(), "kill", kills, p, killer);
            }
        }

        executeRewards(p.getWorld().getName(), "death", deaths, p, killer);
    }

    public void executeRewards(String world, String type, int amount, Player p, @Nullable Player killer){
        List<String> commands = new ArrayList<>();

        if (plugin.getConfig().get("rewards") == null) return;

        for (String key : plugin.getConfig().getConfigurationSection("rewards").getKeys(false)){
            String str = "rewards."+key;
            List<String> worlds = new ArrayList<>(plugin.getConfig().getStringList(str+".worlds"));
            if (!worlds.isEmpty() && !worlds.contains(world)) continue;

            List<String> types = new ArrayList<>(plugin.getConfig().getConfigurationSection(str+".execute").getKeys(false));
            if (!types.isEmpty() && !types.contains(type)) continue;

            List<String> toExecute = new ArrayList<>(plugin.getConfig().getConfigurationSection(str+".execute."+type).getKeys(false));
            if (!toExecute.isEmpty()){
                if (toExecute.contains("*")){
                    commands.addAll(plugin.getConfig().getStringList(str+".execute."+type+".*"));
                }
                commands.addAll(new ArrayList<>(plugin.getConfig().getStringList(str+".execute."+type+"."+amount)));
            }
        }

        for (String command : commands) {
            if (killer == null){
                command = command.replace("{player}", p.getName());
                command = command.replace("{victim}", p.getName());
            } else {
                command = command.replace("{victim}", p.getName()).replace("{killer}", killer.getName());
                command = command.replace("{player}", p.getName());
            }
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
        }
    }
}
