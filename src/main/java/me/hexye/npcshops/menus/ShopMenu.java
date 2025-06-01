package me.hexye.npcshops.menus;

import me.hexye.npcshops.items.ShopItem;
import me.hexye.npcshops.shops.Shop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
            int slot = i; // Assuming the slot is the index in the list
            addButton(new Button(slot) {;
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
        // Button to add item or view the shop.
        addButton(new Button(0) {
            @Override
            public ItemStack getItem() {
                ItemStack item = new ItemStack(Material.BOOK);
                item.getItemMeta().setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aAdd Item"));
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
                item.getItemMeta().setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cDelete Shop"));
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
                item.getItemMeta().setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bView Shop"));
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

    }
}
