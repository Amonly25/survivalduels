package com.ar.askgaming.survivalduels.Arenas;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Arena implements ConfigurationSerializable{

    private String name;
    private int maxPlayers = 2;

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    private Location spawnsTeam1;
    private Location spawnsTeam2;

    public Location getSpawnsTeam1() {
        return spawnsTeam1;
    }

    public Location getSpawnsTeam2() {
        return spawnsTeam2;
    }

    public void setSpawnsTeam1(Location spawnsTeam1) {
        this.spawnsTeam1 = spawnsTeam1;
    }

    public void setSpawnsTeam2(Location spawnsTeam2) {
        this.spawnsTeam2 = spawnsTeam2;
    }

    public Arena(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public int getMaxPlayers() {
        return maxPlayers;
    }

    private boolean inUse;
    public boolean isInUse() {
        return inUse;
    }
    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }
    
    //#region Deserialization
    public Arena(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.maxPlayers = (int) map.get("maxPlayers");
        this.spawnsTeam1 = (Location) map.get("spawnsTeam1");
        this.spawnsTeam2 = (Location) map.get("spawnsTeam2");
    }
    //#region Serialization
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("maxPlayers", maxPlayers);
        map.put("spawnsTeam1", spawnsTeam1);
        map.put("spawnsTeam2", spawnsTeam2);
        return map;
    }
}
