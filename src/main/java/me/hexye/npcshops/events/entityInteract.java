package me.hexye.npcshops.events;

import me.hexye.npcshops.NPCShops;
import me.hexye.npcshops.database.Database;
import me.hexye.npcshops.menus.ShopMenu;
import me.hexye.npcshops.shops.Shop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class entityInteract implements Listener {
    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof org.bukkit.entity.Villager) {
            Villager villager = (Villager) event.getRightClicked();
            if (!villager.hasMetadata("NPCShopsID")) {
                return;
            }
            event.setCancelled(true);
            String shopId = villager.getMetadata("NPCShopsID").get(0).asString();
            UUID shopUUID = UUID.fromString(shopId);
            Database database = NPCShops.getInstance().getDatabase();
            Shop shop = database.getShop(shopUUID);
            ShopMenu menu = new ShopMenu(shop, event.getPlayer());
            menu.displayTo(event.getPlayer());
        }
    }
}
