package com.example.admintools;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class Main extends JavaPlugin {
    private PunishmentManager pm;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!new File(getDataFolder(), "data.yml").exists()) saveResource("data.yml", false);

        pm = new PunishmentManager(this);

        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("tempban").setExecutor(new TempBanCommand(this));
        getCommand("unban").setExecutor(new UnbanCommand(this));
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("unmute").setExecutor(new UnmuteCommand(this));
        getCommand("warn").setExecutor(new WarnCommand(this));
        getCommand("check").setExecutor(new CheckCommand(this));
        getCommand("staffchat").setExecutor(new StaffChatCommand(this));
        getCommand("adminmenu").setExecutor(new AdminMenuCommand(this));

        // Tab-completer
        AdminToolsTabCompleter tabCompleter = new AdminToolsTabCompleter(this);
        getCommand("kick").setTabCompleter(tabCompleter);
        getCommand("ban").setTabCompleter(tabCompleter);
        getCommand("tempban").setTabCompleter(tabCompleter);
        getCommand("unban").setTabCompleter(tabCompleter);
        getCommand("mute").setTabCompleter(tabCompleter);
        getCommand("unmute").setTabCompleter(tabCompleter);
        getCommand("warn").setTabCompleter(tabCompleter);
        getCommand("check").setTabCompleter(tabCompleter);

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new AdminMenuGUI(this), this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)
            new AdminToolsPlaceholders(this).register();

        getLogger().info("AdminTools Lite v3 enabled!");
    }

    @Override
    public void onDisable() {
        if (pm != null) pm.save();
    }

    public PunishmentManager getPunishmentManager() { return pm; }
}