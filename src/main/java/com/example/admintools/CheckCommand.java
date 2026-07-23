package com.example.admintools;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CheckCommand implements CommandExecutor {
    private final Main plugin;
    public CheckCommand(Main p) { plugin = p; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!s.hasPermission("admintools.check")) {
            s.sendMessage(plugin.getConfig().getString("messages.no-permission"));
            return true;
        }
        if (a.length < 1) {
            s.sendMessage("§cИспользуйте: /check <игрок>");
            return true;
        }
        UUID uuid = Bukkit.getOfflinePlayer(a[0]).getUniqueId();
        PunishmentManager.PlayerData pd = plugin.getPunishmentManager().getPlayerData(uuid);
        s.sendMessage("§6==== Информация о " + a[0] + " ====");
        s.sendMessage("§7Предупреждений: " + pd.getWarns());
        s.sendMessage("§7Забанен: " + (pd.isBanned() ? "§cДа (" + pd.getBanReason() + ")" : "§aНет"));
        s.sendMessage("§7Замучен: " + (plugin.getPunishmentManager().isMuted(uuid) ? "§cДа" : "§aНет"));
        return true;
    }
}