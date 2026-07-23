package com.example.admintools;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MuteCommand implements CommandExecutor {
    private final Main plugin;
    public MuteCommand(Main p) { plugin = p; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!s.hasPermission("admintools.mute")) {
            s.sendMessage(plugin.getConfig().getString("messages.no-permission"));
            return true;
        }
        if (a.length < 2) {
            s.sendMessage("§cИспользуйте: /mute <игрок> <время> [причина]");
            return true;
        }
        String name = a[0];
        UUID uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
        long duration = plugin.getPunishmentManager().parseDurationPublic(a[1]);
        String reason = a.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(a, 2, a.length)) : "Не указана";
        plugin.getPunishmentManager().mute(uuid, duration);
        Bukkit.broadcastMessage(plugin.getConfig().getString("messages.mute")
                .replace("%player%", name)
                .replace("%time%", a[1])
                .replace("%reason%", reason)
                .replace("%prefix%", plugin.getConfig().getString("prefix")));

        // Запись статистики
        if (s instanceof Player) {
            plugin.getPunishmentManager().recordStaffAction(((Player) s).getUniqueId(), "mute");
        } else {
            plugin.getPunishmentManager().recordStaffAction(new UUID(0, 0), "mute");
        }

        return true;
    }
}