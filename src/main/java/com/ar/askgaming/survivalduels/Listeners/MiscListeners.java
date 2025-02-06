package com.ar.askgaming.survivalduels.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Duels.Duel;

public class MiscListeners implements Listener{

    private final SurvivalDuels plugin;

    public MiscListeners(SurvivalDuels plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void foodChangeEvent(FoodLevelChangeEvent e) {
    	if (e.getEntity() instanceof Player) {
    		Player p = (Player) e.getEntity();
    		Duel duel = plugin.getDuelmanager().getDuel(p);
            if (duel == null) {
                return;
            }
            e.setCancelled(true);    
    	}
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
    	if (e.getEntity() instanceof Player) {
    		Player p = (Player) e.getEntity();
    		Duel duel = plugin.getDuelmanager().getDuel(p);
            if (duel == null) {
                return;
            }
            e.setCancelled(true);
                        
    	}
    }
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        Duel duel = plugin.getDuelmanager().getDuel(p);
        if (duel == null) {
            return;
        }
        e.setCancelled(true);
    }

}
