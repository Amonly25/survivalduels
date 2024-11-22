package com.ar.askgaming.survivalduels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Arena implements ConfigurationSerializable{

    private boolean enabled;
    private String name;
    private int maxPlayers;

    private List<Location> spawnsTeam1 = new ArrayList<>();
    private List<Location> spawnsTeam2 = new ArrayList<>();

    public Arena(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public String getName() {
        return name;
    }
    public int getMaxPlayers() {
        return maxPlayers;
    }
    public int getMinPlayers() {
        if (spawnsTeam1.size() == spawnsTeam2.size()) {
            return spawnsTeam2.size();
        }
        return 0;
    }
    private boolean inUse;
    public boolean isInUse() {
        return inUse;
    }
    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }
    public List<Location> getSpawnsTeam1() {
        return spawnsTeam1;
    }

    public List<Location> getSpawnsTeam2() {
        return spawnsTeam2;
    }
    
    //#region Deserialization
    public Arena(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.enabled = (boolean) map.get("enabled");
        this.maxPlayers = (int) map.get("maxPlayers");
        this.spawnsTeam1 = (List<Location>) map.get("spawnsTeam1");
        this.spawnsTeam2 = (List<Location>) map.get("spawnsTeam2");
    }
    //#region Serialization
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("enabled", enabled);
        map.put("maxPlayers", maxPlayers);
        map.put("spawnsTeam1", spawnsTeam1);
        map.put("spawnsTeam2", spawnsTeam2);
        return map;
    }
}
