package plugin.template.spigot.type;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import plugin.template.spigot.Main;
import plugin.template.spigot.enums.SenderTypes;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class SubCMD {
    private Main plugin;

    public SubCMD(Main plugin){
        this.plugin = plugin;
    }

    public abstract String getName();
    public abstract String getPermission();
    public List<String> getAliases(){
        return new ArrayList<>();
    }
    public abstract SenderTypes getSenderType();

    public abstract boolean onCommand(CommandSender sender, String alias, String[] args);
    public abstract List<String> onTab(CommandSender sender, String alias, String[] args);


    public boolean check(CommandSender sender){
        return check(sender, getPermission());
    }
    public static boolean check(CommandSender sender, String permission){
        if (permission == null || permission.equals("") || permission.equalsIgnoreCase("NONE")){
            return true;
        }
        return sender.hasPermission(permission);
    }

    public void sendMSG(CommandSender sender, String msg){
        getPlugin().getUtils().sendMSG(sender, msg);
    }
}
