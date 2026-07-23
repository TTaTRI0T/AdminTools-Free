package com.example.admintools;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AdminToolsPlaceholders extends PlaceholderExpansion {
    private final Main plugin;
    public AdminToolsPlaceholders(Main p) { plugin = p; }

    @Override
    public @NotNull String getIdentifier() { return "admintools"; }
    @Override
    public @NotNull String getAuthor() { return "AdminTools"; }
    @Override
    public @NotNull String getVersion() { return "3.0.0"; }
    @Override
    public boolean persist() { return true; }

    @Override
    public String onPlaceholderRequest(Player p, @NotNull String id) {
        if (p == null) return "";
        PunishmentManager.PlayerData pd = plugin.getPunishmentManager().getPlayerData(p.getUniqueId());
        switch (id) {
            case "warns": return String.valueOf(pd.getWarns());
            case "muted": return plugin.getPunishmentManager().isMuted(p.getUniqueId()) ? "Да" : "Нет";
            case "banned": return pd.isBanned() ? "Да" : "Нет";
        }
        return null;
    }
}