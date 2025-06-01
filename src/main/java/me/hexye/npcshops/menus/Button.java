package me.hexye.npcshops.menus;

import org.bukkit.inventory.ItemStack;

public abstract class Button {
    private ItemStack item;
    private final int slot;

    public Button(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public abstract ItemStack getItem();

    public abstract void onClick();
}
