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

        plugin.getServer().getPluginCommand("dkit").setExecutor(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("create", "delete", "list", "set", "get");
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "delete":
                case "get":
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

        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can execute this");
        }
        Player p = (Player) sender;

        if (args.length == 0){
            sender.sendMessage("Use kit <create/delete/list/set>");
        }

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
            case "get":
                getKit(p, args);
                break;
            default:
                sender.sendMessage("Use kit <create/delete/list/set>");
                break;
        }
        return true;
    }
    private void createKit(Player p, String[] args){
        if (args.length < 2) {
            p.sendMessage("Uso: /dkit create <nombre>");
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
        if (args.length < 2) {
            p.sendMessage("Uso: /dkit delete <nombre>");
            return;
        }
        String name = args[1];
        Kit kit = plugin.getKitmanager().getKit(name);
        if (kit == null){
            p.sendMessage("Kit not found");
            return;
        }
        plugin.getKitmanager().deleteKit(kit);
        p.sendMessage("Kit deleted");
    }
    private void listKits(Player p) {
        List<Kit> kits = plugin.getKitmanager().getKits();
        if (kits.isEmpty()) {
            p.sendMessage("No hay kits disponibles.");
            return;
        }

        p.sendMessage("Kits disponibles:");
        for (Kit kit : kits) {
            p.sendMessage("- " + kit.getName());
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
        p.sendMessage("Kit " + kit.getName() + " set.");
    }
    private void getKit(Player p, String[] args){
        if (args.length < 2){
            p.sendMessage("Use /kit get <kit>");
            return;
        }
        String name = args[1];
        Kit kit = plugin.getKitmanager().getKit(name);
        if (kit == null){
            p.sendMessage("Kit not found");
            return;
        }

        p.getInventory().setContents(kit.getItems());
        p.getInventory().setArmorContents(kit.getArmor());
        p.sendMessage("Kit " + kit.getName() + " loaded.");
    }

}
