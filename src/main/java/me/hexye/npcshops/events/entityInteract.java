package me.hexye.npcshops.events;

import me.hexye.npcshops.NPCShops;
import me.hexye.npcshops.database.Database;
import me.hexye.npcshops.shops.Shop;
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
            String shopId = villager.getMetadata("NPCShopsID").get(0).asString();
            UUID shopUUID = UUID.fromString(shopId);
            Database database = NPCShops.getInstance().getDatabase();
            Shop shop = database.getShop(shopUUID);
        }
    }
}
