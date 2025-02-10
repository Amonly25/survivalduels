package com.ar.askgaming.survivalduels.Duels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Arenas.Arena;
import com.ar.askgaming.survivalduels.Kits.Kit;

import net.md_5.bungee.api.ChatColor;

public class Duel {

    private SurvivalDuels plugin = SurvivalDuels.getPlugin(SurvivalDuels.class);

    private final Team team1;
    private final Team team2;
    private final Arena arena;
    private Team winner;
    private Team loser;
    public enum DuelState {
        COUNTDOWN,
        INGAME,
        END
    }
    private enum MessageType {
        START,
        END,
        PREPARE,
        ERROR_TP
    }

    private List<Player> spectators = new ArrayList<>();
    private String kit;	
    private String arenaName;
    private int countdown = 10;
    private DuelState state;

    private boolean useOwnInventory = false;
    private boolean keepInventory = false;

    private HashMap<Block, Material> blocks = new HashMap<>();

    public HashMap<Block, Material> getBlocks() {
        return blocks;
    }
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
        for (Player pl : Bukkit.getOnlinePlayers()){
            if (pl.hasPermission("survivalduels.use")) {
                pl.sendMessage(plugin.getLangManager().getFrom("duel.created", pl).replace("{team1}",
                 team1.getName()).replace("{team2}", team2.getName()));
            }
        };

        sendMessageToTeams(MessageType.PREPARE);
       
        plugin.getDuelLogger().log("Duel between " + team1.getName() + " and " + team2.getName() + " started. Arena: " + arenaName + ", Kit: " + kit.getName());

        setKit(team1.getDuelPlayers(), kit);
        setKit(team2.getDuelPlayers(), kit);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                state = DuelState.INGAME;
                sendMessageToTeams(MessageType.START);
            }
        }, countdown * 20);
    }
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
    private void setKit(List<Player> list, Kit kit) {
        if (useOwnInventory) {
            return;
        }

        if (kit == null) {
            return;
        }
        for (Player player : list) {
            player.getInventory().setContents(kit.getItems());
            player.getInventory().setArmorContents(kit.getArmor());
        }
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
    public void checkOnPlayerDeath(Player player) {
        player.setHealth(20);
        player.setGameMode(GameMode.SPECTATOR);
        spectators.add(player);

        team1.getAlivePlayers().remove(player);
        team2.getAlivePlayers().remove(player);

        if (team1.getAlivePlayers().isEmpty()) {
            endDuel(team2, team1);
        } else if (team2.getAlivePlayers().isEmpty()) {
            endDuel(team1, team2);
        }
    }
    public void endDuel(Team winner, Team losser) {
        this.winner = winner;
        this.loser = losser;

        state = DuelState.END;
        arena.setInUse(false);
        plugin.getDuelmanager().getTeams().remove(winner);
    
        List<Player> allPlayers = new ArrayList<>(winner.getDuelPlayers());
        allPlayers.addAll(spectators);
        resetPlayers(allPlayers);

        for (Player pl : Bukkit.getOnlinePlayers()){
            if (pl.hasPermission("survivalduels.use")) {
                pl.sendMessage(plugin.getLangManager().getFrom("duel.ended", pl).replace("{winner}", winner.getName()).replace("{loser}", losser.getName()));
            }
        };
        plugin.getDuelLogger().log("Duel between " + team1.getName() + " and " + team2.getName() + " ended. Winner: " + winner.getName());
        plugin.getDuelmanager().modifyStats(winner, losser);

        for (Block block : blocks.keySet()) {
            block.setType(blocks.get(block));

        }
        plugin.getDuelmanager().getDuels().remove(this);
    }
    
    private void teleportBack(Player player) {
        World world = Bukkit.getWorld("world");
        Location location = world.getSpawnLocation();

        try {
            boolean tp = player.teleport(plugin.getDuelmanager().getLastLocation().getOrDefault(player, location), TeleportCause.PLUGIN);
            if (!tp) {
                plugin.getDuelLogger().log("Error while teleporting player back.");
            }
            if (useOwnInventory && !keepInventory) {
                for (Player p : loser.getDuelPlayers()) {
                    plugin.getDuelmanager().getLastInventory().put(p, new ItemStack[36]);

                }
            }
            player.getInventory().setContents(plugin.getDuelmanager().getLastInventory().get(player));

            plugin.getDuelLogger().log("Player " + player.getName() + " teleported back to spawn and inventory restored.");
            plugin.getDuelmanager().getLastLocation().remove(player);
            plugin.getDuelmanager().getLastInventory().remove(player);
        } catch (IllegalArgumentException e) {
            plugin.getDuelLogger().log("Error while teleporting/restoring player back. " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void resetPlayers(List<Player> players) {
        players.forEach(player -> {
            try {
                player.setGameMode(GameMode.SURVIVAL);
                player.getActivePotionEffects().clear();
                player.setFireTicks(0);
                teleportBack(player);
            } catch (Exception e) {
                plugin.getDuelLogger().log("Failed to rollback player " + player.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    public void checkOnPlayerQuit(Player p) {
        plugin.getDuelLogger().log(p.getName() + " quit the duel.");
        checkOnPlayerDeath(p);
    }
    
    public void rollBackPlayers() {
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(team1.getDuelPlayers());
        allPlayers.addAll(team2.getDuelPlayers());
        allPlayers.addAll(spectators);
    
        resetPlayers(allPlayers);
    }
  
}
