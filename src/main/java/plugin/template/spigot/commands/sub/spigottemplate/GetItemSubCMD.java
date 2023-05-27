package plugin.template.spigot.commands.sub.spigottemplate;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import plugin.template.spigot.Main;
import plugin.template.spigot.enums.SenderTypes;
import plugin.template.spigot.type.DataPlayer;
import plugin.template.spigot.type.SubCMD;

import java.util.ArrayList;
import java.util.List;

public class GetItemSubCMD extends SubCMD {

    public GetItemSubCMD(Main plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "getitem";
    }

    @Override
    public String getPermission() {
        return "none";
    }

    @Override
    public SenderTypes getSenderType() {
        return SenderTypes.PLAYER;
    }

    @Override
    public boolean onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        DataPlayer dp = getPlugin().getDataManager().getDataPlayer(p);
        if (args.length == 1){
            List<String> items = new ArrayList<>(dp.getItems().keySet());
            if (items.size() == 0){
                sendMSG(sender, "commands.main.getItem.noItems");
                return true;
            }
            String item = args[0];
            if (!items.contains(item)){
                sendMSG(sender, "commands.main.getItem.itemNotExists");
                return true;
            }
            ItemStack itemStack = dp.getItems().get(item).getItem().build(p);
            p.getInventory().addItem(itemStack);
            String line = getPlugin().getUtils().getKey("commands.main.getItem.success");
            line = line.replace("{amount}", itemStack.getAmount()+"").replace("{item}", itemStack.getType().name());
            sendMSG(sender, line);
        }
        return true;
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        if (!check(sender)){
            return new ArrayList<>();
        }
        Player p = (Player) sender;
        DataPlayer dp = getPlugin().getDataManager().getDataPlayer(p);
        if (args.length == 1){
            return StringUtil.copyPartialMatches(args[0], new ArrayList<>(dp.getItems().keySet()), new ArrayList<>());
        }
        return new ArrayList<>();
    }
}
