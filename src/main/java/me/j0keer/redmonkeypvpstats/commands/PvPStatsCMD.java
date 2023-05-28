package me.j0keer.redmonkeypvpstats.commands;

import me.j0keer.redmonkeypvpstats.Main;
import me.j0keer.redmonkeypvpstats.commands.sub.spigottemplate.ClearStatsCMD;
import me.j0keer.redmonkeypvpstats.commands.sub.spigottemplate.ReloadSubCMD;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.j0keer.redmonkeypvpstats.type.CMD;

import java.util.ArrayList;
import java.util.List;

public class PvPStatsCMD extends CMD {

    public PvPStatsCMD(Main plugin) {
        super(plugin);
        addSubCMD(new ReloadSubCMD(plugin));
        addSubCMD(new ClearStatsCMD(plugin));
    }

    @Override
    public String getName() {
        return "pvpstats";
    }

    @Override
    public String getDescription() {
        return "Main command of plugin.";
    }

    @Override
    public String getPermission() {
        return "redmonkeypvpstats.admin";
    }

    @Override
    public String getPermissionError() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return List.of("stats");
    }

    @Override
    public boolean isTabComplete() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0){
            sendMSG(sender, "commands.main.needArguments");
            return true;
        }
        return executeCMD(sender, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0){
            return execute(sender, label, args);
        }
        return new ArrayList<>();
    }
}
