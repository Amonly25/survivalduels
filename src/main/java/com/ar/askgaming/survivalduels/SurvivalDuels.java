package com.ar.askgaming.survivalduels;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.survivalduels.Arenas.Arena;
import com.ar.askgaming.survivalduels.Arenas.ArenaManager;
import com.ar.askgaming.survivalduels.Duels.DuelManager;
import com.ar.askgaming.survivalduels.Kits.Kit;
import com.ar.askgaming.survivalduels.Kits.KitManager;
import com.ar.askgaming.survivalduels.Listeners.EntityDamageListener;
import com.ar.askgaming.survivalduels.Listeners.PlayerJoinListener;
import com.ar.askgaming.survivalduels.Listeners.PlayerMoveListener;
import com.ar.askgaming.survivalduels.Listeners.PlayerQuitListener;
import com.ar.askgaming.survivalduels.Utils.DuelLogger;

public class SurvivalDuels extends JavaPlugin {

    private DuelManager duelmanager;
    private ArenaManager arenamanager;
    private KitManager kitmanager;
    private DuelLogger logger;

    public void onEnable() {
        
        saveDefaultConfig();

        ConfigurationSerialization.registerClass(Arena.class,"Arena");
        ConfigurationSerialization.registerClass(Kit.class,"Kit");

        duelmanager = new DuelManager(this);
        arenamanager = new ArenaManager(this);
        kitmanager = new KitManager(this);
        logger = new DuelLogger(this);
                
        getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);

        new PlayerJoinListener(this);
    }

    public void onDisable() {
        getDuelmanager().onShutdown();
    }
    public DuelLogger getDuelLogger() {
        return logger;
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