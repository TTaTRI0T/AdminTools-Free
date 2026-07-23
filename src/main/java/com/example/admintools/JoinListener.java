package com.example.admintools;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final Main plugin;
    public JoinListener(Main p) { plugin = p; }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PunishmentManager pm = plugin.getPunishmentManager();
        // Проверка бана: если бан истёк – разбан
        if (pm.isBanned(p.getUniqueId())) {
            // isBanned уже проверяет срок, но для надёжности вызовем
            PunishmentManager.PlayerData pd = pm.getPlayerData(p.getUniqueId());
            if (pd.isBanned() && pd.getBanExpiry() > 0 && System.currentTimeMillis() > pd.getBanExpiry()) {
                pd.setBanned(false);
                pm.save();
                p.sendMessage("§aВаш бан истёк, добро пожаловать!");
            }
        }
        // Проверка мута
        if (pm.isMuted(p.getUniqueId())) {
            // muted уже проверяется по времени, ничего не делаем
        }
    }
}