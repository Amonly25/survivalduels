package com.ar.askgaming.survivalduels.Kits;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ar.askgaming.survivalduels.SurvivalDuels;

import net.md_5.bungee.api.chat.hover.content.Item;

public class KitManager {

    private final File file;
    private final FileConfiguration config;
    private final SurvivalDuels plugin;
    private final List<Kit> kits = new ArrayList<>();

    public KitManager(SurvivalDuels plugin) {
        this.plugin = plugin;

        new Commands(plugin);

        file = new File(plugin.getDataFolder(), "kits.yml");

        if (!file.exists()) {
            plugin.saveResource("kits.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);
        loadKits();
    }
    public void loadKits(){
        kits.clear(); // Limpiar la lista antes de cargar
        Set<String> keys = config.getKeys(false);

        for (String key : keys) {
            Object obj = config.get(key);
            if (obj instanceof Kit) { // Bukkit guarda objetos serializados como Map<String, Object>
                Kit kit = (Kit) obj;
                kits.add(kit);
            }
        }
    }
    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Error al guardar kits.yml");
            e.printStackTrace();
        }
    }

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
        if (kits.isEmpty()) {
            return null;
        }
        return kits.get(new Random().nextInt(kits.size()));
    }
    public void setKit(Player p, Kit kit) {
        ItemStack[] items = p.getInventory().getContents().clone();
        ItemStack[] armor = p.getInventory().getArmorContents().clone();
        kit.setItems(items);
        kit.setArmor(armor);

        p.sendMessage("Kit " + kit.getName() + " establecido.");
        config.set(kit.getName(), kit);
        save();
    }

}
