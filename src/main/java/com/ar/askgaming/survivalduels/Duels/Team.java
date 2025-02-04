package com.ar.askgaming.survivalduels.Duels;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Team {

    private List<Player> duelPlayers = new ArrayList<>();
    private List<Player> alivePlayers = new ArrayList<>();


    public List<Player> getDuelPlayers() {
        return duelPlayers;
    }
    public List<Player> getAlivePlayers() {
        return alivePlayers;
    }
    public Team(Player player) {
        duelPlayers.add(player);
        alivePlayers.add(player);

        setName();
    }
    public Team(List<Player> players) {
        duelPlayers.addAll(players);
        alivePlayers.addAll(players);

        setName();
    }
    private String name = "";

    public String getName() {
        return name;
    }


    private void setName() {
        String split = "";
        for (OfflinePlayer player : duelPlayers) {
            split += player.getName() + ", ";
        }
        name = split.substring(0, split.length() - 2);
    }
       

}
