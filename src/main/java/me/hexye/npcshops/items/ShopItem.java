package me.hexye.npcshops.items;


import org.bukkit.inventory.ItemStack;

public class ShopItem {
    private final String base64Item;
    private final ItemStack itemStack;
    private int price;

    public ShopItem(String base64Item, ItemStack itemStack, int price) {
        this.base64Item = base64Item;
        this.itemStack = itemStack;
        this.price = price;
    }

    public String getBase64Item() {
        return base64Item;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
