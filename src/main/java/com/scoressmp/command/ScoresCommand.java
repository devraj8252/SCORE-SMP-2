/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.scoressmp.command;

import com.scoressmp.item.ScoreItemManager;
import com.scoressmp.item.ScoreType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ScoresCommand
        implements CommandExecutor,
        TabCompleter {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("scoressmp.admin")) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(String.valueOf(ChatColor.GOLD) + "ScoresSMP Admin Commands:");
            sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "/scoressmp give <player> <type> <level|template>");
            sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "/scoressmp reload");
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            com.scoressmp.config.ConfigManager.reload();
            sender.sendMessage(String.valueOf(ChatColor.GREEN) + "ScoresSMP configuration reloaded!");
            return true;
        }
        if (args[0].equalsIgnoreCase("give")) {
            ItemStack itemToGive;
            ScoreType type;
            if (args.length < 4) {
                sender.sendMessage(
                        String.valueOf(ChatColor.RED) + "Usage: /scoressmp give <player> <type> <level|template>");
                return true;
            }
            Player target = Bukkit.getPlayerExact((String) args[1]);
            if (target == null) {
                sender.sendMessage(String.valueOf(ChatColor.RED) + "Player not found!");
                return true;
            }
            try {
                type = ScoreType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(
                        String.valueOf(ChatColor.RED) + "Invalid Score Type! Use e.g., FIRE, WATER, DRAGON, etc.");
                return true;
            }
            String levelArg = args[3];
            if (levelArg.equalsIgnoreCase("template")) {
                if (type == ScoreType.HONOR) {
                    sender.sendMessage(String.valueOf(ChatColor.RED) + "The Honor score does not have a template!");
                    return true;
                }
                itemToGive = ScoreItemManager.createTemplate(type);
                sender.sendMessage(String.valueOf(ChatColor.GREEN) + "Gave " + target.getName() + " a "
                        + type.getDisplayName() + " Template.");
            } else {
                try {
                    int level = Integer.parseInt(levelArg);
                    if (level < 1 || level > 3) {
                        sender.sendMessage(String.valueOf(ChatColor.RED) + "Level must be between 1 and 3.");
                        return true;
                    }
                    itemToGive = ScoreItemManager.createScoreWeapon(type, level);
                    sender.sendMessage(String.valueOf(ChatColor.GREEN) + "Gave " + target.getName() + " a Level "
                            + level + " " + type.getDisplayName() + ".");
                } catch (NumberFormatException e) {
                    sender.sendMessage(String.valueOf(ChatColor.RED) + "Invalid level specified.");
                    return true;
                }
            }
            target.getInventory().addItem(new ItemStack[] { itemToGive });
            return true;
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (!sender.hasPermission("scoressmp.admin")) {
            return completions;
        }
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("give", "reload");
            completions.addAll(
                    subCommands.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList()));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                    continue;
                completions.add(p.getName());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            List<String> types = Arrays.stream(ScoreType.values()).map(Enum::name).collect(Collectors.toList());
            completions.addAll(types.stream().filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList()));
        } else if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            List<String> levels = Arrays.asList("1", "2", "3", "template");
            completions.addAll(
                    levels.stream().filter(s -> s.startsWith(args[3].toLowerCase())).collect(Collectors.toList()));
        }
        return completions;
    }
}
