package com.ar.askgaming.survivalduels.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Duels.Duel;
import com.ar.askgaming.survivalduels.Duels.DuelManager;
import com.ar.askgaming.survivalduels.Duels.Queue;

public class PlayerQuitListener implements Listener{

    private SurvivalDuels plugin;
    public PlayerQuitListener(SurvivalDuels plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        
        Player p = event.getPlayer();
        DuelManager dm = plugin.getDuelmanager();
                   
        Queue queue = dm.isInQueue(p);
        if (queue != null) {
            queue.removePlayer(p);
            return;
        }

        Duel duel = dm.getDuel(p);
        if (duel == null) {
            return;
        }
        duel.checkOnPlayerQuit(p);
        
    }
}
