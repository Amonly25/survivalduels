package com.ar.askgaming.survivalduels.Utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ar.askgaming.survivalduels.SurvivalDuels;

public class PlayerData {

    private final SurvivalDuels plugin;
    private File file;
    private FileConfiguration config;

    public PlayerData(SurvivalDuels plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "playerdata.yml");

        if (!file.exists()) {
            plugin.saveResource("playerdata.yml", false);
        }

        config = new YamlConfiguration();
        reload(); // Carga el archivo correctamente
    }

    public void reload() {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.WARNING, "Error loading playerdata.yml: " + e.getMessage());
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Error saving playerdata.yml: " + e.getMessage());
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void set(String path, Object value) {
        config.set(path, value);
        save(); // Guarda automáticamente los cambios
    }
}