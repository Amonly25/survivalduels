package com.ar.askgaming.survivalduels.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.ar.askgaming.survivalduels.SurvivalDuels;

public class Language {

    private final SurvivalDuels plugin;
    private final String prefix;
    private final HashMap<String, HashMap<String, String>> cache = new HashMap<>();

    public Language(SurvivalDuels plugin) {
        this.plugin = plugin;

        // Guardar los archivos de idioma si no existen
        saveDefaultLang("en.yml");
        saveDefaultLang("es.yml");

        // Cargar prefijo
        this.prefix = plugin.getConfig().getString("prefix", "§7[§6&lDuels§7] ");
    }

    private void saveDefaultLang(String fileName) {
        File langFile = new File(plugin.getDataFolder() + "/lang/" + fileName);
        if (!langFile.exists()) {
            plugin.saveResource("lang/" + fileName, false);
        }
    }

    public String getFrom(String path, Player p) {
        String locale = (p == null) ? "en" : p.getLocale().split("_")[0];
    
        // Si el idioma no existe, usar inglés como fallback inmediato
        File file = new File(plugin.getDataFolder() + "/lang/" + locale + ".yml");
        if (!file.exists()) {
            locale = "en";
            file = new File(plugin.getDataFolder() + "/lang/en.yml");
        }
    
        // Verificar caché
        if (cache.containsKey(locale) && cache.get(locale).containsKey(path)) {
            return ChatColor.translateAlternateColorCodes('&', prefix + cache.get(locale).get(path));
        }
    
        // Cargar mensaje desde archivo
        String required = loadMessage(file, path);
    
        // Si el mensaje no se encuentra en el archivo de idioma, obtener el de inglés
        if (required.startsWith("Error:")) {
            File defaultFile = new File(plugin.getDataFolder() + "/lang/en.yml");
            required = loadMessage(defaultFile, path);
        }
    
        // Guardar en caché
        cache.computeIfAbsent(locale, k -> new HashMap<>()).put(path, required);
        if (path.startsWith("misc.")) {
            return ChatColor.translateAlternateColorCodes('&', required);
        }
        return ChatColor.translateAlternateColorCodes('&', prefix + required);
    }
    
    private String loadMessage(File file, String path) {
        FileConfiguration langFile = new YamlConfiguration();
        try {
            langFile.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning("Error loading lang file: " + file.getName());
            e.printStackTrace();
            return "Error: Missing lang file";
        }
    
        if (langFile.isList(path)) {
            return String.join("\n", langFile.getStringList(path));
        }
    
        return langFile.getString(path, "Error: Invalid lang path: " + path); // Retornar error si la clave no existe
    }
    
}