package plugin.template.spigot.type;

import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import plugin.template.spigot.Main;
import plugin.template.spigot.enums.SenderTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

@Getter
public abstract class CMD implements CommandExecutor, TabCompleter {
    private Main plugin;
    private List<SubCMD> subCMDS;

    public CMD(Main plugin){
        this.plugin = plugin;
        subCMDS = new ArrayList<>();
    }

    public abstract String getName();
    public abstract String getDescription();
    public abstract String getPermission();
    public abstract String getPermissionError();
    public List<String> getAliases(){
        return new ArrayList<>();
    }
    public abstract boolean isTabComplete();

    public void sendMSG(CommandSender sender, String msg){
        getPlugin().getUtils().sendMSG(sender, msg);
    }

    public void addSubCMD(SubCMD sub){
        if (!subCMDS.contains(sub)){
            subCMDS.add(sub);
        }
    }

    public boolean executeCMD(CommandSender sender, String alias, String[] args){
        String cmd = args[0].toLowerCase();
        Vector<String> vector = new Vector<>(Arrays.asList(args));
        vector.remove(0);
        args = vector.toArray(new String[0]);

        for (SubCMD sub : getSubCMDS()){
            if (!sub.getName().equalsIgnoreCase(cmd) && !sub.getAliases().contains(cmd.toLowerCase())){
                continue;
            }
            if (!sub.check(sender)){
                plugin.getUtils().sendMSG(sender, "general.noPermission");
                return true;
            }
            if (sub.getSenderType() != SenderTypes.BOTH){
                if (sub.getSenderType() == SenderTypes.CONSOLE && sender instanceof Player) {
                    plugin.getUtils().sendMSG(sender, "general.onlyConsole");
                    return true;
                } else if (sub.getSenderType() == SenderTypes.PLAYER && !(sender instanceof Player)){
                    plugin.getUtils().sendMSG(sender, "general.onlyPlayers");
                    return true;
                }
            }
            return sub.onCommand(sender, alias, args);
        }
        plugin.getUtils().sendMSG(sender, "general.commandNotExists");
        return true;
    }

    public List<String> execute(CommandSender sender, String alias, String[] args){
        if (args.length == 1){
            List<String> cmds = new ArrayList<>();
            if (getSubCMDS().size() > 0){
                for (SubCMD subCMD : getSubCMDS()) {
                    if (subCMD.getSenderType() == SenderTypes.CONSOLE && sender instanceof Player) {
                        continue;
                    } else if (subCMD.getSenderType() == SenderTypes.PLAYER && !(sender instanceof Player)){
                        continue;
                    }
                    cmds.add(subCMD.getName());
                    cmds.addAll(subCMD.getAliases());
                }
            }
            return StringUtil.copyPartialMatches(args[0], cmds, new ArrayList<>());
        }
        if (args.length >= 2){
            String cmd = args[0].toLowerCase();
            Vector<String> vector = new Vector<>(Arrays.asList(args));
            vector.remove(0);
            args = vector.toArray(new String[0]);
            for (SubCMD sub : getSubCMDS()){
                if (!sub.getName().equalsIgnoreCase(cmd) && !sub.getAliases().contains(cmd.toLowerCase())){
                    continue;
                }
                if (!sub.check(sender)){
                    return new ArrayList<>();
                }
                if (sub.getSenderType() != SenderTypes.BOTH){
                    if (sub.getSenderType() == SenderTypes.CONSOLE && sender instanceof Player) {
                        return new ArrayList<>();
                    } else if (sub.getSenderType() == SenderTypes.PLAYER && !(sender instanceof Player)){
                        return new ArrayList<>();
                    }
                }
                return sub.onTab(sender, alias, args);
            }
        }
        return new ArrayList<>();
    }
}

