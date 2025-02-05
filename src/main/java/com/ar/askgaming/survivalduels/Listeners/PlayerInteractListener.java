package com.ar.askgaming.survivalduels.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Duels.Duel;

public class PlayerInteractListener implements Listener{

    private SurvivalDuels plugin;

    public PlayerInteractListener(SurvivalDuels plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
	public void onItemConsume(PlayerItemConsumeEvent e) {
		
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		
        Duel duel = plugin.getDuelmanager().getDuel(p);
        if (duel == null) {
            return;
        }

		if (item.getType() == Material.POTION) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                for (ItemStack i : p.getInventory().getContents()) {
                    if (i!=null && i.getType() == Material.GLASS_BOTTLE) {
                        i.setAmount(0);
                    }
                }	                
            }, 5L);
        }
	}
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		
        Duel duel = plugin.getDuelmanager().getDuel(p);
        if (duel == null) {
            return;
        }

		if (item == null) {
			return;
		}
		if (e.getHand() == EquipmentSlot.OFF_HAND){
            return;
        }
		if (item.getType() == Material.MUSHROOM_STEW) {
            if (p.getHealth() == 20.0) {
                return;
            }

            if (p.getHealth() >= 14.5) {		                    
                p.setHealth(20.0);
            }
            else {
                p.setHealth(p.getHealth() + 5.5);
            }
            
            item.setAmount(0);
        }
    }
}
