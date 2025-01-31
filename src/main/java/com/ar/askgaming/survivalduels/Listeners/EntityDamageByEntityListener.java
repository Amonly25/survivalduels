package com.ar.askgaming.survivalduels.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Duels.Duel;
import com.ar.askgaming.survivalduels.Duels.Team;

public class EntityDamageByEntityListener implements Listener {

    private SurvivalDuels plugin;
    public EntityDamageByEntityListener(SurvivalDuels plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        
        if (event.getEntity() instanceof Player) {

            handlePlayerDamage(event);
        }

    }
    private void handlePlayerDamage(EntityDamageByEntityEvent event) {
        Player damaged = (Player) event.getEntity();
        Player damager = null;
    
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }
    
        if (damager != null) {

            Duel duel = plugin.getDuelmanager().getDuel(damaged);
            if (duel == null) {
                return;
            }
            Duel duelDamager = plugin.getDuelmanager().getDuel(damager);
            if (duelDamager == null) {
                return;
            }
            if (!duel.equals(duelDamager)) {
                event.setCancelled(true);
                return;
            }
            
            Team teamDamaged = plugin.getDuelmanager().isInTeam(damaged);
            Team TeamDamager = plugin.getDuelmanager().isInTeam(damager);

            if (teamDamaged != null && TeamDamager != null && teamDamaged.equals(TeamDamager)) {
                event.setCancelled(true);
            }
            
            if (damaged.getHealth() - event.getFinalDamage() <= 0) {
                damaged.setHealth(20);
                damaged.setGameMode(GameMode.SPECTATOR);
                duel.checkOnPlayerDeath(damaged);
            }
        }
    }
}
