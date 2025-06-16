package me.hexye.npcshops.events;

import me.hexye.npcshops.menus.Button;
import me.hexye.npcshops.menus.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.metadata.MetadataValue;

public class menuEvents implements Listener {
    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (player.hasMetadata("NPCShopMenu")) {
            Menu menu = (Menu)((MetadataValue)player.getMetadata("NPCShopMenu").get(0)).value();

            for (Button button : menu.getButtons()) {
                if (button.getSlot() == slot) {
                    event.setCancelled(true);
                    button.onClick();
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (player.hasMetadata("NPCShopMenu")) {
            player.removeMetadata("NPCShopMenu", me.hexye.npcshops.NPCShops.getInstance());
            player.closeInventory();
        }
    }
}
