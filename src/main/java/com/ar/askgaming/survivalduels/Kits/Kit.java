package com.ar.askgaming.survivalduels.Kits;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class Kit implements ConfigurationSerializable {

    private String name;
    private ItemStack[] items = new ItemStack[36];
    private ItemStack[] armor = new ItemStack[4];

    public Kit(String name) {
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public Kit(Map<String, Object> map) {
        this.name = (String) map.getOrDefault("name", "default");
        
        // Cargar items asegurando que no sean nulos
        List<ItemStack> itemsList = (List<ItemStack>) map.get("items");
        this.items = (itemsList != null) ? itemsList.toArray(new ItemStack[0]) : new ItemStack[36];

        // Cargar armadura asegurando que no sea nula
        List<ItemStack> armorList = (List<ItemStack>) map.get("armor");
        this.armor = (armorList != null) ? armorList.toArray(new ItemStack[0]) : new ItemStack[4];
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("items", Arrays.asList(items)); // Guardar como lista
        map.put("armor", Arrays.asList(armor)); // Guardar como lista
        return map;
    }

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
        this.items = (items != null) ? items : new ItemStack[36];
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = (armor != null) ? armor : new ItemStack[4];
    }
}