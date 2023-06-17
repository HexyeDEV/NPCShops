package me.hexye.npcshops;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

public class previewShopEvent implements Listener {

    private static Economy econ = null;
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().getViewers().get(0).getOpenInventory().getTitle().startsWith("Preview Shop ")) {
            return;
        }
        event.setCancelled(true);
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        Shops shops = new Shops();
        String shopName = event.getInventory().getViewers().get(0).getOpenInventory().getTitle().replace("Preview Shop ", "");
        if (event.getAction() == InventoryAction.PICKUP_ALL) {
            int price = shops.getItemPrice(shopName, slot);
            if (!setupEconomy()) {
                RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
                econ = rsp.getProvider();
            }
            if (econ == null) {
                Bukkit.getLogger().info(ChatColor.RED + "Vault economy is not enabled. This plugin depends on Vault economy. Make sure you have Vault economy enabled!");
                return;
            }
            if (econ.getBalance(player) < price) {
                player.sendMessage(ChatColor.RED + "You do not have enough money to buy this item!");
                return;
            }
            ItemStack item = event.getCurrentItem();
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(ChatColor.RED + "You do not have enough space in your inventory to buy this item!");
                return;
            }
            econ.withdrawPlayer(player, price);
            player.getInventory().addItem(item);
            player.sendMessage(ChatColor.GREEN + "You have bought the item for " + price + "!");
            player.closeInventory();
            return;
        }
        if (event.getAction() == InventoryAction.PICKUP_HALF) {
            shops.removeItem(shopName, slot);
            player.sendMessage(ChatColor.GREEN + "You have removed the item from the shop!");
            return;
        }
    }

}
