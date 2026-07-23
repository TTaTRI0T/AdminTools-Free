package com.example.admintools;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand implements CommandExecutor {
    private final Main plugin;
    public StaffChatCommand(Main p) { plugin = p; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!s.hasPermission("admintools.staffchat")) {
            s.sendMessage(plugin.getConfig().getString("messages.no-permission"));
            return true;
        }
        if (a.length == 0) {
            s.sendMessage("§cИспользуйте: /staffchat <сообщение>");
            return true;
        }
        String msg = String.join(" ", a);
        String format = plugin.getConfig().getString("messages.staffchat")
                .replace("%player%", s.getName())
                .replace("%message%", msg);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("admintools.staffchat")) p.sendMessage(format);
        }
        return true;
    }
}