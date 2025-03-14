package com.ar.askgaming.survivalduels.Arenas;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.ar.askgaming.survivalduels.SurvivalDuels;

public class Commands implements TabExecutor {

    private SurvivalDuels plugin;
    public Commands(SurvivalDuels plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginCommand("arenas").setExecutor(this);
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("list", "create", "delete", "set");
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "delete":
                case "set":
                return plugin.getArenamanager().getArenas().stream().map(Arena::getName).toList();
                default:
                    break;
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                return List.of("maxPlayers", "loc1", "loc2");
            }
        }
        return null;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores pueden ejecutar este comando");
            return true;
        }
        Player player = (Player) sender;

        switch (args[0].toLowerCase()) {
            case "create":
                createArena(player, args);             
                return true;
            case "delete":
                deleteArena(player, args);
                return true;
            case "list":
                listArenas(player);
                return true;
            case "set":
                setArena(player, args);
                return true;
            default:
                player.sendMessage("Command not found");
                return true;
        }

    }

    private void createArena(Player p, String[] args) {
        if (args.length < 2) {
            p.sendMessage("Use: /arenas create <name>");
            return;
        }
        String name = args[1];
        Arena arena = plugin.getArenamanager().getByName(name);
        if (arena != null) {
            p.sendMessage("The arene with that name already exists");
            return;
        }
        p.sendMessage("Arena created successfully, you can now configure it with /arenas set <arena> <maxPlayers|loc1|loc2>");
        plugin.getArenamanager().createArena(name);
    }
    private void deleteArena(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Use: /arenas delete <name>");
            return;
        }
        String name = args[1];
        Arena arena = plugin.getArenamanager().getByName(name);
        if (arena == null) {
            player.sendMessage("This arena does not exist");
            return;
        }
        plugin.getArenamanager().deleteArena(arena);
        player.sendMessage("Arena deleted successfully");
    }
    private void listArenas(Player player) {
        player.sendMessage("Arenas:");
        for (Arena arena : plugin.getArenamanager().getArenas()) {
            player.sendMessage(arena.getName());
        }
    }
    private void setArena(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Use: /arenas set <arena> <maxPlayers>");
            return;
        }
        String name = args[1];
        Arena arena = plugin.getArenamanager().getByName(name);
        if (arena == null) {
            player.sendMessage("This arena does not exist");
            return;
        }
        Location loc = player.getLocation();
        switch (args[2].toLowerCase()) {
            case "maxplayers":
                int maxPlayers;
                try {
                    maxPlayers = Integer.parseInt(args[3]);

                } catch (Exception e) {
                    player.sendMessage("Invalid number");
                    return;
                }
                arena.setMaxPlayers(maxPlayers);
                player.sendMessage("Max players configured correctly to " + maxPlayers);
                plugin.getArenamanager().save();
                break;
            case "loc1":
                arena.setSpawnsTeam1(loc);
                player.sendMessage("Spawn 1 configured correctly");
                plugin.getArenamanager().save();
                break;
            case "loc2":
                arena.setSpawnsTeam2(loc);
                player.sendMessage("Spawn 2 configured correctly");
                plugin.getArenamanager().save();
                break;
            default:
                break;
        }
    }

}
