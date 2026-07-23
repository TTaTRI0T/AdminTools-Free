package com.example.admintools;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WarnCommand implements CommandExecutor {
    private final Main plugin;
    public WarnCommand(Main p) { plugin = p; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!s.hasPermission("admintools.warn")) {
            s.sendMessage(plugin.getConfig().getString("messages.no-permission"));
            return true;
        }
        if (a.length < 1) {
            s.sendMessage("§cИспользуйте: /warn <игрок> [причина]");
            return true;
        }
        String name = a[0];
        UUID uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
        String reason = a.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(a, 1, a.length)) : "Не указана";
        plugin.getPunishmentManager().warn(uuid, reason);
        Bukkit.broadcastMessage(plugin.getConfig().getString("messages.warn")
                .replace("%player%", name)
                .replace("%reason%", reason)
                .replace("%prefix%", plugin.getConfig().getString("prefix")));

        // Запись статистики
        if (s instanceof Player) {
            plugin.getPunishmentManager().recordStaffAction(((Player) s).getUniqueId(), "warn");
        } else {
            plugin.getPunishmentManager().recordStaffAction(new UUID(0, 0), "warn");
        }

        return true;
    }
}