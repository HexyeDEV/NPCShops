package me.hexye.npcshops;

import me.hexye.npcshops.commands.CreateShop;
import me.hexye.npcshops.database.Database;
import me.hexye.npcshops.database.YAMLDatabase;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class NPCShops extends JavaPlugin {
    private static FileConfiguration config;
    private static Database database;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        saveResource("shops.yml", false);
        config = getConfig();
        getCommand("createshop").setExecutor(new CreateShop());
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

    public static Database getDatabase() {
        return database;
    }
}
