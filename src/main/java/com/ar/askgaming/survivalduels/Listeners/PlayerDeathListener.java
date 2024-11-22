package com.ar.askgaming.survivalduels.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.ar.askgaming.survivalduels.Duel;
import com.ar.askgaming.survivalduels.DuelManager;
import com.ar.askgaming.survivalduels.SurvivalDuels;

public class PlayerDeathListener implements Listener{

    private SurvivalDuels plugin;
    public PlayerDeathListener(SurvivalDuels plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        
        Player p = event.getEntity();
        DuelManager dm = plugin.getDuelmanager();
                   
        Duel duel = dm.getDuel(p);
        if (duel == null) {
            return;
            
        }
        duel.checkOnPlayerDeath(p);
        
    }
}
