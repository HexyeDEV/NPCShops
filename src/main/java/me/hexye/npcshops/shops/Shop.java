package me.hexye.npcshops.shops;

import me.hexye.npcshops.items.ShopItem;
import org.bukkit.Location;
import org.bukkit.entity.Villager;

import java.util.List;
import java.util.UUID;

public class Shop {
    private Villager villager;
    private Location location;
    private String name;
    private int size;
    private List<ShopItem> items;
    private UUID shopId;

    public Shop(Villager villager, Location location, String name, int size, List<ShopItem> items) {
        this.villager = villager;
        this.location = location;
        this.name = name;
        this.size = size;
        this.items = items;
        this.shopId = UUID.randomUUID();
    }

    public Shop(Villager villager, Location location, String name, int size, List<ShopItem> items, UUID shopId) {
        this.villager = villager;
        this.location = location;
        this.name = name;
        this.size = size;
        this.items = items;
        this.shopId = shopId;
    }

    public Villager getVillager() {
        return villager;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public List<ShopItem> getItems() {
        return items;
    }

    public UUID getShopId() {
        return shopId;
    }

    public void addItem(ShopItem item) {
        items.add(item);
    }
}
