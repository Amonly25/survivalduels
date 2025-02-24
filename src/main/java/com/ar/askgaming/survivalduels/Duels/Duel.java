package com.ar.askgaming.survivalduels.Duels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Arenas.Arena;
import com.ar.askgaming.survivalduels.Duels.DuelManager.DuelState;
import com.ar.askgaming.survivalduels.Duels.DuelManager.MessageType;
import com.ar.askgaming.survivalduels.Kits.Kit;

import net.md_5.bungee.api.ChatColor;

public class Duel {

    private SurvivalDuels plugin = SurvivalDuels.getPlugin(SurvivalDuels.class);

    private final Team team1;
    private final Team team2;
    private final Arena arena;
    private final String kit;	
    private final String arenaName;
    private final int countdown = 10;
    private DuelState state;
    private boolean useOwnInventory = false;
    private boolean keepInventory = false;

    private HashMap<Location, Material> blocks = new HashMap<>();
    private List<Player> spectators = new ArrayList<>();

    //#region Constructor
    public Duel(Team team1, Team team2, Arena arena, Kit kit) {

        useOwnInventory = plugin.getConfig().getBoolean("duels.useOwnInventory.enabled",false);
        keepInventory = plugin.getConfig().getBoolean("duels.useOwnInventory.keepInventory",false);

        this.team1 = team1; 
        this.team2 = team2;
        this.arena = arena;
        this.state = DuelState.COUNTDOWN;
        this.kit = kit.getName();
        this.arenaName = arena.getName();

        if (!teleportTeams(team1.getDuelPlayers(), arena.getSpawnsTeam1()) ||
            !teleportTeams(team2.getDuelPlayers(), arena.getSpawnsTeam2())) {
                sendMessageToTeams(MessageType.ERROR_TP);
                plugin.getDuelLogger().log("Error while teleporting players to the arena, someone doest have teleport permission, The duel has been cancelled.");
                rollBackPlayers();
                return;
        }

        sendMessageToTeams(MessageType.PREPARE);
       
        plugin.getDuelLogger().log("Duel between " + team1.getName() + " and " + team2.getName() + " started. Arena: " + arenaName + ", Kit: " + kit.getName());

        plugin.getKitmanager().giveKit(team1.getDuelPlayers(), kit);
        plugin.getKitmanager().giveKit(team2.getDuelPlayers(), kit);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                state = DuelState.INGAME;
                sendMessageToTeams(MessageType.START);
            }
        }, countdown * 20);
    }
    //#region teleportTeams
    private boolean teleportTeams(List<Player> list, Location location) {
        int size = list.size();
        int counter = 0;
        for (Player player : list) {
            if (player.teleport(location, TeleportCause.PLUGIN)){
                counter++;
            }
            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
            player.setGameMode(GameMode.SURVIVAL);
            player.setFoodLevel(20);
            player.getActivePotionEffects().clear();
        }
        return counter == size;
    }
    private void sendMessageToTeams(MessageType type) {
        sendMessageToPlayers(team1.getDuelPlayers(), type);
        sendMessageToPlayers(team2.getDuelPlayers(), type);
    }
    //#region sendMessage
    private void sendMessageToPlayers(List<Player> list, MessageType type) {
        for (Player player : list) {
            switch (type) {
                case START:
                    player.sendTitle(ChatColor.GREEN+"Go!", "", 10, 50, 20);
                            case END:

                    break;
                case PREPARE:
                    player.sendMessage(plugin.getLangManager().getFrom("duel."+type.toString().toLowerCase(), player)
                    .replace("{kit}", kit)
                    .replace("{arena}", arenaName)
                    .replace("{time}", countdown + "")
                    );
                    new BukkitRunnable() {		
                        int count = countdown;
                        
                        @Override
                        public void run() {	      
                            
                            if (count == 0) {        		
                                cancel(); 
                                return;
                            }	    	    	                                    	    	                        
                            player.sendTitle("", ChatColor.GRAY+String.valueOf(count), 10, 20, 20);
                            count--;  
                        }
                    }.runTaskTimer(plugin, 0L, 20L); 
                    break;
                case ERROR_TP:
                    player.sendMessage(plugin.getLangManager().getFrom("duel."+type.toString().toLowerCase(), player));
                    break;
                default:
                    break;
            }
        }
    }
    //#region eliminatePlayer
    public void eliminatePlayer(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        spectators.add(player);
        player.getInventory().clear();

        team1.getAlivePlayers().remove(player);
        team2.getAlivePlayers().remove(player);

        if (team1.getAlivePlayers().isEmpty()) {
            endDuel(team2, team1);
        } else if (team2.getAlivePlayers().isEmpty()) {
            endDuel(team1, team2);
        }
    }
    //#region endDuel
    public void endDuel(Team winner, Team losser) {

        state = DuelState.END;
        arena.setInUse(false);
        plugin.getDuelmanager().getTeams().remove(winner);
        plugin.getDuelmanager().getTeams().remove(losser);
    
        List<Player> allPlayers = new ArrayList<>(winner.getDuelPlayers());
        allPlayers.forEach(p -> p.getInventory().clear());
        
        allPlayers.addAll(spectators);
        for (Player pl : allPlayers) {
            if (useOwnInventory && !keepInventory) {
                plugin.getDuelmanager().getLastInventory().put(pl.getUniqueId(), new ItemStack[36]);
            }
            plugin.getDuelmanager().checkRollBack(pl);
        }

        for (Player pl : Bukkit.getOnlinePlayers()){
            if (pl.hasPermission("survivalduels.use")) {
                pl.sendMessage(plugin.getLangManager().getFrom("duel.ended", pl).replace("{winner}", winner.getName()).replace("{loser}", losser.getName()));
            }
        };
        plugin.getDuelLogger().log("Duel between " + team1.getName() + " and " + team2.getName() + " ended. Winner: " + winner.getName());
        plugin.getDuelmanager().modifyStats(winner, losser);

        for (Location loc : blocks.keySet()) {
            loc.getBlock().setType(blocks.get(loc));
        }
        plugin.getDuelmanager().getDuels().remove(this);
    }
    private void rollBackPlayers() {
        for (Player player : team1.getDuelPlayers()) {
            plugin.getDuelmanager().checkRollBack(player);
        }
        for (Player player : team2.getDuelPlayers()) {
            plugin.getDuelmanager().checkRollBack(player);
        }
    }
                
    //#region getters
    public List<Player> getSpectators() {
        return spectators;
    }
    public HashMap<Location, Material> getBlocks() {
        return blocks;
    }
    public Team getTeam1() {
        return team1;
    }
    public Team getTeam2() {
        return team2;
    }
    public DuelState getState() {
        return state;
    }
    public void setState(DuelState state) {
        this.state = state;
    }
    public Arena getArena() {
        return arena;
    }
  
}
