package me.hexye.npcshops.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class entityDamage implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        if (damaged instanceof Villager) {
            if (damaged.hasMetadata("NPCShopsID")) {
                event.setCancelled(true);
            }
        }
    }
}
