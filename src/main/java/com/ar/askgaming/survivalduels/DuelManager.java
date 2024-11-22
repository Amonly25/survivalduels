package com.ar.askgaming.survivalduels;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class DuelManager {
    
    private SurvivalDuels plugin;
    public DuelManager(SurvivalDuels plugin) {
        this.plugin = plugin;

        Queue solo = new Queue(Queue.QueueType.SOLO);
        Queue duo = new Queue(Queue.QueueType.DUO);
        Queue trio = new Queue(Queue.QueueType.TRIO);
        Queue squad = new Queue(Queue.QueueType.SQUAD);

        queues.add(solo);
        queues.add(duo);
        queues.add(trio);
        queues.add(squad);

    }
    private List<Queue> queues = new ArrayList<>();
    private List<Duel> duels = new ArrayList<>();
    private List<Team> teams = new ArrayList<>();

    public void createDuel(Queue queue) {

        List<Player> players = queue.getPlayers();
        List<Player> team1 = new ArrayList<>();
        List<Player> team2 = new ArrayList<>();
    
        for (Player player : players) {
           player.sendMessage("Jugadores encontrados, creando duelo...");
        }

        int teamSize = 0;
        switch (queue.getType()) {
            case SOLO:
                teamSize = 1;
                break;
            case DUO:
                teamSize = 2;
                break;
            case TRIO:
                teamSize = 3;
                break;
            case SQUAD:
                teamSize = 4;
                break;
            default:
                return;
        }
    
        for (int i = 0; i < teamSize; i++) {
            team1.add(players.get(i));
            team2.add(players.get(i + teamSize));
        }
    
        Team t1 = new Team(team1);
        Team t2 = new Team(team2);
        teams.add(t1);
        teams.add(t2);

        Arena arena = plugin.getArenamanager().getArenaAvailable(teamSize);
        if (arena == null) {
            for (Player player : players) {
                player.sendMessage("No hay arenas disponibles, por favor espere...");
            }
            //Handle no arena available
            //Prepare and wait for an arena to be available.
            return;
        }
        arena.setInUse(true);
        for (Player player : players) {
            player.sendMessage("Duelo creado, teletransportando...");
        }
        queue.getPlayers().clear();

        Duel duel = new Duel(t1, t2, arena);
        duels.add(duel);

    }
    public Duel isInDuel(Player player) {
        for (Duel duel : duels) {
            if (duel.getTeam1().getPlayers().contains(player) || duel.getTeam2().getPlayers().contains(player)) {
                return duel;
            }
        }
        return null;
    }
    public Duel getDuel(Player player) {
        for (Duel duel : duels) {
            if (duel.getTeam1().getPlayers().contains(player) || duel.getTeam2().getPlayers().contains(player)) {
                return duel;
            }
        }
        return null;
    }
    public Team isInTeam(Player player) {
        for (Team team : teams) {
            if (team.getPlayers().contains(player)) {
                return team;
            }
        }
        return null;
    }
    public Queue isInQueue(Player player) {
        for (Queue queue : queues) {
            if (queue.getPlayers().contains(player)) {
                return queue;
            }
        }
        return null;
    }
    public void leaveQueue(Player player) {
        Queue queue = isInQueue(player);
        if (queue != null) {
            queue.removePlayer(player);
        }
    }
    public List<Queue> getQueues() {
        return queues;
    }
    public List<Duel> getDuels() {
        return duels;
    }
    public List<Team> getTeams() {
        return teams;
    }
}
