package com.ar.askgaming.survivalduels.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Duels.Duel;
import com.ar.askgaming.survivalduels.Duels.Queue;

public class PlayerQuitListener implements Listener{

    private SurvivalDuels plugin;
    public PlayerQuitListener(SurvivalDuels plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        
        Player p = event.getPlayer();
                   
        Queue queue = plugin.getQueueManager().isInQueue(p);
        if (queue != null) {
            queue.removePlayer(p);
            return;
        }

        Duel duel = plugin.getDuelmanager().getDuel(p);
        if (duel == null) {
            return;
        }
        plugin.getDuelLogger().log("Player " + p.getName() + " has left the game while in a duel.");
        duel.eliminatePlayer(p);
        
    }
}
