package me.hexye.npcshops;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class NPCShops extends JavaPlugin implements Listener {

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

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();

        }
        File shopsFolder = new File(getDataFolder() + "/shops");
        if (!shopsFolder.exists()){
            shopsFolder.mkdirs();
        }
        if (!setupEconomy()) {
            Bukkit.getLogger().info(ChatColor.RED + "Vault economy is not enabled. This plugin depends on Vault economy. Make sure you have Vault economy enabled!");
        }
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new addItemEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new previewShopEvent(), this);
        this.getServer().getPluginManager().registerEvents(new shopClickEvent(), this);
        this.getServer().getPluginManager().registerEvents(new shopManagerEvent(), this);
        Bukkit.getLogger().info(ChatColor.GREEN + "NPCShops has been enabled!");
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("createshop")) {
            if(!sender.hasPermission("NPCShops.manager")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Please specify a size and a name for the shop!");
                return true;
            }
            if (args.length == 1) {
                sender.sendMessage(ChatColor.RED + "Please specify a size and a name for the shop!");
                return true;
            }
            int size = 0;
            try {
                size = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Please specify valid a size and a name for the shop!");
                return true;
            }
            if (size % 9 != 0) {
                sender.sendMessage(ChatColor.RED + "Please specify a size that is a multiple of 9!");
                return true;
            }
            if (size > 54) {
                sender.sendMessage(ChatColor.RED + "Please specify a size that is less than or equal to 54!");
                return true;
            }
            String name = String.join(" ", Arrays.asList(args).subList(1, args.length));
            Shops shops = new Shops();
            shops.addShop(name, size);
            Player player = (Player) sender;
            Villager villager = (Villager) player.getWorld().spawn(player.getLocation(), Villager.class);
            villager.setCustomName(name);
            villager.setCustomNameVisible(true);
            villager.setAI(false);
            villager.setInvulnerable(true);
            villager.setSilent(true);
            villager.setCollidable(false);
            villager.setCanPickupItems(false);
            villager.setRemoveWhenFarAway(false);
            player.sendMessage(ChatColor.GREEN + "You have created a shop with the name " + name + " and the size " + size + "!");
            return true;
        }
        return true;
    }

    @EventHandler()
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager) {
            Villager villager = (Villager) event.getRightClicked();
            if (villager.getCustomName() == null) {
                return;
            }
            Shops shops = new Shops();
            if (!shops.shopExists(villager.getCustomName())) {
                return;
            }
            event.setCancelled(true);
            Player player = event.getPlayer();
            if (!player.hasPermission("NPCShops.use")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this shop!");
                return;
            }
            if (player.hasPermission("NPCShops.manager")) {
                Inventory inventory = Bukkit.createInventory(null, 36, "Shop Manager " + villager.getCustomName());
                ItemStack item = new ItemStack(Material.BARRIER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "Delete Shop");
                item.setItemMeta(meta);
                inventory.setItem(31, item);
                ItemStack item2 = new ItemStack(Material.CHEST);
                ItemMeta meta2 = item2.getItemMeta();
                meta2.setDisplayName(ChatColor.GREEN + "Add Item");
                item2.setItemMeta(meta2);
                inventory.setItem(13, item2);
                ItemStack item3 = new ItemStack(Material.BOOK);
                ItemMeta meta3 = item3.getItemMeta();
                meta3.setDisplayName(ChatColor.GREEN + "Preview Shop");
                item3.setItemMeta(meta3);
                inventory.setItem(22, item3);
                player.openInventory(inventory);
            } else {
                List<ItemStack> items = shops.getItems(villager.getCustomName());
                int size = shops.getSize(villager.getCustomName());
                Inventory inventory = Bukkit.createInventory(null, size, villager.getCustomName());
                for (int i = 0; i < items.size(); i++) {
                    ItemStack item = items.get(i);
                    int price = shops.getItemPrice(villager.getCustomName(), i);
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GREEN + "Price: " + price);
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    inventory.setItem(i, items.get(i));
                    player.openInventory(inventory);
                }
            }
        }
    }

    @EventHandler()
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Villager) {
            Villager villager = (Villager) event.getEntity();
            if (villager.getCustomName() == null) {
                return;
            }
            Shops shops = new Shops();
            if (!shops.shopExists(villager.getCustomName())) {
                return;
            }
            event.setCancelled(true);
        }
    }

    @Override()
    public void onDisable() {
        Bukkit.getLogger().info(ChatColor.RED + "NPCShops has been disabled!");
    }
}
