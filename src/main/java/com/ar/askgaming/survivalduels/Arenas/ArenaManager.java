package com.ar.askgaming.survivalduels.Arenas;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ar.askgaming.survivalduels.SurvivalDuels;

public class ArenaManager {

    File file;
    FileConfiguration config;

    private SurvivalDuels plugin;
    public ArenaManager(SurvivalDuels plugin) {

        this.plugin = plugin;

        new Commands(plugin);

        file = new File(plugin.getDataFolder(), "arenas.yml");

        if (!file.exists()) {
            plugin.saveResource("arenas.yml", false);
        }
        config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set<String> Keys = config.getKeys(false);

        // Iterar sobre todas las keys y cargar cada Arena
        for (String key : Keys) {
            Object obj = config.get(key);
            if (obj instanceof Arena) {
                Arena arena = (Arena) obj;
                arenas.add(arena);
            }
        }
    }
    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Arena> arenas = new ArrayList<>();
    
    public List<Arena> getArenas() {
        return arenas;
    }
 
    public Arena getArenaAvailable(int size) {
        if (arenas.isEmpty()) {
            return null;
        }

        for (Arena arena : arenas) {
            if (arena.getMaxPlayers() >= size && arena.isInUse() == false && arena.getSpawnsTeam1() != null && arena.getSpawnsTeam2() != null) {
                return arena;
            }
        }
        return null;
    }

    public void createArena(String name) {
        Arena arena = new Arena(name);
        arenas.add(arena);
        config.set(name, arena);
        save();
    }

    public void deleteArena(Arena arena) {
        arenas.remove(arena);
        config.set(arena.getName(), null);
        save();

    }
    public Arena getByName(String name) {
        return arenas.stream().filter(a -> a.getName().equals(name)).findFirst().orElse(null);
    }
}
