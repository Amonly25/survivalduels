package com.ar.askgaming.survivalduels.Duels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Arenas.Arena;
import com.ar.askgaming.survivalduels.Kits.Kit;
import com.ar.askgaming.survivalduels.Utils.Language;

public class DuelManager {
    
    private SurvivalDuels plugin;
    private Language lang;
    public DuelManager(SurvivalDuels plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLangManager();
        new Commands(plugin);

        Queue solo = new Queue(Queue.QueueType.SOLO);
        Queue duo = new Queue(Queue.QueueType.DUO);
        Queue trio = new Queue(Queue.QueueType.TRIO);
        Queue squad = new Queue(Queue.QueueType.SQUAD);

        queues.add(solo);
        queues.add(duo);
        queues.add(trio);
        queues.add(squad);

    }
    private List<Queue> queues = new ArrayList<>();
    private List<Duel> duels = new ArrayList<>();
    private List<Team> teams = new ArrayList<>();

    private HashMap<Player, Location> lastLocation = new HashMap<>();
    private HashMap<Player, ItemStack[]> lastInventory = new HashMap<>();

    public HashMap<Player, ItemStack[]> getLastInventory() {
        return lastInventory;
    }
    public void setLastInventory(HashMap<Player, ItemStack[]> lastInventory) {
        this.lastInventory = lastInventory;
    }
    public HashMap<Player, Location> getLastLocation() {
        return lastLocation;
    }
    public void setLastLocation(HashMap<Player, Location> lastLocation) {
        this.lastLocation = lastLocation;
    }
    public void createDuel(Queue queue) {

        List<Player> players = queue.getPlayers();
        List<Player> team1 = new ArrayList<>();
        List<Player> team2 = new ArrayList<>();
    
        for (Player player : players) {
           player.sendMessage(lang.getFrom("queue.found", player));
        }

        int teamSize = 0;
        switch (queue.getType()) {
            case SOLO:
                teamSize = 1;
                break;
            case DUO:
                teamSize = 2;
                break;
            case TRIO:
                teamSize = 3;
                break;
            case SQUAD:
                teamSize = 4;
                break;
            default:
                return;
        }
    
        for (int i = 0; i < teamSize; i++) {
            team1.add(players.get(i));
            team2.add(players.get(i + teamSize));
        }
    
        Team t1 = new Team(team1);
        Team t2 = new Team(team2);

        createDuel(t1, t2);

        queue.getPlayers().clear();

    }
    private void createDuel(Team team1, Team team2) {
        Arena arena = plugin.getArenamanager().getArenaAvailable(2);
        if (arena == null) {
            for (Player player : team1.getDuelPlayers()) {
                player.sendMessage(lang.getFrom("arena.not_found", player));
           
            }
            for (Player player : team2.getDuelPlayers()) {
                player.sendMessage(lang.getFrom("arena.not_found", player));
            }
            return;
        }
        teams.add(team1);
        teams.add(team2);

        arena.setInUse(true);
        for (Player player : team1.getDuelPlayers()) {
            lastLocation.put(player, player.getLocation());
            lastInventory.put(player, player.getInventory().getContents());
        }
        for (Player player : team2.getDuelPlayers()) {
            lastLocation.put(player, player.getLocation());
            lastInventory.put(player, player.getInventory().getContents());
        }
        Kit kit = plugin.getKitmanager().getRandomKit();
        Duel duel = new Duel(team1, team2, arena, kit);
        duels.add(duel);
    }

    public Duel isInDuel(Player player) {
        for (Duel duel : duels) {
            if (duel.getTeam1().getDuelPlayers().contains(player) || duel.getTeam2().getDuelPlayers().contains(player)) {
                return duel;
            }
        }
        return null;
    }
    public Duel getDuel(Player player) {
        for (Duel duel : duels) {
            if (duel.getTeam1().getDuelPlayers().contains(player) || duel.getTeam2().getDuelPlayers().contains(player)) {
                return duel;
            }
        }
        return null;
    }
    public Team isInTeam(Player player) {
        for (Team team : teams) {
            if (team.getDuelPlayers().contains(player)) {
                return team;
            }
        }
        return null;
    }
    public Queue isInQueue(Player player) {
        for (Queue queue : queues) {
            if (queue.getPlayers().contains(player)) {
                return queue;
            }
        }
        return null;
    }
    public void leaveQueue(Player player) {
        Queue queue = isInQueue(player);
        if (queue != null) {
            queue.removePlayer(player);
        }
    }
    public List<Queue> getQueues() {
        return queues;
    }
    public List<Duel> getDuels() {
        return duels;
    }
    public List<Team> getTeams() {
        return teams;
    }
    public void onShutdown() {
        for (Player player : lastLocation.keySet()) {
            checkRollBack(player);
        }
 
    }
    public void checkRollBack(Player p) {
        try {
            if (lastLocation.containsKey(p)) {
                p.teleport(lastLocation.get(p));
                p.getInventory().setContents(lastInventory.get(p));
                lastLocation.remove(p);
                lastInventory.remove(p);
                plugin.getDuelLogger().log("Player " + p.getName() + " teleported back to spawn and inventory restored.");
            }
        } catch (IllegalArgumentException e) {
            plugin.getDuelLogger().log("Error while teleporting player " + p.getName() + " back to spawn, inventory contents: " + lastInventory.get(p).toString());
            e.printStackTrace();
        }
    }

    private HashMap<Player, Player> requests = new HashMap<>();

    public void requestDuel(Player p, Player target) {
        if (requests.containsKey(p) && requests.get(p) == target) {
            p.sendMessage(lang.getFrom("duel.already_send", p));
            return;
        }
        if (requests.containsKey(target) && requests.get(target) == p) {
            Team team1 = new Team(p);
            Team team2 = new Team(target);
            createDuel(team1, team2);
            requests.remove(p);
            requests.remove(target);
            return;
        }
        requests.put(p, target);
        p.sendMessage(lang.getFrom("duel.request", p).replace("{player}", target.getName()));
        target.sendMessage(lang.getFrom("duel.receive", p).replace("{player}", p.getName()));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (requests.containsKey(p) && requests.get(p) == target) {
                requests.remove(p);
                requests.remove(target);
                p.sendMessage(lang.getFrom("duel.expired", p).replace("{player}", target.getName()));
                target.sendMessage(lang.getFrom("duel.expired", p).replace("{player}", p.getName()));
            }
        }, 20 * 60);
    }
    public void modifyStats(Team winner, Team losser) {
        int minSize = Math.min(winner.getDuelPlayers().size(), losser.getDuelPlayers().size());
    
        for (int i = 0; i < minSize; i++) {
            adjustElo(winner.getDuelPlayers().get(i), losser.getDuelPlayers().get(i));
        }
    
        for (Player player : winner.getDuelPlayers()) {
            addWin(player);
        }
        for (Player player : losser.getDuelPlayers()) {
            addLoss(player);
        }
    
        // Guardar solo una vez al final
        plugin.getPlayerData().save();
    }
    
    private void addWin(OfflinePlayer player) {
        FileConfiguration config = plugin.getPlayerData().getConfig();
        String path = player.getUniqueId().toString() + ".wins";
        config.set(path, config.getInt(path, 0) + 1);
    }
    
    private void addLoss(OfflinePlayer player) {
        FileConfiguration config = plugin.getPlayerData().getConfig();
        String path = player.getUniqueId().toString() + ".losses";
        config.set(path, config.getInt(path, 0) + 1);
    }
    
    public void setElo(OfflinePlayer player, int newElo) {
        plugin.getPlayerData().getConfig().set(player.getUniqueId().toString() + ".elo", newElo);
    }
    
    public void adjustElo(OfflinePlayer winner, OfflinePlayer loser) {
        FileConfiguration config = plugin.getPlayerData().getConfig();
    
        int winnerElo = config.getInt(winner.getUniqueId().toString() + ".elo", 1000);
        int loserElo = config.getInt(loser.getUniqueId().toString() + ".elo", 1000);
        int k = 32; // Factor de ajuste
    
        double expectedWinner = 1 / (1 + Math.pow(10, (loserElo - winnerElo) / 400.0));
        double expectedLoser = 1 / (1 + Math.pow(10, (winnerElo - loserElo) / 400.0));
    
        int newWinnerElo = (int) (winnerElo + k * (1 - expectedWinner));
        int newLoserElo = (int) (loserElo + k * (0 - expectedLoser));
    
        setElo(winner, newWinnerElo);
        setElo(loser, newLoserElo);
    
        Player winnerOnline = winner.getPlayer();
        Player loserOnline = loser.getPlayer();
    
        if (winnerOnline != null) {
            winnerOnline.sendMessage(lang.getFrom("duel.won", loserOnline).replace("{elo}", newWinnerElo + ""));
            winnerOnline.playSound(winnerOnline.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        }
        if (loserOnline != null) {
            loserOnline.sendMessage(lang.getFrom("duel.lose", winnerOnline).replace("{elo}", newLoserElo + ""));
        }
    }
}
