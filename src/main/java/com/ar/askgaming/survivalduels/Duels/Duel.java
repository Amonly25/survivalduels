package com.ar.askgaming.survivalduels.Duels;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Arenas.Arena;
import com.ar.askgaming.survivalduels.Kits.Kit;

public class Duel {

    private SurvivalDuels plugin = SurvivalDuels.getPlugin(SurvivalDuels.class);

    private final Team team1;
    private final Team team2;
    private final Arena arena;
    public enum DuelState {
        COUNTDOWN,
        INGAME,
        END
    }

    private List<Player> spectators = new ArrayList<>();

    private int countdown = 10;
    private DuelState state;

    public Duel(Team team1, Team team2, Arena arena, Kit kit) {
        this.team1 = team1; 
        this.team2 = team2;
        this.arena = arena;
        this.state = DuelState.COUNTDOWN;

        Bukkit.broadcastMessage("El duelo entre " + team1.getName() + " y " + team2.getName() + " comenzará en " + countdown + " segundos.");
        sendMessageToTeams("El duelo se llevará a cabo en la arena " + arena.getName() + ", usando el kit " + kit.getName() + ".");

        teleportTeams(team1.getPlayers(), arena.getSpawnsTeam1());
        teleportTeams(team2.getPlayers(), arena.getSpawnsTeam2());

        plugin.getDuelLogger().log("Duel between " + team1.getName() + " and " + team2.getName() + " started. Arena: " + arena.getName() + ", Kit: " + kit.getName());

        setKit(team1.getPlayers(), kit);
        setKit(team2.getPlayers(), kit);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                state = DuelState.INGAME;
                sendMessageToTeams("El duelo ha comenzado.");
            }
        }, countdown * 20);
    }
    private void teleportTeams(List<Player> list, Location location) {
        for (Player player : list) {
            player.teleport(location);
            player.setHealth(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.getActivePotionEffects().clear();
        }
    }
    private void sendMessageToTeams(String message) {
        sendMessageToPlayers(team1.getPlayers(), message);
        sendMessageToPlayers(team2.getPlayers(), message);
    }
    private void sendMessageToPlayers(List<Player> list, String message) {
        for (Player player : list) {
            player.sendMessage(message);
        }
    }
    private void setKit(List<Player> list, Kit kit) {
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
        spectators.add(player);
        team1.getPlayers().remove(player);
        team2.getPlayers().remove(player);

        if (team1.getPlayers().isEmpty()) {
            endDuel(team2);
        } else if (team2.getPlayers().isEmpty()) {
            endDuel(team1);
        }
    }
    public void endDuel(Team winner) {
        state = DuelState.END;
        arena.setInUse(false);
        plugin.getDuelmanager().getTeams().remove(winner);
    
        List<Player> allPlayers = new ArrayList<>(winner.getPlayers());
        allPlayers.addAll(spectators);
        resetPlayers(allPlayers);

        Bukkit.broadcastMessage("El duelo ha terminado, el equipo " + winner.getName() + " ha ganado.");
        plugin.getDuelLogger().log("Duel between " + team1.getName() + " and " + team2.getName() + " ended. Winner: " + winner.getName());

        plugin.getDuelmanager().getDuels().remove(this);
    }
    
    private void teleportBack(Player player) {
        World world = Bukkit.getWorld("world");
        Location location = world.getSpawnLocation();

        try {
            player.teleport(plugin.getDuelmanager().getLastLocation().getOrDefault(player, location));
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
            teleportBack(player);
            player.setGameMode(GameMode.SURVIVAL);
            player.getActivePotionEffects().clear();
        });
    }
    
    public void checkOnPlayerQuit(Player p) {
        plugin.getDuelLogger().log(p.getName() + " quit the duel.");
        checkOnPlayerDeath(p);
    }
    
    public void rollBackPlayers() {
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(team1.getPlayers());
        allPlayers.addAll(team2.getPlayers());
        allPlayers.addAll(spectators);
    
        resetPlayers(allPlayers);
    }
  
}
