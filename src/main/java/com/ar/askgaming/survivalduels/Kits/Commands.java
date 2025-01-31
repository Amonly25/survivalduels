package com.ar.askgaming.survivalduels.Kits;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.ar.askgaming.survivalduels.SurvivalDuels;

public class Commands implements TabExecutor{

    private SurvivalDuels plugin;
    public Commands(SurvivalDuels plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginCommand("ckit").setExecutor(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("create", "delete", "list", "set");
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "delete":
                case "set":
                    return plugin.getKitmanager().getKits().stream().map(Kit::getName).toList();
                default:
                    break;
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0){
            sender.sendMessage("Use kit <create/delete/list/set>");
        }
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can execute this");
        }
        Player p = (Player) sender;
        switch (args[0].toLowerCase()) {
            case "create":
                createKit(p, args);
                break;
            case "delete":
                deleteKit(p, args);
                break;
            case "list":
                listKits(p);
                break;
            case "set":
                setKit(p, args);
                break;        
            default:
                sender.sendMessage("Use kit <create/delete/list/set>");
                break;
        }
        return false;
    }
    private void createKit(Player p, String[] args){
        if (args.length == 1){
            p.sendMessage("Use /kit create <name>");
            return;
        }
        String name = args[1];
        if (plugin.getKitmanager().getKit(name) != null){
            p.sendMessage("Kit already exists");
            return;
        }
        plugin.getKitmanager().createKit(name);
        p.sendMessage("Kit created");
    }
    private void deleteKit(Player p, String[] args){
        if (args.length == 1){
            p.sendMessage("Use /kit delete <name>");
            return;
        }
        String name = args[1];
        Kit kit = plugin.getKitmanager().getKit(name);
        if (kit == null){
            p.sendMessage("Kit not found");
            return;
        }
        plugin.getKitmanager().deleteKit(kit);
    }
    private void listKits(Player p){
        p.sendMessage("Kits:");
        for (Kit kit : plugin.getKitmanager().getKits()){
            p.sendMessage(kit.getName());
        }
    }
    private void setKit(Player p, String[] args){
        if (args.length < 2){
            p.sendMessage("Use /kit set <kit>");
            return;
        }
        String name = args[1];
        Kit kit = plugin.getKitmanager().getKit(name);
        if (kit == null){
            p.sendMessage("Kit not found");
            return;
        }

        plugin.getKitmanager().setKit(p, kit);
    }

}
