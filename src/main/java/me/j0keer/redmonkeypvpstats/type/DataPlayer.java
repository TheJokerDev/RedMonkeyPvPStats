package me.j0keer.redmonkeypvpstats.type;

import lombok.Getter;
import lombok.Setter;
import me.j0keer.redmonkeypvpstats.Main;
import me.j0keer.redmonkeypvpstats.database.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class DataPlayer {
    private final Main plugin = Main.getPlugin();
    private final Player player;
    private String name;
    private UUID uuid;
    @Setter private HashMap<String, WorldStatistic> statisticHashMap = new HashMap<>();

    public DataPlayer(Player player){
        this.player = player;
        name = player.getName();
        uuid = player.getUniqueId();
        loadData();
    }

    public void loadData(){
        new BukkitRunnable() {
            @Override
            public void run() {
                DatabaseManager.getDatabase().loadUser(DataPlayer.this);
            }
        }.runTaskAsynchronously(plugin);
    }

    public DataPlayer(String name){
        this.name = name;
        player = plugin.getServer().getPlayer(name);
        if (player != null){
            uuid = player.getUniqueId();
        }
        loadData();
    }

    public DataPlayer(UUID uuid){
        this.uuid = uuid;
        player = plugin.getServer().getPlayer(uuid);
        if (player != null){
            name = player.getName();
        }
        loadData();
    }

    public WorldStatistic getWorldStatistic(String world){
        return statisticHashMap.computeIfAbsent(world, k -> new WorldStatistic(plugin));
    }

    public int getKills(String world){
        return statisticHashMap.get(world).getKills();
    }

    public int getKills(){
        int kills = 0;
        for (WorldStatistic worldStatistic : statisticHashMap.values()){
            kills += worldStatistic.getKills();
        }
        return kills;
    }

    public int getDeaths(String world){
        return statisticHashMap.get(world).getDeaths();
    }

    public int getDeaths(){
        int deaths = 0;
        for (WorldStatistic worldStatistic : statisticHashMap.values()){
            deaths += worldStatistic.getDeaths();
        }
        return deaths;
    }

    public double getKDR(String world){
        return statisticHashMap.get(world).getKDR();
    }

    public double getKDR(){
        double kdr = 0;
        for (WorldStatistic worldStatistic : statisticHashMap.values()){
            kdr += worldStatistic.getKDR();
        }
        return kdr;
    }

    public int getStreak(String world){
        return statisticHashMap.get(world).getKillstreak();
    }

    public int getStreak(){
        int streak = 0;
        for (WorldStatistic worldStatistic : statisticHashMap.values()){
            streak += worldStatistic.getKillstreak();
        }
        return streak;
    }

    public int getBestStreak(String world){
        return statisticHashMap.get(world).getHighestKillstreak();
    }

    public int getBestStreak(){
        int bestStreak = 0;
        for (WorldStatistic worldStatistic : statisticHashMap.values()){
            bestStreak += worldStatistic.getHighestKillstreak();
        }
        return bestStreak;
    }

    public String serialize(){
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, WorldStatistic> entry : statisticHashMap.entrySet()){
            stringBuilder.append(entry.getKey()).append(":").append(entry.getValue().serialize()).append(";");
        }
        return stringBuilder.toString();
    }

    public void deserialize(String data){
        if (data == null || data.isEmpty()){
            return;
        }
        String[] worlds = data.split(";");
        plugin.debug("Worlds: " + Arrays.toString(worlds));
        for (String world : worlds){
            String[] worldData = world.split(":");
            statisticHashMap.put(worldData[0], new WorldStatistic(plugin, worldData[1]));
            plugin.debug("World: " + worldData[0] + " Data: " + worldData[1]);
        }
    }

    public void upload(){
        new BukkitRunnable() {
            @Override
            public void run() {
                DatabaseManager.getDatabase().saveUser(DataPlayer.this);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void forceUpload(){
        DatabaseManager.getDatabase().saveUser(this);
    }
}
