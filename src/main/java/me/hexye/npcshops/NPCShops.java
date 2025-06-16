package me.hexye.npcshops;

import me.hexye.npcshops.commands.CreateShop;
import me.hexye.npcshops.database.Database;
import me.hexye.npcshops.database.YAMLDatabase;
import me.hexye.npcshops.events.entityDamage;
import me.hexye.npcshops.events.entityInteract;
import me.hexye.npcshops.events.menuEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class NPCShops extends JavaPlugin {
    private static FileConfiguration config;
    private static Database database;
    private static FileConfiguration messages;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        saveResource("shops.yml", false);
        saveResource("messages.yml", false);
        config = getConfig();
        messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));
        getCommand("createshop").setExecutor(new CreateShop());
        Bukkit.getPluginManager().registerEvents(new entityInteract(), this);
        Bukkit.getPluginManager().registerEvents(new entityDamage(), this);
        Bukkit.getPluginManager().registerEvents(new menuEvents(), this);
        int storageType = config.getInt("storage-type");
        if (storageType == 0) {
            database = new YAMLDatabase();
        }
        database.setupDatabase();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static NPCShops getInstance() {
        return NPCShops.getPlugin(NPCShops.class);
    }

    public static FileConfiguration getPluginConfig() {
        return config;
    }

    public Database getDatabase() {
        return database;
    }

    public String getMessage(String key) {
        return messages.getString(key, "&cMessage not found: " + key);
    }
}
