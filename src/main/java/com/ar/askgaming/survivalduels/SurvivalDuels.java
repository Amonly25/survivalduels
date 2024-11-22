package com.ar.askgaming.survivalduels;

import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.survivalduels.Listeners.EntityDamageByEntityListener;
import com.ar.askgaming.survivalduels.Listeners.PlayerDeathListener;
import com.ar.askgaming.survivalduels.Listeners.PlayerMoveListener;
import com.ar.askgaming.survivalduels.Listeners.PlayerQuitListener;

public class SurvivalDuels extends JavaPlugin {

    private DuelManager duelmanager;
    private ArenaManager arenamanager;

    public void onEnable() {
        
        saveDefaultConfig();
        duelmanager = new DuelManager(this);
        arenamanager = new ArenaManager(this);

        getServer().getPluginCommand("duels").setExecutor(new Commands(this));

        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);

    }

    public void onDisable() {
        
    }

    public DuelManager getDuelmanager() {
        return duelmanager;
    }
    public ArenaManager getArenamanager() {
        return arenamanager;
    }
    
    
}