package me.hexye.npcshops;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Shops {
    public boolean addShop(String name, int inventorySize) {
        File f = new File(Bukkit.getServer().getPluginManager().getPlugin("NPCShops").getDataFolder() + "/shops/" + name + ".yml");
        if (!f.exists()){
            try {
                f.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            FileConfiguration c = YamlConfiguration.loadConfiguration(f);
            c.createSection("Data");
            c.set("Data.Name", name);
            c.set("Data.InventorySize", inventorySize);
            c.createSection("Items");
            try {
                c.save(f);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } else {
            return false;
        }
    }


    public boolean removeShop(String name) {
        File f = new File(Bukkit.getServer().getPluginManager().getPlugin("NPCShops").getDataFolder() + "/shops/" + name + ".yml");
        if (f.exists()){
            try {
                f.delete();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean shopExists(String name) {
        File f = new File(Bukkit.getServer().getPluginManager().getPlugin("NPCShops").getDataFolder() + "/shops/" + name + ".yml");
        return f.exists();
    }

    public void addItem(String name, ItemStack item, int index, float price) {
        if (!shopExists(name)) {
            return;
        }
        File f = new File(Bukkit.getServer().getPluginManager().getPlugin("NPCShops").getDataFolder() + "/shops/" + name + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        c.createSection("Items." + index);
        c.set("Items." + index + ".item", item);
        c.set("Items." + index + ".price", price);
        try {
            c.save(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeItem(String name, int index) {
        if (!shopExists(name)) {
            return;
        }
        File f = new File(Bukkit.getServer().getPluginManager().getPlugin("NPCShops").getDataFolder() + "/shops/" + name + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        c.set("Items." + index, null);
        try {
            c.save(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bukkit.getLogger().info("Removed item " + index + " from shop " + name);
        File f2 = new File(Bukkit.getServer().getPluginManager().getPlugin("NPCShops").getDataFolder() + "/shops/" + name + ".yml");
        FileConfiguration c2 = YamlConfiguration.loadConfiguration(f2);
        for (int i = index + 1; i < c2.getConfigurationSection("Items").getKeys(false).size() + 1; i++) {
            String i_string = Integer.toString(i);
            String i_string_minus_one = Integer.toString(i - 1);
            c2.set("Items." + i_string_minus_one + ".item", c2.getItemStack("Items." + i_string + ".item"));
            c2.set("Items." + i_string_minus_one + ".price", c2.getInt("Items." + i_string + ".price"));
            c2.set("Items." + i_string, null);
            Bukkit.getLogger().info(i_string_minus_one + " " + i_string);
        }
        try {
            c2.save(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ItemStack> getItems(String name) {
        if (!shopExists(name)) {
            return null;
        }
        File f = new File(Bukkit.getServer().getPluginManager().getPlugin("NPCShops").getDataFolder() + "/shops/" + name + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < c.getConfigurationSection("Items").getKeys(false).size(); i++) {
            String i_string = Integer.toString(i);
            items.add(c.getItemStack("Items." + i_string + ".item"));
        }
        return items;
    }

    public int getSize(String name) {
        if (!shopExists(name)) {
            return 0;
        }
        File f = new File(Bukkit.getServer().getPluginManager().getPlugin("NPCShops").getDataFolder() + "/shops/" + name + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        return c.getInt("Data.InventorySize");
    }

    public int getItemPrice(String name, int index) {
        if (!shopExists(name)) {
            return 0;
        }
        File f = new File(Bukkit.getServer().getPluginManager().getPlugin("NPCShops").getDataFolder() + "/shops/" + name + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        return c.getInt("Items." + index + ".price");
    }
}
