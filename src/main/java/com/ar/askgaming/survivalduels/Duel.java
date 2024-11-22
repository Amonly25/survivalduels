package com.ar.askgaming.survivalduels;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Duel {

    private SurvivalDuels plugin = SurvivalDuels.getPlugin(SurvivalDuels.class);

    private Team team1;
    private Team team2;
    private Arena arena;
    public enum DuelState {
        COUNTDOWN,
        INGAME,
        END
    }

    private int countdown = 10;
    private DuelState state;

    public Duel(Team team1, Team team2, Arena arena) {
        this.team1 = team1; 
        this.team2 = team2;
        this.arena = arena;
        this.state = DuelState.COUNTDOWN;

        for (int i = 0; i < team1.getPlayers().size(); i++) {
            team1.getPlayers().get(i).teleport(arena.getSpawnsTeam1().get(i));
            team1.getPlayers().get(i).sendMessage("El duelo comenzara en 10 segundos");   
        }
        for (int i = 0; i < team2.getPlayers().size(); i++) {
            team2.getPlayers().get(i).teleport(arena.getSpawnsTeam2().get(i));
            team2.getPlayers().get(i).sendMessage("El duelo comenzara en 10 segundos");
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                state = DuelState.INGAME;
                for (int i = 0; i < team1.getPlayers().size(); i++) {
                    team1.getPlayers().get(i).sendMessage("El duelo ha comenzado");
                }
                for (int i = 0; i < team2.getPlayers().size(); i++) {
                    team2.getPlayers().get(i).sendMessage("El duelo ha comenzado");
                }
            }
        }, countdown * 20);
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
        if (team1.getPlayers().contains(player)) {
            team1.getPlayers().remove(player);
        }
        if (team2.getPlayers().contains(player)) {
            team2.getPlayers().remove(player);
        }
        if (team1.getPlayers().isEmpty()) {
            team1 = null;
            plugin.getDuelmanager().getTeams().remove(team1);
            endDuel(team2);
        }
        if (team2.getPlayers().isEmpty()) {
            team2 = null;
            plugin.getDuelmanager().getTeams().remove(team2);
            endDuel(team1);

        }
    }
    public void endDuel(Team winner) {
        state = DuelState.END;
        arena.setInUse(false);
        plugin.getDuelmanager().getTeams().remove(winner);

        List<Player> players = winner.getPlayers();

        for (Player player : players) {
            player.sendMessage("Has ganado el duelo");
        }
        winner = null;
        for (Duel duel : plugin.getDuelmanager().getDuels()) {
            if (duel == this) {
                plugin.getDuelmanager().getDuels().remove(duel);
            }
        }
        for (Player player : team1.getPlayers()) {
            //Teleport to last location or spawn if not found
        }
        
    }


}
