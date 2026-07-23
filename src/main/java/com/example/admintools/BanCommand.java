package com.example.admintools;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BanCommand implements CommandExecutor {
    private final Main plugin;
    public BanCommand(Main p) { plugin = p; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!s.hasPermission("admintools.ban")) {
            s.sendMessage(plugin.getConfig().getString("messages.no-permission"));
            return true;
        }
        if (a.length < 1) {
            s.sendMessage("§cИспользуйте: /ban <игрок> [причина]");
            return true;
        }
        String name = a[0];
        UUID uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
        String reason = a.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(a, 1, a.length)) : "Не указана";
        plugin.getPunishmentManager().ban(uuid, reason, 0);
        Bukkit.broadcastMessage(plugin.getConfig().getString("messages.ban")
                .replace("%player%", name)
                .replace("%reason%", reason)
                .replace("%prefix%", plugin.getConfig().getString("prefix")));

        // Запись статистики
        if (s instanceof Player) {
            plugin.getPunishmentManager().recordStaffAction(((Player) s).getUniqueId(), "ban");
        } else {
            plugin.getPunishmentManager().recordStaffAction(new UUID(0, 0), "ban");
        }

        return true;
    }
}