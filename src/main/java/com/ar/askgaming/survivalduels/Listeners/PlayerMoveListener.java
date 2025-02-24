package com.ar.askgaming.survivalduels.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Duels.Duel;
import com.ar.askgaming.survivalduels.Duels.DuelManager.DuelState;

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
        if (duel.getState() == DuelState.COUNTDOWN) {
            event.setCancelled(true);
        }
        if (event.getTo().getY() < 0){
            p.teleport(duel.getArena().getSpawnsTeam1());
            duel.eliminatePlayer(p);

        }
    }
    @EventHandler
	public void onTp(PlayerTeleportEvent e) {
		Player p = e.getPlayer();

		if (p.isOp()) return;

		Duel duel = plugin.getDuelmanager().getDuel(p);
        if (duel == null) {
            return;
        }
        e.setCancelled(true);
	}
    
}
