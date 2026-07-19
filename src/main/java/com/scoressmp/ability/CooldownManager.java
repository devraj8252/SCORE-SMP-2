/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.scoressmp.ability;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class CooldownManager {
    private static final Map<UUID, Map<String, Long>> cooldowns = new HashMap<UUID, Map<String, Long>>();

    public static boolean isOnCooldown(Player player, String abilityName) {
        Map<String, Long> pCooldowns = cooldowns.get(player.getUniqueId());
        if (pCooldowns != null && pCooldowns.containsKey(abilityName)) {
            return System.currentTimeMillis() < pCooldowns.get(abilityName);
        }
        return false;
    }

    public static void setCooldown(Player player, String abilityName, int seconds) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap()).put(abilityName, System.currentTimeMillis() + (long)seconds * 1000L);
    }

    public static long getRemainingCooldown(Player player, String abilityName) {
        Map<String, Long> pCooldowns = cooldowns.get(player.getUniqueId());
        if (pCooldowns != null && pCooldowns.containsKey(abilityName)) {
            return (pCooldowns.get(abilityName) - System.currentTimeMillis()) / 1000L;
        }
        return 0L;
    }
}

