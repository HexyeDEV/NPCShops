package me.hexye.npcshops;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class shopManagerEvent implements Listener {
    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().getName().startsWith("Shop Manager ")) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("NPCShops.manager")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to do that!");
            return;
        }
        event.setCancelled(true);
        Inventory inv = event.getInventory();
        String shopName = inv.getName().replace("Shop Manager ", "");
        Shops shops = new Shops();
        if (!shops.shopExists(shopName)) {
            player.sendMessage(ChatColor.RED + "Shop doesn't exist!");
            return;
        }
        if (event.getCurrentItem().getType() == Material.BARRIER && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Delete Shop")) {
            shops.removeShop(shopName);
            for (Villager villager : player.getWorld().getEntitiesByClass(Villager.class)) {
                if (villager.getCustomName().equalsIgnoreCase(shopName)) {
                    villager.remove();
                }
            }
            player.sendMessage(ChatColor.GREEN + "You have deleted the shop " + shopName + "!");
            player.closeInventory();
            return;
        }
        if (event.getCurrentItem().getType() == Material.CHEST) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Add Item")) {
                player.closeInventory();
                Inventory inventory = Bukkit.createInventory(null, 9, "Add Item " + shopName);
                player.openInventory(inventory);
                return;
            }
        }
        if (event.getCurrentItem().getType() == Material.BOOK) {
            Inventory inventory = Bukkit.createInventory(null, shops.getSize(shopName), "Preview Shop " + shopName);
            List<ItemStack> items = shops.getItems(shopName);
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                if (item == null) {
                    continue;
                }
                ItemMeta meta = item.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.AQUA + "Price: " + shops.getItemPrice(shopName, i));
                lore.add(ChatColor.GREEN + "Left click to buy");
                lore.add(ChatColor.RED + "Right click to remove item");
                meta.setLore(lore);
                item.setItemMeta(meta);
                inventory.setItem(i, item);
            }
            player.closeInventory();
            player.openInventory(inventory);
            return;
        }
    }
}
