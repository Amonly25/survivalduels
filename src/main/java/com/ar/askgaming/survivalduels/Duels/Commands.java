package com.ar.askgaming.survivalduels.Duels;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Duels.Queue.QueueType;

public class Commands implements TabExecutor {

    private SurvivalDuels plugin;
    public Commands(SurvivalDuels plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginCommand("duels").setExecutor(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) {
            return List.of("queue", "leave");
        }
       
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        Player p = (Player) sender;

        switch (args[0].toLowerCase()) {
            case "queue":
                queue(p, args);
                break;
        
            default:
                break;
        }

        return true;
    }
    public void queue(Player p, String[] args){
        if (args.length == 1) {
            p.sendMessage("Usage: /duel queue <type>");
            return;
        }
        if (args[1].equalsIgnoreCase("leave")) {
            plugin.getDuelmanager().leaveQueue(p);
            p.sendMessage("You have left all queues");
            return;
        }
        if (plugin.getDuelmanager().isInQueue(p) != null) {
            p.sendMessage("You are already in a queue");
            return;
        }

        QueueType type = null;
        try {
            type = QueueType.valueOf(args[1].toUpperCase());
        } catch (Exception e) {
            p.sendMessage("Invalid queue type");
        }
        if (type == null) {
            return;
        }

        for (Queue queue : plugin.getDuelmanager().getQueues()) {
            if (queue.getType() == type) {
                p.sendMessage("You have been added to the " + type.toString().toLowerCase() + " queue");
                queue.addPlayer(p);
                return;
            }
        }
    }
}
