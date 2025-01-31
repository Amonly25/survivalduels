package com.ar.askgaming.survivalduels.Kits;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class Kit implements ConfigurationSerializable{

    public Kit(String name) {
        this.name = name;
    }   
    public Kit(Map<String, Object> map) {
        this.name = (String) map.get("name");
        List<ItemStack> items = (List<ItemStack>) map.get("items");
        List<ItemStack> armor = (List<ItemStack>) map.get("armor");
        this.items = items.toArray(new ItemStack[0]);
        this.armor = armor.toArray(new ItemStack[0]);

    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("items", items);
        map.put("armor", armor);
        return map;
    }
    
    private String name;
    private ItemStack[] items = new ItemStack[36];
    private ItemStack[] armor = new ItemStack[4];

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ItemStack[] getItems() {
        return items;
    }
    public void setItems(ItemStack[] items) {
        this.items = items;
    }
    public ItemStack[] getArmor() {
        return armor;
    }
    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

}
