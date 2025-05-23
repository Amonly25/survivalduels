package com.ar.askgaming.survivalduels.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
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
        handleBlockModification(event.getPlayer(), event.getBlock().getLocation(), event.getBlock().getType());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        handleBlockModification(event.getPlayer(), event.getBlock().getLocation(), Material.AIR);
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        handleBlockModification(event.getPlayer(), block.getLocation(), Material.AIR);
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Block block = event.getBlockClicked(); // Se llena el bloque tocado
        handleBlockModification(event.getPlayer(), block.getLocation(), block.getType());
    }

    private void handleBlockModification(Player player, Location loc, Material material) {
        Duel duel = plugin.getDuelmanager().getDuel(player);
        if (duel == null) return;

        // Solo registrar bloques si aún no están guardados
        if (!duel.getBlocks().containsKey(loc)) {
            duel.getBlocks().put(loc, material);
        }
    }

}
