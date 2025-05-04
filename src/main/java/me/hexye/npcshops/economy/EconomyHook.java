package me.hexye.npcshops.economy;

import org.bukkit.OfflinePlayer;

public interface EconomyHook {
    void setupEconomy();
    void deposit(OfflinePlayer player, double amount);
    void withdraw(OfflinePlayer player, double amount);
    boolean hasEnough(OfflinePlayer player, double amount);
}
