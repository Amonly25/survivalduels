package com.ar.askgaming.survivalduels.Kits;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.ar.askgaming.survivalduels.SurvivalDuels;

public class KitManager {

    private File file;
    private FileConfiguration config;

    private SurvivalDuels plugin;
    public KitManager(SurvivalDuels plugin) {
        this.plugin = plugin;

        new Commands(plugin);

        file = new File(plugin.getDataFolder(), "kits.yml");
        if (!file.exists()) {
            plugin.saveResource("kits.yml", false);
        }
        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<String> keys = config.getKeys(false);

        for (String key : keys) {
            Object obj = config.get(key);
            if (obj instanceof Kit) {
                Kit kit = (Kit) obj;
                kits.add(kit);
            }
        }
    }
    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private List<Kit> kits = new ArrayList<>();

    public List<Kit> getKits() {
        return kits;
    }

    public Kit getKit(String name) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }
    public void createKit(String name) {
        Kit kit = new Kit(name);
        kits.add(kit);
        config.set(name, kit);
        save();
    }
    public void deleteKit(Kit kit) {
        kits.remove(kit);
        config.set(kit.getName(), null);
        save();
    }
    public Kit getRandomKit() {
        return kits.get((int) (Math.random() * kits.size()));
    }
    public void setKit(Player p, Kit kit) {
        kit.setArmor(p.getInventory().getArmorContents().clone());
        kit.setItems(p.getInventory().getContents().clone());
        p.sendMessage("Kit " + kit.getName() + " set");
        save();
    }

}
