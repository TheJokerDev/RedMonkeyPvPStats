package plugin.template.spigot.managers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import plugin.template.spigot.Main;
import plugin.template.spigot.commands.SpigotTemplateCMD;
import plugin.template.spigot.type.CMD;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CMDManager {
    private Main plugin;
    public List<CMD> commands = new ArrayList<>();


    public CMDManager(Main plugin){
        this.plugin = plugin;

        //Add the class containing CMD extension.
        //commands.add(new Class());
        commands.add(new SpigotTemplateCMD(plugin));

        if (commands.size() > 0) commands.forEach(this::registerCommand);
    }



    public void registerCommand(CMD cmd) {
        if (plugin.getCommand(cmd.getName()) == null) {
            PluginCommand command = getCommand(cmd.getName(), plugin);
            if (cmd.getPermission() != null || cmd.getPermission().equals("none")) {
                command.setPermission(cmd.getPermission());
            }
            if (cmd.getDescription() != null) command.setDescription(cmd.getDescription());
            if (cmd.getAliases().size() > 0) command.setAliases(cmd.getAliases());
            if (cmd.getPermissionError()!=null) command.setPermissionMessage(cmd.getPermissionError());
            try {
                getCommandMap().register(plugin.getDescription().getName(), command);
            } catch (Exception e) {
                return;
            }
            plugin.getCommand(cmd.getName()).setExecutor(cmd);
            if (cmd.isTabComplete()) {
                plugin.getCommand(cmd.getName()).setTabCompleter(cmd);
            }
            plugin.console("   &b→ &fLoaded command: &a"+cmd.getName());
        }
    }

    private PluginCommand getCommand(String name, Main plugin) {
        PluginCommand command = null;
        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);
            command = c.newInstance(name, plugin);
        } catch (SecurityException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return command;
    }

    private CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);
                commandMap = (CommandMap)f.get(Bukkit.getPluginManager());
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return commandMap;
    }


}
