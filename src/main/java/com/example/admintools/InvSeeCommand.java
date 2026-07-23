package com.example.admintools;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InvSeeCommand implements CommandExecutor {
    private final Main plugin;
    public InvSeeCommand(Main p) { plugin = p; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player admin)) { s.sendMessage("Только игроки!"); return true; }
        if (!admin.hasPermission("admintools.invsee")) { admin.sendMessage(plugin.getConfig().getString("messages.no-permission")); return true; }
        if (a.length < 1) { admin.sendMessage("§cИспользуйте: /invsee <игрок>"); return true; }
        Player target = Bukkit.getPlayer(a[0]);
        if (target == null) { admin.sendMessage(plugin.getConfig().getString("messages.player-not-found")); return true; }
        Inventory inv = target.getInventory();
        admin.openInventory(inv);
        admin.sendMessage("§aОткрыт инвентарь игрока " + target.getName());
        return true;
    }
}