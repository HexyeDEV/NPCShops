package me.hexye.npcshops.database;

import me.hexye.npcshops.shops.Shop;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface Database {
    void setupDatabase();
    void loadShops();
    UUID createShop(String shopName, Location location, int size);
    void deleteShop(UUID shopId);
    void addItemToShop(UUID shopId, ItemStack itemStack, int price);
    void saveShop(UUID shopId, Shop shop);
    Shop getShop(UUID shopId);
}
