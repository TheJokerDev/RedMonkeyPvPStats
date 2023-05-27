package plugin.template.spigot.commands.sub.spigottemplate;

import org.bukkit.command.CommandSender;
import plugin.template.spigot.Main;
import plugin.template.spigot.enums.SenderTypes;
import plugin.template.spigot.type.SubCMD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReloadSubCMD extends SubCMD {

    public ReloadSubCMD(Main plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "none";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("recargar", "reiniciar");
    }

    @Override
    public SenderTypes getSenderType() {
        return SenderTypes.BOTH;
    }

    @Override
    public boolean onCommand(CommandSender sender, String alias, String[] args) {
        if (args.length == 0){
            getPlugin().getConfigUtil().reloadAll();
            getPlugin().getItemsManager().reloadItems();
            sendMSG(sender, "commands.main.reload.success");
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        return new ArrayList<>();
    }
}
