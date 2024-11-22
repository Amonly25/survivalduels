package com.ar.askgaming.survivalduels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

public class Queue {

    private SurvivalDuels plugin = SurvivalDuels.getPlugin(SurvivalDuels.class);

    public enum QueueType {
        SOLO,
        DUO,
        TRIO,
        SQUAD
    }

    public Queue(QueueType type) {
        this.type = type;
        
        players = new ArrayList<>();
    }

    private QueueType type;

    public QueueType getType() {
        return type;
    }

    public void setType(QueueType type) {
        this.type = type;
    }

    private List<Player> players;

    public List<Player> getPlayers() {
        return players;
    }
    public void addPlayer(Player player) {
        players.add(player);
        checkQueue();
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void checkQueue() {
        Map<QueueType, Integer> requiredSizes = Map.of(
            QueueType.SOLO, 2,
            QueueType.DUO, 4,
            QueueType.TRIO, 6,
            QueueType.SQUAD, 8
        );

        int requiredSize = requiredSizes.getOrDefault(getType(), -1);
        if (requiredSize != -1 && getPlayers().size() == requiredSize) {
            plugin.getDuelmanager().createDuel(this);
        }
    }
}
