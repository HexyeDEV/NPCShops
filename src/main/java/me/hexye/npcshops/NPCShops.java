package me.hexye.npcshops;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
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
            int size = Integer.parseInt(args[0]);
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
    public void onInventoryClick(InventoryClickEvent event) {
        Shops shops = new Shops();
        if (!shops.shopExists(event.getInventory().getName()) && !event.getInventory().getName().startsWith("Shop Manager") && !event.getInventory().getName().startsWith("Add Item ") && !event.getInventory().getName().startsWith("Preview Shop ")) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (event.getInventory().getName().startsWith("Shop Manager")) {
            event.setCancelled(true);
            if (!player.hasPermission("NPCShops.manager")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return;
            }
            String shopName = event.getInventory().getName().replace("Shop Manager ", "");
            if (event.getCurrentItem().getType() == Material.BARRIER) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Delete Shop")) {
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
            }
            if (event.getCurrentItem().getType() == Material.CHEST) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Add Item")) {
                    player.closeInventory();
                    Inventory inv = Bukkit.createInventory(null, 9, "Add Item " + shopName);
                    player.openInventory(inv);
                    return;
                }
            }
            if (event.getCurrentItem().getType() == Material.BOOK) {
                Inventory inv = Bukkit.createInventory(null, shops.getSize(shopName), "Preview Shop " + shopName);
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
                    inv.setItem(i, item);
                }
                player.closeInventory();
                player.openInventory(inv);
                return;
            }
            return;
        }
        if (event.getInventory().getName().startsWith("Add Item ")) {
            if (!player.hasPermission("NPCShops.manager")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return;
            }
            InventoryAction action = event.getAction();
            if (action != InventoryAction.PLACE_ALL && action != InventoryAction.PLACE_ONE && action != InventoryAction.PLACE_SOME) {
                return;
            }
            String shopName = event.getInventory().getName().replace("Add Item ", "");
            int not_empty_slot = IntStream.range(0, shops.getSize(shopName))
                    .filter(i -> event.getInventory().getItem(i) != null)
                    .findFirst()
                    .orElse(-1);
            if (not_empty_slot != -1) {
                player.closeInventory();
                Prompt prompt = new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return ChatColor.GREEN + "Please type the price for the item in chat (Type cancel to cancel).";
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        context.setSessionData("input", input);
                        try {
                            int price = Integer.parseInt(input);
                        } catch (NumberFormatException e) {
                            return new MessagePrompt() {
                                @Override
                                public String getPromptText(ConversationContext context) {
                                    return ChatColor.RED + "Please type a valid number!";
                                }

                                @Override
                                protected Prompt getNextPrompt(ConversationContext context) {
                                    return null;
                                }
                            };
                        }
                        return new MessagePrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return ChatColor.GREEN + "You have set the price to " + input + "!";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                ItemStack item = event.getInventory().getItem(not_empty_slot);
                                List<ItemStack> items = shops.getItems(shopName);
                                int slot = 0;
                                for (ItemStack item1 : items) {
                                    if (item1 == null) {
                                        break;
                                    }
                                    slot++;
                                }
                                shops.addItem(shopName, item, slot, Integer.parseInt((String) context.getSessionData("input")));
                                player.sendMessage(ChatColor.GREEN + "You have added the item to the shop " + shopName + "!");
                                return Prompt.END_OF_CONVERSATION;
                            }

                            @Override
                            protected Prompt getNextPrompt(ConversationContext context) {
                                return null;
                            }
                        };
                    }
                };
                ConversationFactory factory = new ConversationFactory(this).withEscapeSequence("cancel");
                Conversation conversation = factory.withFirstPrompt(prompt).withLocalEcho(false).buildConversation(player);
                conversation.begin();
            }
            return;
        }
        if (event.getInventory().getName().startsWith("Preview Shop ")) {
            String shopName = event.getInventory().getName().replace("Preview Shop ", "");
            if (event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            event.setCancelled(true);
            int slot = event.getSlot();
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
        event.setCancelled(true);
        if (!player.hasPermission("NPCShops.use")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this shop!");
            return;
        }
        int slot = event.getSlot();
        int price = shops.getItemPrice(event.getInventory().getName(), slot);
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
