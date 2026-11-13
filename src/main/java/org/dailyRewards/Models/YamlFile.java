package org.dailyRewards.Models;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.dailyRewards.DailyRewards;

import java.io.File;
import java.io.IOException;

public class YamlFile {

    private final File file;
    private FileConfiguration config;


    public YamlFile(String path) {
        File file= new File(DailyRewards.getInstance().getDataFolder(),path);
        if(!file.exists()){
            DailyRewards.getInstance().saveResource(path,false);
        }

        this.file=file;
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig(){
        return config;
    }

    public void reload(){
        config=YamlConfiguration.loadConfiguration(file);
    }

    public void save(){
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
