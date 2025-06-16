package me.hexye.npcshops.menus;

import me.hexye.npcshops.NPCShops;
import me.hexye.npcshops.database.Database;
import me.hexye.npcshops.items.ShopItem;
import me.hexye.npcshops.shops.Shop;
import me.hexye.npcshops.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class ShopMenu extends Menu{
    private final Shop shop;
    private final Player player;


    public ShopMenu(Shop shop, Player player) {
        this.shop = shop;
        this.player = player;
        setSize(shop.getSize());
        setTitle(shop.getName());
        loadFullInventory();
    }

    private final void loadFullInventory() {
        if (this.player.hasPermission("npcshops.admin")) {
            loadAdminButtons();
        } else {
            loadItems();
        }
    }

    private final void loadItems() {
        List<ShopItem> items = shop.getItems();
        for (int i = 0; i < items.size(); i++) {
            ShopItem item = items.get(i);
            addButton(new Button(i) {;
                @Override
                public ItemStack getItem() {
                    return item.getItemStack();
                }

                @Override
                public void onClick() {
                    buyItem(item);
                }
            });
        }
    }

    private final void loadAdminItems() {

    }

    private final void loadAdminButtons() {
        addButton(new Button(0) {
            @Override
            public ItemStack getItem() {
                ItemStack item = new ItemStack(Material.BOOK);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aAdd Item"));
                item.setItemMeta(meta);
                return item;
            }

            @Override
            public void onClick() {
                addItem();
            }
        });

        addButton(new Button(1) {
            @Override
            public ItemStack getItem() {
                ItemStack item = new ItemStack(Material.BARRIER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cDelete Shop"));
                item.setItemMeta(meta);
                return item;
            }

            @Override
            public void onClick() {
                deleteShop();
            }
        });

        addButton(new Button(2) {
            @Override
            public ItemStack getItem() {
                ItemStack item = new ItemStack(Material.CHEST);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bView Shop"));
                item.setItemMeta(meta);
                return item;
            }

            @Override
            public void onClick() {
                loadAdminItems();
            }
        });
    }

    private final void buyItem(ShopItem item) {

    }

    private final void addItem() {

    }

    private final void deleteShop() {
        Database database = NPCShops.getInstance().getDatabase();
        database.deleteShop(shop.getShopId());
        Messages.sendMessage(player, NPCShops.getInstance().getMessage("delete-shop"));
    }
}
