package org.dailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dailyRewards.Commands.DailyCommand;
import org.dailyRewards.Getter.Language;
import org.dailyRewards.Listeners.GuiListener;
import org.dailyRewards.Models.YamlFile;

import java.util.ArrayList;
import java.util.List;


public final class DailyRewards extends JavaPlugin {

    public static YamlFile PLAYER_COLLECTED;
    public static YamlFile GUI_CONFIG;
    public static boolean IS_PLACEHOLDER_API;


    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        PLAYER_COLLECTED = new YamlFile("playerCollected.yml");
        GUI_CONFIG = new YamlFile("guiConfig.yml");

        Language.reload();

        initializeCommands();

        getServer().getPluginManager().registerEvents(new GuiListener(),this);

        IS_PLACEHOLDER_API= Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    private void initializeCommands(){
        FileConfiguration config = getConfig();

        String name = config.getString("command.name","daily");
        String description = config.getString("command.description","Everything about daily");
        String usage = config.getString("command.usage","/daily");
        List<String> aliases = config.getStringList("command.aliases");
        String defaultPermission = config.getString("default-permission","daily.command.default");
        PermissionDefault permissionDefault = PermissionDefault.getByName(config.getString("default-permission-state","true"));
        if(permissionDefault==null){
            permissionDefault=PermissionDefault.TRUE;
        }

        new DailyCommand(name,description,usage,aliases,defaultPermission,permissionDefault);
    }

    public static DailyRewards getInstance(){
        return JavaPlugin.getPlugin(DailyRewards.class);
    }

    public static  <T> List<T> transformList(@NonNull List<?> list, Class<T> tClass){
        List<T> transformedList = new ArrayList<>();
        for(Object object : list){
            if(tClass.isInstance(object)){
                transformedList.add(tClass.cast(object));
            }
        }

        return transformedList;
    }

    public static <T> int sizeOfArrayWithoutNull(T[] list){
        int size=0;
        for(Object o : list){
            if(o!=null){
                size++;
            }
        }

        return size;
    }
}
