package com.example.admintools;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatListener implements Listener {
    private final Main plugin;
    public ChatListener(Main p) { plugin = p; }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (plugin.getPunishmentManager().isMuted(p.getUniqueId())) {
            e.setCancelled(true);
            p.sendMessage("§cВы замучены и не можете писать в чат.");
            return;
        }
        if (plugin.getConfig().getBoolean("chat-filter.enabled", false)) {
            List<String> words = plugin.getConfig().getStringList("chat-filter.words");
            for (String word : words) {
                if (e.getMessage().toLowerCase().contains(word.toLowerCase())) {
                    e.setCancelled(true);
                    p.sendMessage("§cВаше сообщение заблокировано (недопустимые слова).");
                    String action = plugin.getConfig().getString("chat-filter.action", "mute 10m");
                    if (action.startsWith("mute")) {
                        String[] parts = action.split(" ");
                        long dur = plugin.getPunishmentManager().parseDurationPublic(parts.length > 1 ? parts[1] : "10m");
                        plugin.getPunishmentManager().mute(p.getUniqueId(), dur);
                    }
                    if (plugin.getConfig().getBoolean("chat-filter.notify-staff", true)) {
                        for (Player staff : Bukkit.getOnlinePlayers()) {
                            if (staff.hasPermission("admintools.staffchat")) {
                                staff.sendMessage("§c[Filter] §f" + p.getName() + " §7заблокирован за слово.");
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
}