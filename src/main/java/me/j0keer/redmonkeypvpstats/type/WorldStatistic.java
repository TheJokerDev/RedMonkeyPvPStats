package me.j0keer.redmonkeypvpstats.type;

import lombok.Getter;
import me.j0keer.redmonkeypvpstats.Main;

@Getter
public class WorldStatistic {
    private final Main plugin;

    private int kills = 0;
    private int deaths = 0;
    private int killstreak = 0;
    private int highestKillstreak = 0;

    public WorldStatistic(Main plugin){
        this.plugin = plugin;
    }

    public WorldStatistic(Main plugin, String statistics){
        this.plugin = plugin;
        String[] stats = statistics.split(",");
        kills = Integer.parseInt(stats[0]);
        deaths = Integer.parseInt(stats[1]);
        killstreak = Integer.parseInt(stats[2]);
        highestKillstreak = Integer.parseInt(stats[3]);
    }

    public int addKill(){
        kills++;
        killstreak++;
        if (killstreak > highestKillstreak){
            highestKillstreak = killstreak;
        }
        return kills;
    }

    public int addDeath(){
        deaths++;
        killstreak = 0;
        return deaths;
    }

    public void reset(){
        kills = 0;
        deaths = 0;
        killstreak = 0;
        highestKillstreak = 0;
    }

    public void resetKills(){
        kills = 0;
    }

    public void resetDeaths(){
        deaths = 0;
    }

    public void resetKillstreak(){
        killstreak = 0;
    }

    public double getKDR(){
        if (deaths == 0){
            return kills;
        }
        return (double) kills / deaths;
    }

    public double getKSR(){
        if (deaths == 0){
            return killstreak;
        }
        return (double) killstreak / deaths;
    }

    public double getHKSR(){
        if (deaths == 0){
            return highestKillstreak;
        }
        return (double) highestKillstreak / deaths;
    }

    public String getKDRString(){
        return String.format("%.2f", getKDR());
    }

    public String getKSRString(){
        return String.format("%.2f", getKSR());
    }

    public String getHKSRString(){
        return String.format("%.2f", getHKSR());
    }

    public String serialize(){
        return kills + "," + deaths + "," + killstreak + "," + highestKillstreak;
    }
}
