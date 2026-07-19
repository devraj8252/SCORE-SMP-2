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
        if (keyPath.contains("bastion") || keyPath.contains("ruined_portal") || keyPath.contains("nether_bridge")) {
            if (this.random.nextDouble() <= 0.2) {
                this.addTemplate(event, ScoreType.FIRE);
            }
        } else if (keyPath.equals(LootTables.BURIED_TREASURE.getKey().getKey())) {
            this.addTemplate(event, ScoreType.WATER);
        } else if (keyPath.contains("shipwreck")) {
            if (this.random.nextDouble() <= 0.25) {
                this.addTemplate(event, ScoreType.WATER);
            }
        } else if (keyPath.equals(LootTables.ABANDONED_MINESHAFT.getKey().getKey()) || keyPath.equals(LootTables.SIMPLE_DUNGEON.getKey().getKey())) {
            if (this.random.nextDouble() <= 0.2) {
                this.addTemplate(event, ScoreType.MINE);
            }
        } else if (keyPath.equals(LootTables.END_CITY_TREASURE.getKey().getKey())) {
            if (this.random.nextDouble() <= 0.2) {
                this.addTemplate(event, ScoreType.DRAGON);
            }
        } else if (keyPath.equals(LootTables.ANCIENT_CITY.getKey().getKey()) && this.random.nextDouble() <= 0.2) {
            this.addTemplate(event, ScoreType.WARDEN);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity dead = event.getEntity();
        if (dead instanceof Blaze || dead instanceof Ghast || dead instanceof MagmaCube || dead instanceof Piglin) {
            if (this.random.nextDouble() <= 0.05) {
                this.dropTemplate(event, ScoreType.FIRE);
            }
        } else if (dead instanceof Guardian) {
            if (this.random.nextDouble() <= 0.25) {
                this.dropTemplate(event, ScoreType.WATER);
            }
        } else if (dead instanceof ElderGuardian) {
            if (this.random.nextDouble() <= 0.1) {
                this.dropTemplate(event, ScoreType.WATER);
            }
        } else if (dead instanceof Drowned) {
            if (this.random.nextDouble() <= 0.05) {
                this.dropTemplate(event, ScoreType.WATER);
            }
        } else if (dead instanceof EnderDragon) {
            if (this.random.nextDouble() <= 0.5) {
                this.dropTemplate(event, ScoreType.DRAGON);
            }
        } else if (dead instanceof Player) {
            if (this.random.nextDouble() <= 0.05) {
                this.dropTemplate(event, ScoreType.PVP);
            }
        } else if (dead instanceof Warden && this.random.nextDouble() <= 0.5) {
            this.dropTemplate(event, ScoreType.WARDEN);
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

