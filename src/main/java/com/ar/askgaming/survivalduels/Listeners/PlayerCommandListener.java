package com.ar.askgaming.survivalduels.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.ar.askgaming.survivalduels.SurvivalDuels;

public class PlayerCommandListener implements Listener{

    private SurvivalDuels plugin;

    public PlayerCommandListener(SurvivalDuels plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        if (!plugin.getDuelmanager().isInDuel(p)) {
            return;
        }
        if (p.isOp()) return;
       
        if (!event.getMessage().equalsIgnoreCase("/duel leave")) { 
            event.setCancelled(true);
            p.sendMessage(plugin.getLangManager().getFrom("duel.cant_use_commands", p));
        }
    }

}
