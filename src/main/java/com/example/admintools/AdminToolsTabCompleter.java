package com.example.admintools;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdminToolsTabCompleter implements TabCompleter {
    private final Main plugin;
    public AdminToolsTabCompleter(Main p) { plugin = p; }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(partial)) {
                    completions.add(player.getName());
                }
            }
            return completions;
        }
        if (command.getName().equalsIgnoreCase("tempban") || command.getName().equalsIgnoreCase("mute")) {
            if (args.length == 2) {
                String partial = args[1].toLowerCase();
                String[] times = {"10s","1m","5m","10m","30m","1h","2h","6h","12h","1d","3d","7d"};
                for (String time : times) {
                    if (time.startsWith(partial)) completions.add(time);
                }
                return completions;
            }
        }
        return completions;
    }
}