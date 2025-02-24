package com.ar.askgaming.survivalduels.Utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Duels.Team;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceHolders extends PlaceholderExpansion{

    private SurvivalDuels plugin;
    public PlaceHolders(SurvivalDuels main){
        plugin = main;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        FileConfiguration config = plugin.getPlayerData().getConfig();
        String uuid = player.getUniqueId().toString();
        switch (params) {
            case "wins":
                return String.valueOf(config.getInt(uuid + ".wins",0));
            case "losses":
                return String.valueOf(config.getInt(uuid + ".losses",0));       
            case "elo":
                return String.valueOf(config.getInt(uuid + ".elo",1000));
            case "team":
                Team team = plugin.getDuelmanager().isInTeam(player.getPlayer());
                if (team != null) {
                    String prefix = team.getPrefix();
                    return prefix;
                }
                return "";
            default:
                return "Invalid Placeholder";
        }
    }

    @Override
    public String getAuthor() {
        return "AskGaming";
    }

    @Override
    public String getIdentifier() {
        return "duels";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

}
