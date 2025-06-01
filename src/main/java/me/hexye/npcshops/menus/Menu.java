package me.hexye.npcshops.menus;

import me.hexye.npcshops.NPCShops;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Menu {
    private final List<Button> buttons = new ArrayList<>();
    private int size = 27;
    private String title;

    public final List<Button> getButtons() {
        return this.buttons;
    }

    private Inventory playerInventory = null;
    private Player inventoryOwner = null;

    private final void loadButtons(Inventory inv) {
        for (Button button: buttons) {
            inv.setItem(button.getSlot(), button.getItem());
        }
    }

    protected final void addButton(Button button) {
        int slot = button.getSlot();
        removeButton(slot);
        this.buttons.add(button);
        if (this.playerInventory != null) {
            loadButtons(this.playerInventory);
            this.inventoryOwner.setMetadata("NPCShopMenu", new FixedMetadataValue(NPCShops.getInstance(), this));
        }
    }

    protected final void removeButton(int slot) {
        Iterator<Button> iterator = this.buttons.iterator();
        while (iterator.hasNext()) {
            Button button = iterator.next();
            if (button.getSlot() == slot) {
                iterator.remove();
                if (this.playerInventory != null) {
                    this.playerInventory.setItem(slot, null);
                    this.inventoryOwner.setMetadata("NPCShopMenu", new FixedMetadataValue(NPCShops.getInstance(), this));
                }
                break;
            }
        }
    }

    protected final void setSize(int size) {
        this.size = size;
    }

    protected final void setTitle(String title) {
        this.title = title;
        if (this.playerInventory != null) {
            this.inventoryOwner.closeInventory();
            this.playerInventory = Bukkit.createInventory(this.inventoryOwner, this.size, ChatColor.translateAlternateColorCodes('&', this.title));
            loadButtons(this.playerInventory);
            this.inventoryOwner.setMetadata("NPCShopMenu", new FixedMetadataValue(NPCShops.getInstance(), this));
            this.inventoryOwner.openInventory(this.playerInventory);
        }
    }

    public final void displayTo(Player player) {
        player.closeInventory();
        Inventory inv = Bukkit.createInventory(player, this.size, ChatColor.translateAlternateColorCodes('&', this.title));

        loadButtons(inv);
        player.setMetadata("NPCShopMenu", new FixedMetadataValue(NPCShops.getInstance(), this));
        player.openInventory(inv);
        this.playerInventory = inv;
        this.inventoryOwner = player;
    }
}
