package com.example.admintools;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class UnmuteCommand implements CommandExecutor {
    private final Main plugin;
    public UnmuteCommand(Main p) { plugin = p; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!s.hasPermission("admintools.unmute")) {
            s.sendMessage(plugin.getConfig().getString("messages.no-permission"));
            return true;
        }
        if (a.length < 1) {
            s.sendMessage("§cИспользуйте: /unmute <игрок>");
            return true;
        }
        UUID uuid = Bukkit.getOfflinePlayer(a[0]).getUniqueId();
        plugin.getPunishmentManager().unmute(uuid);
        s.sendMessage(plugin.getConfig().getString("messages.unmute")
                .replace("%player%", a[0])
                .replace("%prefix%", plugin.getConfig().getString("prefix")));
        return true;
    }
}