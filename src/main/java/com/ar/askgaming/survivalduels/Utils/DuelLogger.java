package com.ar.askgaming.survivalduels.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.ar.askgaming.survivalduels.SurvivalDuels;

public class DuelLogger {

    private final SurvivalDuels plugin;
    private final File file;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public DuelLogger(SurvivalDuels plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "duels.log");
    }

    public void log(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = timestamp + " " + message;

        synchronized (this) { // Evita problemas en entornos multihilo
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(logMessage);
                writer.newLine();
            } catch (IOException e) {
                plugin.getLogger().warning("Error al escribir en duels.log: " + e.getMessage());
            }
        }
    }
}