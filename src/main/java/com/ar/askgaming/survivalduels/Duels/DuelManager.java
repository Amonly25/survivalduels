package com.ar.askgaming.survivalduels.Duels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ar.askgaming.survivalduels.SurvivalDuels;
import com.ar.askgaming.survivalduels.Arenas.Arena;
import com.ar.askgaming.survivalduels.Kits.Kit;
import com.ar.askgaming.survivalduels.Utils.Language;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class DuelManager {
    
    private SurvivalDuels plugin;
    private Language lang;
    private List<Duel> duels = new ArrayList<>();
    private List<Team> teams = new ArrayList<>();

    private HashMap<UUID, Location> lastLocation = new HashMap<>();
    private HashMap<UUID, ItemStack[]> lastInventory = new HashMap<>();
    private HashMap<Player, Player> requests = new HashMap<>();

    public enum DuelState {
        COUNTDOWN,
        INGAME,
        END
    }
    public enum MessageType {
        START,
        END,
        PREPARE,
        ERROR_TP
    }

    public DuelManager(SurvivalDuels plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLangManager();

        new Commands(plugin, this);
    }

    //#region createDuel
    public void createDuel(Team team1, Team team2) {
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
            lastLocation.put(player.getUniqueId(), player.getLocation());
            lastInventory.put(player.getUniqueId(), player.getInventory().getContents());

        }
        for (Player player : team2.getDuelPlayers()) {
            lastLocation.put(player.getUniqueId(), player.getLocation());
            lastInventory.put(player.getUniqueId(), player.getInventory().getContents());
        }
        Kit kit = plugin.getKitmanager().getRandomKit();
        Duel duel = new Duel(team1, team2, arena, kit);
        duels.add(duel);

        for (Player pl : Bukkit.getOnlinePlayers()){
            if (pl.hasPermission("survivalduels.use")) {
                pl.sendMessage(plugin.getLangManager().getFrom("duel.created", pl).replace("{team1}",
                 team1.getName()).replace("{team2}", team2.getName()));
            }
        };
    }

    //#region rollback
    public void onShutdown() {
        HashMap<UUID, Location> lastLocMap = new HashMap<>(this.lastLocation);
        HashMap<UUID, ItemStack[]> lastInvMap = new HashMap<>(this.lastInventory);

        lastLocMap.forEach((player, location) -> {
            Player p = Bukkit.getPlayer(player);
            if (p != null) {
                teleportBack(p);
            } else {
                plugin.getDuelLogger().log("Player " + player + " is offline, can't teleport back to spawn.");
            }
        });
        lastInvMap.forEach((player, inventory) -> {
            Player p = Bukkit.getPlayer(player);
            if (p != null) {
                giveInventoryBack(p);
            } else {
                plugin.getDuelLogger().log("Player " + player + " is offline, can't restore inventory.");
                plugin.getDuelLogger().log("Inventory: " + inventory.toString());
            }
        });
        
    }
    public void teleportBack(Player p) {
        UUID uuid = p.getUniqueId();
        if (lastLocation.containsKey(uuid)) {
            try {
                boolean tp = p.teleport(lastLocation.get(uuid));
                if (!tp) {
                    plugin.getDuelLogger().log("Error while teleporting player back.");
                } else {
                    plugin.getDuelLogger().log("Player " + p.getName() + " teleported back to spawn.");
                }
                lastLocation.remove(uuid);
            } catch (Exception e) {
                plugin.getDuelLogger().log("Error while teleporting player " + p.getName() + " back to spawn." + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    public void giveInventoryBack(Player p) {
        UUID uuid = p.getUniqueId();
        if (lastInventory.containsKey(uuid)) {
            try {

                ItemStack[] items = plugin.getDuelmanager().getLastInventory().get(uuid);
                if (items != null) {
                    p.getInventory().setContents(lastInventory.get(uuid));
                    plugin.getDuelLogger().log("Player " + p.getName() + " inventory restored.");
    
                }
                lastInventory.remove(uuid);
            } catch (Exception e) {
                plugin.getDuelLogger().log("Error while restoring player " + p.getName() + " inventory." + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void checkRollBack(Player p) {
        p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(p.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        p.setFoodLevel(20);
        p.getActivePotionEffects().clear();
        p.setAbsorptionAmount(0);
        p.setFireTicks(0);
        p.setFallDistance(0);
        p.setSaturation(20);    

        teleportBack(p);
        giveInventoryBack(p);
    }

    //#region request
    public void requestDuel(Player p, Player target) {
        if (requests.containsKey(p) && requests.get(p) == target) {
            p.sendMessage(lang.getFrom("duel.already_send", p));
            return;
        }
        if (requests.containsKey(target) && requests.get(target) == p) {
            Team team1 = new Team(p);
            team1.setPrefix("§bTeam 1");
            Team team2 = new Team(target);
            team2.setPrefix("§cTeam 2");
            createDuel(team1, team2);
            requests.remove(p);
            requests.remove(target);
            return;
        }
        requests.put(p, target);
        p.sendMessage(lang.getFrom("duel.request", p).replace("{player}", target.getName()));
        sendDuelMessage(p, target);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (requests.containsKey(p) && requests.get(p) == target) {
                requests.remove(p);
                requests.remove(target);
                p.sendMessage(lang.getFrom("duel.expired", p).replace("{player}", target.getName()));
                target.sendMessage(lang.getFrom("duel.expired", p).replace("{player}", p.getName()));
            }
        }, 20 * 60);
    }

    private void sendDuelMessage(Player sender, Player target){
        TextComponent message = new TextComponent(lang.getFrom("duel.received", target).replace("{player}", sender.getName()));

        String click = lang.getFrom("misc.click_to_accept", target);

        TextComponent clickableText = new TextComponent(click);
      
        clickableText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel " + sender.getName()));

        clickableText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(click)));

        message.addExtra(clickableText);

        target.spigot().sendMessage(message);

    }
    //#region stats
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
        plugin.getLeaderBoard().updateText();
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
    //#region elo
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
    //#region getters
    public HashMap<UUID, ItemStack[]> getLastInventory() {
        return lastInventory;
    }

    public HashMap<UUID, Location> getLastLocation() {
        return lastLocation;
    }

    public List<Duel> getDuels() {
        return duels;
    }
    public List<Team> getTeams() {
        return teams;
    }
    public boolean isInDuel(Player player) {
        return getDuel(player) != null;
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
}
