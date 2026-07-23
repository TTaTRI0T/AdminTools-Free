package com.example.admintools;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {
    private final Main plugin;
    public KickCommand(Main p) { plugin = p; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!s.hasPermission("admintools.kick")) {
            s.sendMessage(plugin.getConfig().getString("messages.no-permission"));
            return true;
        }
        if (a.length < 1) {
            s.sendMessage("§cИспользуйте: /kick <игрок> [причина]");
            return true;
        }
        Player t = Bukkit.getPlayer(a[0]);
        if (t == null) {
            s.sendMessage(plugin.getConfig().getString("messages.player-not-found"));
            return true;
        }
        String reason = a.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(a, 1, a.length)) : "Не указана";
        t.kickPlayer("§cВы были кикнуты! Причина: " + reason);
        Bukkit.broadcastMessage(plugin.getConfig().getString("messages.kick")
                .replace("%player%", t.getName())
                .replace("%reason%", reason)
                .replace("%prefix%", plugin.getConfig().getString("prefix")));

        // Запись статистики
        if (s instanceof Player) {
            plugin.getPunishmentManager().recordStaffAction(((Player) s).getUniqueId(), "kick");
        } else {
            plugin.getPunishmentManager().recordStaffAction(new java.util.UUID(0, 0), "kick");
        }

        return true;
    }
}