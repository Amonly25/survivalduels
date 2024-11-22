package com.ar.askgaming.survivalduels.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.ar.askgaming.survivalduels.Duel;
import com.ar.askgaming.survivalduels.SurvivalDuels;

public class PlayerMoveListener implements Listener{

    private SurvivalDuels plugin;
    public PlayerMoveListener(SurvivalDuels plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        
        Player p = event.getPlayer();
        
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        //Reducir el uso de memoria
        
        Duel duel = plugin.getDuelmanager().getDuel(p);
        if (duel == null) {
            return;
        }
        if (duel.getState() == Duel.DuelState.COUNTDOWN) {
            event.setCancelled(true);
        }
    }
    
}
