package com.example.admintools;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.*;

public class PunishmentManager {
    private final Main plugin;
    private final Map<UUID, PlayerData> playerData = new HashMap<>();
    private final Map<UUID, Long> mutedUntil = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> staffStats = new HashMap<>(); // staff UUID -> (type -> count)
    private File dataFile;
    private FileConfiguration dataConfig;
    private Logger logLogger;

    public PunishmentManager(Main plugin) {
        this.plugin = plugin;
        setupLogger();
        load();
    }

    private void setupLogger() {
        if (!plugin.getConfig().getBoolean("logging.enabled")) return;
        try {
            File logFile = new File(plugin.getDataFolder(), plugin.getConfig().getString("logging.file", "logs/admin.log"));
            logFile.getParentFile().mkdirs();
            FileHandler fh = new FileHandler(logFile.getPath(), true);
            fh.setFormatter(new SimpleFormatter());
            logLogger = Logger.getLogger("AdminTools");
            logLogger.addHandler(fh);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void log(String message) {
        if (logLogger != null) logLogger.info(message);
        plugin.getLogger().info(message);
    }

    public void load() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        playerData.clear();
        mutedUntil.clear();
        staffStats.clear();
        if (dataConfig.contains("players")) {
            for (String uuidStr : dataConfig.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                PlayerData pd = new PlayerData();
                pd.setWarns(dataConfig.getInt("players." + uuidStr + ".warns"));
                pd.setBanned(dataConfig.getBoolean("players." + uuidStr + ".banned"));
                pd.setBanReason(dataConfig.getString("players." + uuidStr + ".ban-reason", ""));
                pd.setBanExpiry(dataConfig.getLong("players." + uuidStr + ".ban-expiry", 0));
                if (dataConfig.contains("players." + uuidStr + ".muted-until")) {
                    long muted = dataConfig.getLong("players." + uuidStr + ".muted-until");
                    if (muted > System.currentTimeMillis()) {
                        mutedUntil.put(uuid, muted);
                        pd.setMuted(true);
                    }
                }
                playerData.put(uuid, pd);
            }
        }
        // Загрузка статистики персонала
        if (dataConfig.contains("staff-stats")) {
            for (String uuidStr : dataConfig.getConfigurationSection("staff-stats").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                Map<String, Integer> stats = new HashMap<>();
                ConfigurationSection sec = dataConfig.getConfigurationSection("staff-stats." + uuidStr);
                if (sec != null) {
                    for (String key : sec.getKeys(false)) {
                        stats.put(key, sec.getInt(key));
                    }
                }
                staffStats.put(uuid, stats);
            }
        }
    }

    public void save() {
        dataConfig.set("players", null);
        for (Map.Entry<UUID, PlayerData> entry : playerData.entrySet()) {
            String path = "players." + entry.getKey().toString();
            dataConfig.set(path + ".warns", entry.getValue().getWarns());
            dataConfig.set(path + ".banned", entry.getValue().isBanned());
            dataConfig.set(path + ".ban-reason", entry.getValue().getBanReason());
            dataConfig.set(path + ".ban-expiry", entry.getValue().getBanExpiry());
            if (mutedUntil.containsKey(entry.getKey())) {
                dataConfig.set(path + ".muted-until", mutedUntil.get(entry.getKey()));
            } else {
                dataConfig.set(path + ".muted-until", null);
            }
        }
        // Сохранение статистики
        dataConfig.set("staff-stats", null);
        for (Map.Entry<UUID, Map<String, Integer>> entry : staffStats.entrySet()) {
            String path = "staff-stats." + entry.getKey().toString();
            for (Map.Entry<String, Integer> stat : entry.getValue().entrySet()) {
                dataConfig.set(path + "." + stat.getKey(), stat.getValue());
            }
        }
        try { dataConfig.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerData.computeIfAbsent(uuid, k -> new PlayerData());
    }

    public boolean isBanned(UUID uuid) {
        PlayerData pd = playerData.get(uuid);
        if (pd == null) return false;
        if (pd.isBanned()) {
            if (pd.getBanExpiry() > 0 && System.currentTimeMillis() > pd.getBanExpiry()) {
                pd.setBanned(false);
                save();
                return false;
            }
            return true;
        }
        return false;
    }

    public void ban(UUID uuid, String reason, long duration) {
        PlayerData pd = getPlayerData(uuid);
        pd.setBanned(true);
        pd.setBanReason(reason);
        pd.setBanExpiry(duration > 0 ? System.currentTimeMillis() + duration : 0);
        if (Bukkit.getPlayer(uuid) != null) {
            Bukkit.getPlayer(uuid).kickPlayer("§cВы забанены! Причина: " + reason);
        }
        save();
        log("Ban: " + Bukkit.getOfflinePlayer(uuid).getName() + " (" + reason + ")");
    }

    public void unban(UUID uuid) {
        PlayerData pd = playerData.get(uuid);
        if (pd != null) {
            pd.setBanned(false);
            save();
            log("Unban: " + Bukkit.getOfflinePlayer(uuid).getName());
        }
    }

    public void mute(UUID uuid, long duration) {
        if (duration > 0) {
            mutedUntil.put(uuid, System.currentTimeMillis() + duration);
            getPlayerData(uuid).setMuted(true);
        }
        save();
        log("Mute: " + Bukkit.getOfflinePlayer(uuid).getName() + " (" + duration + "ms)");
    }

    public void unmute(UUID uuid) {
        mutedUntil.remove(uuid);
        PlayerData pd = playerData.get(uuid);
        if (pd != null) pd.setMuted(false);
        save();
        log("Unmute: " + Bukkit.getOfflinePlayer(uuid).getName());
    }

    public boolean isMuted(UUID uuid) {
        Long until = mutedUntil.get(uuid);
        if (until != null && System.currentTimeMillis() < until) return true;
        return false;
    }

    public long getMuteExpiry(UUID uuid) {
        return mutedUntil.getOrDefault(uuid, 0L);
    }

    public void warn(UUID uuid, String reason) {
        PlayerData pd = getPlayerData(uuid);
        pd.addWarn();
        save();
        log("Warn: " + Bukkit.getOfflinePlayer(uuid).getName() + " (" + reason + ")");
        ConfigurationSection warnActions = plugin.getConfig().getConfigurationSection("warn-actions");
        if (warnActions != null) {
            for (String threshold : warnActions.getKeys(false)) {
                int warnsNeeded = Integer.parseInt(threshold);
                if (pd.getWarns() == warnsNeeded) {
                    String action = warnActions.getString(threshold);
                    executeWarnAction(uuid, action);
                    break;
                }
            }
        } else {
            int maxWarns = plugin.getConfig().getInt("max-warnings", 5);
            if (pd.getWarns() >= maxWarns) {
                String action = plugin.getConfig().getString("warn-action", "ban");
                executeWarnAction(uuid, action);
            }
        }
    }

    private void executeWarnAction(UUID uuid, String action) {
        if (action.equals("ban")) {
            ban(uuid, "Достигнуто максимальное количество предупреждений", 0);
        } else if (action.startsWith("tempban")) {
            String[] parts = action.split(" ");
            long dur = parseDuration(parts.length > 1 ? parts[1] : "1d");
            ban(uuid, "Достигнуто максимальное количество предупреждений", dur);
        } else if (action.startsWith("mute")) {
            String[] parts = action.split(" ");
            long dur = parseDuration(parts.length > 1 ? parts[1] : "10m");
            mute(uuid, dur);
        } else if (action.equals("kick")) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.kickPlayer("§cВы кикнуты за многочисленные нарушения.");
        }
    }

    public long parseDurationPublic(String str) { return parseDuration(str); }

    private long parseDuration(String str) {
        long total = 0;
        String num = "";
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) num += c;
            else {
                int val = num.isEmpty() ? 1 : Integer.parseInt(num);
                switch (c) {
                    case 's': total += val * 1000; break;
                    case 'm': total += val * 60000; break;
                    case 'h': total += val * 3600000; break;
                    case 'd': total += val * 86400000; break;
                }
                num = "";
            }
        }
        return total;
    }

    // Статистика админов
    public void recordStaffAction(UUID staff, String type) {
        Map<String, Integer> stats = staffStats.computeIfAbsent(staff, k -> new HashMap<>());
        stats.put(type, stats.getOrDefault(type, 0) + 1);
        save();
    }

    public Map<UUID, Map<String, Integer>> getStaffStats() {
        return staffStats;
    }

    public Map<UUID, PlayerData> getAllData() { return playerData; }

    public static class PlayerData {
        private int warns = 0;
        private boolean banned = false;
        private String banReason = "";
        private long banExpiry = 0;
        private boolean muted = false;

        public int getWarns() { return warns; }
        public void setWarns(int w) { warns = w; }
        public void addWarn() { warns++; }
        public boolean isBanned() { return banned; }
        public void setBanned(boolean b) { banned = b; }
        public String getBanReason() { return banReason; }
        public void setBanReason(String r) { banReason = r; }
        public long getBanExpiry() { return banExpiry; }
        public void setBanExpiry(long e) { banExpiry = e; }
        public boolean isMuted() { return muted; }
        public void setMuted(boolean m) { muted = m; }
    }
}