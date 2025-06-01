package me.hexye.npcshops.database;

import me.hexye.npcshops.NPCShops;
import me.hexye.npcshops.items.ItemSerializer;
import me.hexye.npcshops.items.ShopItem;
import me.hexye.npcshops.shops.Shop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class YAMLDatabase implements Database {
    private final File shopsFile = new File(NPCShops.getInstance().getDataFolder(), "shops.yml");
    private YamlConfiguration shops;
    private final HashMap<UUID, Shop> shopMap = new HashMap<>();
    @Override
    public void setupDatabase() {
        shops = YamlConfiguration.loadConfiguration(shopsFile);
        loadShops();
    }

    @Override
    public void loadShops() {
        for (String id : shops.getKeys(false)) {
            String name = shops.getString(id + ".name");
            Location location = (Location) shops.get(id + ".location");
            int size = shops.getInt(id + ".size");
            UUID shopId = UUID.fromString(id);
            List<String> itemsBase64s = shops.getStringList(id + ".items");
            List<ItemStack> items = new ArrayList<>();
            for (String itemBase64 : itemsBase64s) {
                ItemStack item;
                try {
                    item = ItemSerializer.itemFrom64(itemBase64);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                items.add(item);
            }
            List<ShopItem> shopItems = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                String base64 = itemsBase64s.get(i);
                int price = shops.getInt(id + ".prices." + i);
                ShopItem shopItem = new ShopItem(base64, item, price);
                shopItems.add(shopItem);
            }
            Villager villager = location.getWorld().spawn(location, Villager.class);
            villager.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
            villager.setCustomNameVisible(true);
            villager.setAI(false);
            villager.setCollidable(false);
            villager.setSilent(true);
            villager.setInvulnerable(true);
            villager.setGravity(false);
            villager.setMetadata("NPCShopsID", new FixedMetadataValue(NPCShops.getInstance(), shopId.toString()));

            Shop shop = new Shop(villager, location, name, size, shopItems);
            shopMap.put(shopId, shop);
        }
    }

    @Override
    public UUID createShop(String shopName, Location location, int size) {
        UUID shopId = UUID.randomUUID();
        while (shops.contains(shopId.toString())) {
            shopId = UUID.randomUUID();
        }
        Villager villager = location.getWorld().spawn(location, Villager.class);
        villager.setCustomName(ChatColor.translateAlternateColorCodes('&', shopName));
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setCollidable(false);
        villager.setSilent(true);
        villager.setInvulnerable(true);
        villager.setGravity(false);
        villager.setMetadata("NPCShopsID", new FixedMetadataValue(NPCShops.getInstance(), shopId.toString()));

        Shop shop = new Shop(villager, location, shopName, size, new ArrayList<>());
        shopMap.put(shopId, shop);
        saveShop(shopId, shop);
        return shopId;
    }

    @Override
    public void deleteShop(UUID shopId) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Villager villager = (Villager) shopMap.get(shopId).getVillager();
                if (villager != null) {
                    villager.remove();
                }
                shops.set(shopId.toString(), null);
                shopMap.remove(shopId);
                try {
                    shops.save(shopsFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(NPCShops.getInstance());
    }

    @Override
    public void addItemToShop(UUID shopId, ItemStack itemStack, int price) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ShopItem shopItem = new ShopItem(ItemSerializer.itemTo64(itemStack), itemStack, price);
                Shop shop = shopMap.get(shopId);
                if (shop != null) {
                    shop.addItem(shopItem);
                    saveShop(shopId, shop);
                } else {
                    Bukkit.getLogger().warning("Shop with ID " + shopId + " not found.");
                }
            }
        }.runTaskAsynchronously(NPCShops.getInstance());
    }

    @Override
    public void saveShop(UUID shopId, Shop shop) {
        new BukkitRunnable() {
            @Override
            public void run() {
                shops.set(shopId + ".location", shop.getLocation());
                shops.set(shopId + ".name", shop.getName());
                shops.set(shopId + ".size", shop.getSize());
                List<String> itemsBase64s = new ArrayList<>();
                List<Integer> prices = new ArrayList<>();
                for (ShopItem item : shop.getItems()) {
                    itemsBase64s.add(item.getBase64Item());
                    prices.add(item.getPrice());
                }
                shops.set(shopId + ".items", itemsBase64s);
                shops.set(shopId + ".prices", prices);
                try {
                    shops.save(shopsFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(NPCShops.getInstance());
    }

    @Override
    public Shop getShop(UUID shopId) {
        return shopMap.get(shopId);
    }
}
