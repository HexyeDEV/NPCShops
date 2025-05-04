package me.hexye.npcshops.commands;

import me.hexye.npcshops.NPCShops;
import me.hexye.npcshops.database.Database;
import me.hexye.npcshops.utils.Messages;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateShop implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Messages.sendMessage(sender, "&cThis command can only be used by players.");
            return true;
        }
        if (args.length < 2) {
            return false;
        }
        String sizeString = args[0];
        StringBuilder shopNameBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            shopNameBuilder.append(args[i]);
            if (i != args.length - 1) {
                shopNameBuilder.append(" ");
            }
        }
        String shopName = shopNameBuilder.toString();
        int size;
        try {
            size = Integer.parseInt(sizeString);
        } catch (NumberFormatException e) {
            Messages.sendMessage(sender, "&cInvalid size. Please enter a valid number.");
            return true;
        }
        if (size % 9 != 0) {
            Messages.sendMessage(sender, "&cSize must be a multiple of 9.");
            return true;
        }
        if (size < 9 || size > 54) {
            Messages.sendMessage(sender, "&cSize must be between 9 and 54.");
            return true;
        }
        Location location = ((Player) sender).getLocation();
        Database database = NPCShops.getInstance().getDatabase();
        database.createShop(shopName, location, size);
        Messages.sendMessage(sender, "&aShop created successfully!");
        return true;
    }
}
