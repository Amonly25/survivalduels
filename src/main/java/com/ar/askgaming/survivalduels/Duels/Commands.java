package com.ar.askgaming.survivalduels.Duels;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Duels.QueueManager.QueueType;
import com.ar.askgaming.survivalduels.Utils.Language;

public class Commands implements TabExecutor {

    private SurvivalDuels plugin;
    private Language lang;
    private DuelManager duelManager;
    public Commands(SurvivalDuels plugin, DuelManager duelManager) {
        this.plugin = plugin;
        this.duelManager = duelManager;
        this.lang = plugin.getLangManager();
        plugin.getServer().getPluginCommand("duels").setExecutor(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("queue");
            completions.add("leave");
            if (sender.hasPermission("duels.admin")) {
                completions.add("set_leaderboard");
            }
            return completions;

        }
        if (args.length == 2) {
            return List.of("solo", "duo", "trio", "squad","leave");
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
            case "leave":
                leaveDuel(p,args);
                break;
            case "set_leaderboard":
                setLeaderBoard(p);
                break;
            default:
                duelPlayer(p, args);
                break;
        }

        return true;
    }
    private void setLeaderBoard(Player p) {
        if (!p.hasPermission("duels.admin")) {
            p.sendMessage(lang.getFrom("commands.no_permission", p));
        }

        plugin.getLeaderBoard().createOrUpdateLeaderBoard(p.getLocation());
        p.sendMessage(lang.getFrom("commands.leaderboard_updated", p));
    }
    //#region queue
    public void queue(Player p, String[] args){
        if (args.length == 1) {
            p.sendMessage("Usage: /duel queue <type>");
            return;
        }
        if (args[1].equalsIgnoreCase("leave")) {
            plugin.getQueueManager().leaveQueue(p);
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

        plugin.getQueueManager().addPlayerToQueue(p, type);

    }
    //#region duelPlayer
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
        if (duelManager.getDuel(p) != null) {
            p.sendMessage(lang.getFrom("duel.player_in_duel", p));
            return;
        }
        if (plugin.getQueueManager().isInQueue(target) != null) {
            p.sendMessage(lang.getFrom("queue.in_queue", p));
            return;
        }

        duelManager.requestDuel(p, target);

    }
    //#region canQueueOrDuel
    private boolean canQueueOrDuel(Player p) {
        
        if (duelManager.getDuel(p) != null) {
            p.sendMessage(lang.getFrom("duel.in_duel", p));
            return false;
        }
        if (plugin.getQueueManager().isInQueue(p) != null) {
            p.sendMessage(lang.getFrom("queue.already_in", p));
            return false;
        }
        return true;
    }
    //#region leave
    public void leaveDuel(Player p, String[] args){
        Duel duel = duelManager.getDuel(p);
        if (duelManager == null) {
            p.sendMessage(lang.getFrom("duel.not_in_duel", p));
            return;
        }
        plugin.getDuelLogger().log("Player " + p.getName() + " leave the duel.");
        duel.eliminatePlayer(p);
    }
}
