package com.ar.askgaming.survivalduels.Listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Duels.Duel;

public class PlayerBlockListener implements Listener{

    private SurvivalDuels plugin;
    public PlayerBlockListener(SurvivalDuels plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Duel duel = plugin.getDuelmanager().isInDuel(p);
        if (duel == null) return;
        Block b = event.getBlock();
        duel.getBlocks().put(b, b.getType());
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        Duel duel = plugin.getDuelmanager().isInDuel(p);
        if (duel == null) return;
        Block b = event.getBlock();
        duel.getBlocks().put(b, b.getType());
    }
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        Player p = e.getPlayer();
        Duel duel = plugin.getDuelmanager().isInDuel(p);
        if (duel == null) return;
        Block b = e.getBlock();
        duel.getBlocks().put(b, b.getType());
    }
    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent e) {
        Player p = e.getPlayer();
        Duel duel = plugin.getDuelmanager().isInDuel(p);
        if (duel == null) return;
        Block b = e.getBlock();
        duel.getBlocks().put(b, b.getType());
    }

}
