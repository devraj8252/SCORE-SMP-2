/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.scoressmp;

import com.scoressmp.ability.AbilityListener;
import com.scoressmp.command.ScoresCommand;
import com.scoressmp.crafting.ScoreRecipeManager;
import com.scoressmp.loot.LootManager;
import com.scoressmp.progression.ProgressionManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class ScoresSMPPlugin
extends JavaPlugin {
    private static ScoresSMPPlugin instance;

    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        com.scoressmp.config.ConfigManager.load(this);
        this.getLogger().info("ScoresSMP initializing...");
        this.getCommand("scoressmp").setExecutor((CommandExecutor)new ScoresCommand());
        this.getCommand("scoressmp").setTabCompleter((TabCompleter)new ScoresCommand());
        this.getServer().getPluginManager().registerEvents((Listener)new AbilityListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new ProgressionManager(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new LootManager(), (Plugin)this);
        ScoreRecipeManager.registerRecipes(this);
        this.getLogger().info("ScoresSMP is enabled!");
    }

    public void onDisable() {
        this.getLogger().info("ScoresSMP disabled.");
    }

    public static ScoresSMPPlugin getInstance() {
        return instance;
    }
}

