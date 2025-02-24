package com.ar.askgaming.survivalduels.Duels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.ar.askgaming.survivalduels.SurvivalDuels;

public class QueueManager {

    private SurvivalDuels plugin;
    private List<Queue> queues = new ArrayList<>();

    public enum QueueType {
        SOLO,
        DUO,
        TRIO,
        SQUAD
    }

    public QueueManager(SurvivalDuels plugin) {
        this.plugin = plugin;

        Queue solo = new Queue(QueueType.SOLO);
        Queue duo = new Queue(QueueType.DUO);
        Queue trio = new Queue(QueueType.TRIO);
        Queue squad = new Queue(QueueType.SQUAD);

        queues.add(solo);
        queues.add(duo);
        queues.add(trio);
        queues.add(squad);
    }
    //#region checn
    public void checkQueueOnJoin(Queue queue) {
        Map<QueueType, Integer> requiredSizes = Map.of(
            QueueType.SOLO, 2,
            QueueType.DUO, 4,
            QueueType.TRIO, 6,
            QueueType.SQUAD, 8
        );

        int requiredSize = requiredSizes.getOrDefault(queue.getType(), -1);
        if (requiredSize != -1 && queue.getPlayers().size() == requiredSize) {
            createTeams(queue);
        }
    }
    //#region createTeams
    public void createTeams(Queue queue) {

        List<Player> players = queue.getPlayers();
        List<Player> team1 = new ArrayList<>();
        List<Player> team2 = new ArrayList<>();
    
        for (Player player : players) {
            player.sendMessage(plugin.getLangManager().getFrom("queue.found", player));
        }

        int teamSize = players.size() / 2;

        for (int i = 0; i < teamSize; i++) {
            team1.add(players.get(i)); // Agrega al primer equipo
            team2.add(players.get(i + teamSize)); // Agrega al segundo equipo
        }
        // Si el tamaño de la lista 'players' es impar, asignamos el jugador extra a uno de los equipos
        if (players.size() % 2 != 0) {
            team2.add(players.get(players.size() - 1)); // Agrega el jugador extra al segundo equipo
        }
    
        Team t1 = new Team(team1);
        t1.setPrefix("§bTeam 1");
        Team t2 = new Team(team2);
        t2.setPrefix("§cTeam 2");
        plugin.getDuelmanager().createDuel(t1, t2);

        queue.getPlayers().clear();

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
    public void addPlayerToQueue(Player p, QueueType type) {
        Queue queue = null;
        for (Queue q : queues) {
            if (q.getType() == type) {
                queue = q;
                break;
            }
        }
        if (queue == null) {
            return;
        }
        queue.addPlayer(p);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("survivalduels.use")) {
                player.sendMessage(plugin.getLangManager().getFrom("queue.enter", p).replace("{queue}", queue.getType().toString()).replace("{player}", p.getName()));
            }
        }

        checkQueueOnJoin(queue);
        
    }
}
