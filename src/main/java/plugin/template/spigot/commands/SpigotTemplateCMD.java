package plugin.template.spigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import plugin.template.spigot.Main;
import plugin.template.spigot.commands.sub.spigottemplate.GetItemSubCMD;
import plugin.template.spigot.commands.sub.spigottemplate.ReloadSubCMD;
import plugin.template.spigot.type.CMD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpigotTemplateCMD extends CMD {

    public SpigotTemplateCMD(Main plugin) {
        super(plugin);
        addSubCMD(new ReloadSubCMD(plugin));
        addSubCMD(new GetItemSubCMD(plugin));
    }

    @Override
    public String getName() {
        return "spigottemplate";
    }

    @Override
    public String getDescription() {
        return "Main command of plugin.";
    }

    @Override
    public String getPermission() {
        return "none";
    }

    @Override
    public String getPermissionError() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("template");
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
