package me.j0keer.redmonkeypvpstats.commands.sub.spigottemplate;

import me.j0keer.redmonkeypvpstats.Main;
import me.j0keer.redmonkeypvpstats.enums.SenderTypes;
import me.j0keer.redmonkeypvpstats.type.DataPlayer;
import me.j0keer.redmonkeypvpstats.type.SubCMD;
import me.j0keer.redmonkeypvpstats.type.WorldStatistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClearStatsCMD extends SubCMD {
    public ClearStatsCMD(Main plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "clearstats";
    }

    @Override
    public String getPermission() {
        return "redmonkeypvpstats.clearstats";
    }

    @Override
    public SenderTypes getSenderType() {
        return SenderTypes.BOTH;
    }

    @Override
    public boolean onCommand(CommandSender sender, String alias, String[] args) {
        if (args.length < 2) {
            sendMSG(sender, "commands.clearstats.needArguments");
            return true;
        }
        String var1 = args[0];
        Player p = getPlugin().getServer().getPlayer(var1);
        if (p == null) {
            sendMSG(sender, "general.playerNotFound");
            return true;
        }
        DataPlayer dp = getPlugin().getDataManager().getDataPlayer(p);
        if (dp == null) {
            sendMSG(sender, "general.playerNotFound");
            return true;
        }
        String var2 = args[1];
        WorldStatistic ws = dp.getStatisticHashMap().get(var2);
        if (ws == null) {
            sendMSG(sender, "general.statisticNotFound");
            return true;
        }
        String var3 = args[2];
        if (var3.equalsIgnoreCase("kills")) {
            ws.resetKills();
            sendMSG(sender, "commands.main.clear-stats.success.kill");
            return true;
        }
        if (var3.equalsIgnoreCase("deaths")) {
            ws.resetDeaths();
            sendMSG(sender, "commands.main.clear-stats.success.death");
            return true;
        }
        if (var3.equalsIgnoreCase("killstreak")) {
            ws.resetKillstreak();
            sendMSG(sender, "commands.main.clear-stats.success.killstreak");
            return true;
        }
        if (var3.equalsIgnoreCase("all")) {
            ws.reset();
            sendMSG(sender, "commands.main.clear-stats.success.all");
            return true;
        }
        sendMSG(sender, "commands.main.clear-stats.needArguments");
        return true;
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        if (args.length > 0) {
            if (args.length == 1) {
                return StringUtil.copyPartialMatches(args[0], getPlugin().getServer().getOnlinePlayers().stream().map(Player::getName).toList(), new ArrayList<>());
            }
            String var1 = args[0];
            Player p = getPlugin().getServer().getPlayer(var1);
            if (args.length == 2) {
                if (p != null) {
                    DataPlayer dp = getPlugin().getDataManager().getDataPlayer(p);
                    if (dp != null) {
                        return StringUtil.copyPartialMatches(args[1], dp.getStatisticHashMap().keySet(), new ArrayList<>());
                    }
                }
            }
            if (args.length == 3) {
                String var2 = args[1];
                if (p != null) {
                    DataPlayer dp = getPlugin().getDataManager().getDataPlayer(p);
                    if (dp != null) {
                        WorldStatistic ws = dp.getStatisticHashMap().get(var2);
                        if (ws != null) {
                            return StringUtil.copyPartialMatches(args[2], Arrays.asList("kills", "deaths", "killstreak", "all"), new ArrayList<>());
                        }
                    }
                }
            }
        }
        return new ArrayList<>();
    }
}
