package com.ar.askgaming.survivalduels.Utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ar.askgaming.survivalduels.SurvivalDuels;

public class PlayerData {

    File file;
    FileConfiguration config;

    public PlayerData(SurvivalDuels plugin) {
        file = new File(plugin.getDataFolder(), "playerdata.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }  
    }
    public void save(){
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public FileConfiguration getConfig() {
        return config;
    }

}
