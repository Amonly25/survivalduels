package com.ar.askgaming.survivalduels.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Duels.Duel;
import com.ar.askgaming.survivalduels.Duels.Duel.DuelState;

public class PlayerTeleportListener implements Listener{

    private SurvivalDuels plugin;

    public PlayerTeleportListener(SurvivalDuels plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event) {
        Player p = event.getPlayer();
        Duel duel = plugin.getDuelmanager().isInDuel(p);
        if (duel == null) return;

        if (event.getCause() == TeleportCause.ENDER_PEARL) {
            event.setCancelled(false);
            return;

        }
        if (duel.getState() == DuelState.INGAME || duel.getState() == DuelState.COUNTDOWN) {
            if (duel.getSpectators().contains(p)){
                return;
            }
            event.setCancelled(true);
            
        }
        if (duel.getState() == DuelState.END && event.isCancelled() ) {
            event.setCancelled(false);
        }
    }

}
