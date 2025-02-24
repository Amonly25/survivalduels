package com.ar.askgaming.survivalduels.Duels;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.ar.askgaming.survivalduels.Duels.QueueManager.QueueType;

public class Queue {

    private QueueType type;
    private List<Player> players;

    public Queue(QueueType type) {
        this.type = type;
        
        players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public QueueType getType() {
        return type;
    }

    public void setType(QueueType type) {
        this.type = type;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
