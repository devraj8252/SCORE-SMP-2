/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Sound
 *  org.bukkit.entity.Blaze
 *  org.bukkit.entity.Drowned
 *  org.bukkit.entity.EnderDragon
 *  org.bukkit.entity.Enderman
 *  org.bukkit.entity.Ghast
 *  org.bukkit.entity.Guardian
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.MagmaCube
 *  org.bukkit.entity.Piglin
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Warden
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.persistence.PersistentDataType
 */
package com.scoressmp.progression;

import com.scoressmp.ScoresSMPPlugin;
import com.scoressmp.item.ScoreItemManager;
import com.scoressmp.item.ScoreType;
import org.bukkit.Sound;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ProgressionManager
                implements Listener {
    private final ScoresSMPPlugin plugin;

    public ProgressionManager(ScoresSMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        ItemStack item = killer.getInventory().getItemInMainHand();
        if (!ScoreItemManager.isScoreItem(item)) {
            return;
        }
        ScoreType type = ScoreItemManager.getScoreType(item);
        if (type == null) {
            return;
        }
        int level = ScoreItemManager.getScoreLevel(item);
        if (level >= 3) {
            return;
        }
        LivingEntity dead = event.getEntity();
        boolean validKill = false;
        switch (type) {
            case FIRE: {
                if (!(dead instanceof Blaze) && !(dead instanceof Ghast) && !(dead instanceof MagmaCube)
                        
                    
                        && !(dead instanceof Piglin))
                    break;
                validKill = true;
                break;
            }
                    
            case WATER: {
                if (!(dead instanceof Guardian) && !(dead instanceof Drowned))
                    break;
                validKill = true;
                break;
                    
            }
            case DRAGON: {
                if (!(dead instanceof Enderman) && !(dead instanceof EnderDragon))
                    break;
                validKill = true;
                    
                break;
            }
            case PVP: {
                if (!(dead instanceof Player))
                    break;
                    
                validKill = true;
                break;
            }
            case WARDEN: {
                if (!(dead instanceof Warden))
                    break;
                validKill = true;
                break;
            }
        }
        if (validKill) {
            this.addProgress(killer, item, type, level, 1);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!ScoreItemManager.isScoreItem(item)) {
            return;
        }
        ScoreType type = ScoreItemManager.getScoreType(item);
        if (type != ScoreType.MINE) {
            return;
                
        }
        int level = ScoreItemManager.getScoreLevel(item);
        if (level >= 3) {
            return;
        }
        if (event.getBlock().getType().name().contains("DIAMOND_ORE")
                || event.getBlock().getType().name().contains("ANCIENT_DEBRIS")) {
            this.addProgress(player, item, type, level, 1);
        }
    }
 
                 
    private void addProgress(Player player, ItemStack item, ScoreType type, int level, int amount) {
        int required;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        int currentProgress = meta.getPersistentDataContainer().getOrDefault(ScoreItemManager.SCORE_PROGRESS_KEY,
                PersistentDataType.INTEGER, 0);
        required = level == 1 ? 50 : 150;
                     
        currentProgress += amount;
        if (currentProgress >= required) {
            int newLevel = level + 1;
 
            ItemStack upgradedItem = ScoreItemManager.createScoreWeapon(type, newLevel);
            player.getInventory().setItemInMainHand(upgradedItem);
        } else {
            meta.getPersistentDataContainer().set(ScoreItemManager.SCORE_PROGRESS_KEY, PersistentDataType.INTEGER,
                    currentProgress);
            item.setItemMeta(meta);
        }
    }
}
