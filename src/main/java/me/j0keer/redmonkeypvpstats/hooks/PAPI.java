package me.j0keer.redmonkeypvpstats.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.j0keer.redmonkeypvpstats.Main;
import me.j0keer.redmonkeypvpstats.type.DataPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPI extends PlaceholderExpansion {
    private final Main plugin;

    public PAPI(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "redmonkey";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return params;
        }

        DataPlayer dataPlayer = plugin.getDataManager().getDataPlayer(player);

        String[] args = params.split("_");
        if (args.length > 1) {
            String type = args[0];
            boolean formatted = false;

            if (type.contains("formatted")){
                formatted = true;
                type = type.replace("formatted", "");
            }

            String noData = "0";
            switch (type.toLowerCase()){
                case "kills" -> {
                    String argument = args[1];
                    if (argument.equals("total") || argument.equals("all") || argument.equals("global")){
                        return String.valueOf(dataPlayer.getKills());
                    } else {
                        if (dataPlayer.getStatisticHashMap().containsKey(argument)){
                            return String.valueOf(dataPlayer.getKills(argument));
                        } else {
                            return noData;
                        }
                    }
                }
                case "deaths" -> {
                    String argument = args[1];
                    if (argument.equals("total") || argument.equals("all") || argument.equals("global")){
                        return String.valueOf(dataPlayer.getDeaths());
                    } else {
                        if (dataPlayer.getStatisticHashMap().containsKey(argument)){
                            return String.valueOf(dataPlayer.getDeaths(argument));
                        } else {
                            return noData;
                        }
                    }
                }
                case "killstreak" -> {
                    String argument = args[1];
                    if (argument.equals("total") || argument.equals("all") || argument.equals("global")){
                        return String.valueOf(dataPlayer.getStreak());
                    } else {
                        if (dataPlayer.getStatisticHashMap().containsKey(argument)){
                            return String.valueOf(dataPlayer.getStreak(argument));
                        } else {
                            return noData;
                        }
                    }
                }
                case "bestkillstreak" -> {
                    String argument = args[1];
                    if (argument.equals("total") || argument.equals("all") || argument.equals("global")){
                        return String.valueOf(dataPlayer.getBestStreak());
                    } else {
                        if (dataPlayer.getStatisticHashMap().containsKey(argument)){
                            return String.valueOf(dataPlayer.getBestStreak(argument));
                        } else {
                            return noData;
                        }
                    }
                }
                case "kdr" -> {
                    String argument = args[1];
                    if (argument.equals("total") || argument.equals("all") || argument.equals("global")){
                        return formatted ? String.format("%.2f", dataPlayer.getKDR()) : String.valueOf(dataPlayer.getKDR());
                    } else {
                        if (dataPlayer.getStatisticHashMap().containsKey(argument)){
                            return formatted ? String.format("%.2f", dataPlayer.getKDR(argument)) : String.valueOf(dataPlayer.getKDR(argument));
                        } else {
                            return noData;
                        }
                    }
                }
            }
        }
        return params;
    }
}
