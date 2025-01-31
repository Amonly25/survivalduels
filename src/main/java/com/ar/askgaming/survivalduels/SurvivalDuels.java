package com.ar.askgaming.survivalduels;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.survivalduels.Arenas.Arena;
import com.ar.askgaming.survivalduels.Arenas.ArenaManager;
import com.ar.askgaming.survivalduels.Duels.DuelManager;
import com.ar.askgaming.survivalduels.Kits.Kit;
import com.ar.askgaming.survivalduels.Kits.KitManager;
import com.ar.askgaming.survivalduels.Listeners.EntityDamageByEntityListener;
import com.ar.askgaming.survivalduels.Listeners.PlayerMoveListener;
import com.ar.askgaming.survivalduels.Listeners.PlayerQuitListener;

public class SurvivalDuels extends JavaPlugin {

    private DuelManager duelmanager;
    private ArenaManager arenamanager;
    private KitManager kitmanager;

    public void onEnable() {
        
        saveDefaultConfig();

        ConfigurationSerialization.registerClass(Arena.class,"Arena");
        ConfigurationSerialization.registerClass(Kit.class,"Kit");

        duelmanager = new DuelManager(this);
        arenamanager = new ArenaManager(this);
        kitmanager = new KitManager(this);
                
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
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
    public KitManager getKitmanager() {
        return kitmanager;
    }
    
    
}