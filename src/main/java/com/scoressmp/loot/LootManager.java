/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.NamespacedKey
 *  org.bukkit.entity.Blaze
 *  org.bukkit.entity.Drowned
 *  org.bukkit.entity.ElderGuardian
 *  org.bukkit.entity.EnderDragon
 *  org.bukkit.entity.Ghast
 *  org.bukkit.entity.Guardian
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.MagmaCube
 *  org.bukkit.entity.Piglin
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Warden
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.world.LootGenerateEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.loot.LootTables
 */
package com.scoressmp.loot;

import com.scoressmp.item.ScoreItemManager;
import com.scoressmp.item.ScoreType;
import java.util.Random;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;

public class LootManager
implements Listener {
    private final Random random = new Random();

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        NamespacedKey lootTableKey = event.getLootTable().getKey();
        String keyPath = lootTableKey.getKey();
        
        for (ScoreType type : ScoreType.values()) {
            if (type == ScoreType.HONOR) continue;
            String matchedStructure = getMatchedStructure(keyPath, type);
            if (matchedStructure != null) {
                double chance = com.scoressmp.config.ConfigManager.getLootStructureChance(type, matchedStructure);
                if (chance > 0 && this.random.nextDouble() <= chance) {
                    this.addTemplate(event, type);
                }
            }
        }
    }

    private String getMatchedStructure(String keyPath, ScoreType type) {
        String[] keys = switch(type) {
            case FIRE -> new String[]{"bastion", "ruined_portal", "nether_bridge"};
            case WATER -> new String[]{"buried_treasure", "shipwreck"};
            case MINE -> new String[]{"abandoned_mineshaft", "simple_dungeon"};
            case DRAGON -> new String[]{"end_city_treasure"};
            case WARDEN -> new String[]{"ancient_city"};
            case LUCK -> new String[]{"village"};
            default -> new String[0];
        };
        for (String k : keys) {
            if (keyPath.contains(k)) {
                return k;
            }
        }
        return null;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity dead = event.getEntity();
        String typeName = dead.getType().name();
        
        for (ScoreType type : ScoreType.values()) {
            if (type == ScoreType.HONOR) continue;
            double chance = com.scoressmp.config.ConfigManager.getLootEntityChance(type, typeName);
            if (chance > 0 && this.random.nextDouble() <= chance) {
                this.dropTemplate(event, type);
            }
        }
    }

    private void addTemplate(LootGenerateEvent event, ScoreType type) {
        ItemStack template = ScoreItemManager.createTemplate(type);
        event.getLoot().add(template);
    }

    private void dropTemplate(EntityDeathEvent event, ScoreType type) {
        ItemStack template = ScoreItemManager.createTemplate(type);
        event.getDrops().add(template);
    }
}

