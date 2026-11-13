package org.dailyRewards.Getter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.dailyRewards.DailyRewards;

import java.io.File;

public class Language {

    private static FileConfiguration LANGUAGE;

    public static void reload(){
        DailyRewards dailyRewards = DailyRewards.getInstance();
        File english = new File(dailyRewards.getDataFolder()+File.separator+"languages","en.yml");
        if(!english.exists()){
            dailyRewards.saveResource("languages/en.yml",false);
        }

        FileConfiguration config = dailyRewards.getConfig();

        String selectLanguage = config.getString("language","en");
        File file = new File(dailyRewards.getDataFolder()+File.separator+"languages",selectLanguage+".yml");
        if(!file.exists()){
            file=english;
        }

        LANGUAGE= YamlConfiguration.loadConfiguration(file);
    }

    public static String getTranslation(String s){
        return LANGUAGE.getString(s,"");
    }
}
