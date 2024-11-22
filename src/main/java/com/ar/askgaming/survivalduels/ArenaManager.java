package com.ar.askgaming.survivalduels;

import java.util.ArrayList;
import java.util.List;

public class ArenaManager {

    private List<Arena> arenas = new ArrayList<>();
    private SurvivalDuels plugin;
    
    public List<Arena> getArenas() {
        return arenas;
    }

    public ArenaManager(SurvivalDuels plugin) {
        this.plugin = plugin;
        //Load arenas from config
    }
 
    public Arena getArenaAvailable(int size) {
        if (arenas.isEmpty()) {
            return null;
        }

        for (Arena arena : arenas) {
            if (arena.getMaxPlayers() >= size && arena.isInUse() == false) {
                return arena;
            }
        }
        return null;
    }
}
