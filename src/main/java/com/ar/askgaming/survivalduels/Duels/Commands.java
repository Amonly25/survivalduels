package com.ar.askgaming.survivalduels.Duels;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Duels.Queue.QueueType;
import com.ar.askgaming.survivalduels.Utils.Language;

public class Commands implements TabExecutor {

    private SurvivalDuels plugin;
    private Language lang;
    public Commands(SurvivalDuels plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLangManager();
        plugin.getServer().getPluginCommand("duels").setExecutor(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) {
            return List.of("queue", "leave");
        }
        if (args.length == 2) {
            return List.of("solo", "duo", "trio", "squad");
        }
       
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage("Usage: /duels <queue|leave> [type]");
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
                duelPlayer(p, args);
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
            p.sendMessage(lang.getFrom("queue.leave", p));
            return;
        }
        if (!canQueueOrDuel(p)) {
            return;
        }

        QueueType type = null;
        try {
            type = QueueType.valueOf(args[1].toUpperCase());
        } catch (Exception e) {
            p.sendMessage(lang.getFrom("queue.invalid", p));
            return;
        }

        for (Queue queue : plugin.getDuelmanager().getQueues()) {
            if (queue.getType() == type) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(lang.getFrom("queue.enter", p).replace("{queue}", queue.getType().toString()));
                }
                queue.addPlayer(p);
                return;
            }
        }
    }
    private void duelPlayer(Player p, String[] args) {
        if (!canQueueOrDuel(p)) {
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage(lang.getFrom("commands.player_not_found", p));
            return;
        }
        if (target == p) {
            p.sendMessage(lang.getFrom("commands.cant_duel_yourself", p));
            return;
        }
        if (plugin.getDuelmanager().isInDuel(target) != null) {
            p.sendMessage(lang.getFrom("duel.player_in_duel", p));
            return;
        }
        if (plugin.getDuelmanager().isInQueue(target) != null) {
            p.sendMessage(lang.getFrom("queue.in_queue", p));
            return;
        }

        plugin.getDuelmanager().requestDuel(p, target);

    }
    private boolean canQueueOrDuel(Player p) {
        
        if (plugin.getDuelmanager().isInDuel(p) != null) {
            p.sendMessage(lang.getFrom("duel.in_duel", p));
            return true;
        }
        if (plugin.getDuelmanager().isInQueue(p) != null) {
            p.sendMessage(lang.getFrom("queue.already_in", p));
            return false;
        }
        return false;
    }
}
