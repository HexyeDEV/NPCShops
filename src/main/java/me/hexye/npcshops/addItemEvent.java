package me.hexye.npcshops;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.IntStream;

public class addItemEvent implements Listener {
    static NPCShops plugin;
    public addItemEvent(NPCShops plugin) {
        this.plugin = plugin;
    }
    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().getViewers().get(0).getOpenInventory().getTitle().startsWith("Add Item ")) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("NPCShops.manager")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return;
        }
        Shops shops = new Shops();
        String shopName = event.getInventory().getViewers().get(0).getOpenInventory().getTitle().replace("Add Item ", "");
        Bukkit.getScheduler().runTask(plugin, () -> {
            int not_empty_slot = IntStream.range(0, 9)
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
                ConversationFactory factory = new ConversationFactory(plugin).withEscapeSequence("cancel");
                Conversation conversation = factory.withFirstPrompt(prompt).withLocalEcho(false).buildConversation(player);
                conversation.begin();
            }
            return;
        });
    }
}
