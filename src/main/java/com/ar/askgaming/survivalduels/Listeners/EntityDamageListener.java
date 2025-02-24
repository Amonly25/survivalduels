package com.ar.askgaming.survivalduels.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Duels.Duel;
import com.ar.askgaming.survivalduels.Duels.Team;

public class EntityDamageListener implements Listener {

    private SurvivalDuels plugin;
    public EntityDamageListener(SurvivalDuels plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damaged = (Player) event.getEntity();
        Player damager = getDamager(event.getDamager());

        if (damager == null) {
            return;
        }

        Duel duel = plugin.getDuelmanager().getDuel(damaged);
        Duel duelDamager = plugin.getDuelmanager().getDuel(damager);

        if (duel == null || duelDamager == null) {
            return;
        }

        Team teamDamaged = plugin.getDuelmanager().isInTeam(damaged);
        Team teamDamager = plugin.getDuelmanager().isInTeam(damager);

        if (teamDamaged != null && teamDamager != null && teamDamaged.equals(teamDamager)) {
            event.setCancelled(true);
            return;
        }

        handlePossibleDeath(damaged, event);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damaged = (Player) event.getEntity();
        Duel duel = plugin.getDuelmanager().getDuel(damaged);
        
        if (duel == null) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }

        handlePossibleDeath(damaged, event);
    }

    private Player getDamager(Entity damagerEntity) {
        if (damagerEntity instanceof Player) {
            return (Player) damagerEntity;
        }

        if (damagerEntity instanceof Projectile) {
            Projectile projectile = (Projectile) damagerEntity;
            if (projectile.getShooter() instanceof Player) {
                return (Player) projectile.getShooter();
            }
        }
        return null;
    }

    private void handlePossibleDeath(Player damaged, EntityDamageEvent event) {
        if (damaged.getHealth() - event.getFinalDamage() <= 0) {
            Duel duel = plugin.getDuelmanager().getDuel(damaged);
            if (duel != null) {
                duel.eliminatePlayer(damaged);
                event.setCancelled(true);
            }
        }
    }
}