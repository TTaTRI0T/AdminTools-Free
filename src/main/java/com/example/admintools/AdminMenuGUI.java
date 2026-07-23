package com.example.admintools;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class AdminMenuGUI implements Listener {
    private final Main plugin;
    public AdminMenuGUI(Main p) { plugin = p; }

    public static void open(Player p, Main plugin) {
        Inventory inv = Bukkit.createInventory(null, 27, "Админ-меню");
        inv.setItem(10, createItem(Material.IRON_SWORD, "§cКикнуть", "§7/kick <игрок>"));
        inv.setItem(12, createItem(Material.BARRIER, "§4Забанить", "§7/ban <игрок>"));
        inv.setItem(14, createItem(Material.CLOCK, "§6Временный бан", "§7/tempban <игрок> <время>"));
        inv.setItem(16, createItem(Material.PAPER, "§eМут", "§7/mute <игрок> <время>"));
        inv.setItem(22, createItem(Material.BOOK, "§aПроверить", "§7/check <игрок>"));
        p.openInventory(inv);
    }

    private static ItemStack createItem(Material m, String name, String... lore) {
        ItemStack i = new ItemStack(m);
        ItemMeta me = i.getItemMeta();
        me.setDisplayName(name);
        me.setLore(Arrays.asList(lore));
        i.setItemMeta(me);
        return i;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("Админ-меню")) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player p)) return;
        switch (e.getRawSlot()) {
            case 10: p.performCommand("kick"); break;
            case 12: p.performCommand("ban"); break;
            case 14: p.performCommand("tempban"); break;
            case 16: p.performCommand("mute"); break;
            case 22: p.performCommand("check"); break;
        }
        p.closeInventory();
    }
}