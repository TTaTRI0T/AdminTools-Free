package com.example.admintools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminMenuCommand implements CommandExecutor {
    private final Main plugin;
    public AdminMenuCommand(Main p) { plugin = p; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) {
            s.sendMessage("Только игроки!");
            return true;
        }
        if (!p.hasPermission("admintools.admin")) {
            p.sendMessage(plugin.getConfig().getString("messages.no-permission"));
            return true;
        }
        AdminMenuGUI.open(p, plugin);
        return true;
    }
}