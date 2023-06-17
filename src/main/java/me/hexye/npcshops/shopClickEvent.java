package me.hexye.npcshops;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

public class shopClickEvent implements Listener {

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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Shops shops = new Shops();
        if (!shops.shopExists(event.getInventory().getViewers().get(0).getOpenInventory().getTitle())) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("NPCShops.use")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this shop!");
            return;
        }
        int slot = event.getSlot();
        int price = shops.getItemPrice(event.getInventory().getViewers().get(0).getOpenInventory().getTitle(), slot);
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
        player.getInventory().addItem(item);
        econ.withdrawPlayer(player, price);
        player.sendMessage(ChatColor.GREEN + "You have bought " + item.getType().toString() + " for " + price + "! You now have " + econ.getBalance(player) + "!");
        player.closeInventory();
    }
}
