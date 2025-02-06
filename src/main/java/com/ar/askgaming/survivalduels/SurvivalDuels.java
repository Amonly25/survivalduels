package com.ar.askgaming.survivalduels;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.survivalduels.Arenas.Arena;
import com.ar.askgaming.survivalduels.Arenas.ArenaManager;
import com.ar.askgaming.survivalduels.Duels.DuelManager;
import com.ar.askgaming.survivalduels.Kits.Kit;
import com.ar.askgaming.survivalduels.Kits.KitManager;
import com.ar.askgaming.survivalduels.Listeners.EntityDamageListener;
import com.ar.askgaming.survivalduels.Listeners.MiscListeners;
import com.ar.askgaming.survivalduels.Listeners.PlayerCommandListener;
import com.ar.askgaming.survivalduels.Listeners.PlayerInteractListener;
import com.ar.askgaming.survivalduels.Listeners.PlayerJoinListener;
import com.ar.askgaming.survivalduels.Listeners.PlayerMoveListener;
import com.ar.askgaming.survivalduels.Listeners.PlayerQuitListener;
import com.ar.askgaming.survivalduels.Utils.DuelLogger;
import com.ar.askgaming.survivalduels.Utils.Language;
import com.ar.askgaming.survivalduels.Utils.PlaceHolders;
import com.ar.askgaming.survivalduels.Utils.PlayerData;

public class SurvivalDuels extends JavaPlugin {

    private DuelManager duelmanager;
    private ArenaManager arenamanager;
    private KitManager kitmanager;
    private DuelLogger logger;
    private Language langManager;
    private PlayerData playerData;

    public void onEnable() {
        
        saveDefaultConfig();

        ConfigurationSerialization.registerClass(Arena.class,"Arena");
        ConfigurationSerialization.registerClass(Kit.class,"Kit");

        langManager = new Language(this);
        playerData = new PlayerData(this);
        duelmanager = new DuelManager(this);
        arenamanager = new ArenaManager(this);
        kitmanager = new KitManager(this);
        logger = new DuelLogger(this);
                
        getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);

        new PlayerJoinListener(this);
        new PlayerInteractListener(this);
        new PlayerCommandListener(this);
        new MiscListeners(this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolders(this).register();
        }
    }

    public void onDisable() {
        getDuelmanager().onShutdown();
    }
    public PlayerData getPlayerData() {
        return playerData;
    }
    public DuelLogger getDuelLogger() {
        return logger;
    }
    public Language getLangManager() {
        return langManager;
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