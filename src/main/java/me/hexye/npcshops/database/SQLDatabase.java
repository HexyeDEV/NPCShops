package me.hexye.npcshops.database;

import me.hexye.npcshops.shops.Shop;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SQLDatabase implements Database {
    @Override
    public void setupDatabase() {

    }

    @Override
    public void loadShops() {

    }

    @Override
    public UUID createShop(String shopName, Location location, int size) {
        return null;
    }

    @Override
    public void deleteShop(UUID shopId) {

    }

    @Override
    public void addItemToShop(UUID shopId, ItemStack itemStack, int price) {

    }

    @Override
    public void saveShop(UUID shopId, Shop shop) {

    }

    @Override
    public Shop getShop(UUID shopId) {
        return null;
    }
}
