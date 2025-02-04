package com.ar.askgaming.survivalduels.Utils;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.ar.askgaming.survivalduels.SurvivalDuels;

public class DuelLogger {

    private SurvivalDuels plugin;
    public DuelLogger(SurvivalDuels plugin) {
        this.plugin = plugin;

        file = new File(plugin.getDataFolder(), "duels.log");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    File file;
    
    public void log(String message){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String format = now.format(formatter);
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.write(format + " " + message + "\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
