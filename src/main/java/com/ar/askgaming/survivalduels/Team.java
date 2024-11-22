package com.ar.askgaming.survivalduels;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class Team {

    List<Player> players = new ArrayList<>();

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }
    public Team(Player player) {
        addPlayer(player);
    }
    public Team(List<Player> players) {
        this.players = players;
    }
       

}
