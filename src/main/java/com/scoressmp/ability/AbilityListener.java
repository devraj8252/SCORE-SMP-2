/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Particle
 *  org.bukkit.Sound
 *  org.bukkit.entity.AbstractArrow$PickupStatus
 *  org.bukkit.entity.AnimalTamer
 *  org.bukkit.entity.Arrow
 *  org.bukkit.entity.DragonFireball
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Fireball
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.TNTPrimed
 *  org.bukkit.entity.Wolf
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerToggleSneakEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.util.Vector
 */
package com.scoressmp.ability;

import com.scoressmp.ScoresSMPPlugin;
import com.scoressmp.ability.CooldownManager;
import com.scoressmp.item.ScoreItemManager;
import com.scoressmp.item.ScoreType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class AbilityListener
        implements Listener {
    private final ScoresSMPPlugin plugin;
    private final java.util.Map<java.util.UUID, Long> lastRightClick = new java.util.HashMap<>();

    public AbilityListener(ScoresSMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() == org.bukkit.inventory.EquipmentSlot.OFF_HAND) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!ScoreItemManager.isScoreItem(item)) {
            return;
        }
        ScoreType type = ScoreItemManager.getScoreType(item);
        if (type == null) {
            return;
        }
        int level = ScoreItemManager.getScoreLevel(item);
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            this.handleRightClick(player, type, level);
        } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            this.handleLeftClick(player, type, level);
        }
    }

    @EventHandler
    public void onInteractEntity(org.bukkit.event.player.PlayerInteractEntityEvent event) {
        if (event.getHand() == org.bukkit.inventory.EquipmentSlot.OFF_HAND) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!ScoreItemManager.isScoreItem(item)) {
            return;
        }
        ScoreType type = ScoreItemManager.getScoreType(item);
        if (type == null) {
            return;
        }
        event.setCancelled(true);
        int level = ScoreItemManager.getScoreLevel(item);
        this.handleRightClick(player, type, level);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!ScoreItemManager.isScoreItem(item)) {
            return;
        }
        ScoreType type = ScoreItemManager.getScoreType(item);
        if (type == null) {
            return;
        }
        int level = ScoreItemManager.getScoreLevel(item);
        this.handleShiftClick(player, type, level);
    }

    private void handleLeftClick(Player p, ScoreType type, int level) {
        String ability = "LeftClick_" + type.name();
        if (CooldownManager.isOnCooldown(p, ability)) {
            p.sendMessage(String.valueOf(ChatColor.RED) + "Ability on cooldown: "
                    + CooldownManager.getRemainingCooldown(p, ability) + "s");
            return;
        }
        int duration = com.scoressmp.config.ConfigManager.getAbilityInt(type, "left_click", "duration_per_level", 200) * level;
        switch (type) {
            case FIRE: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, 0));
                p.sendMessage(String.valueOf(ChatColor.GOLD) + "Fire Resistance applied!");
                break;
            }
            case WATER: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, duration, 0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, duration, 0));
                double baseVel = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "left_click", "velocity_multiplier_base", 2.0);
                double perLevelVel = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "left_click", "velocity_multiplier_per_level", 0.5);
                p.setVelocity(p.getLocation().getDirection().multiply(baseVel + (level * perLevelVel)));
                p.sendMessage(String.valueOf(ChatColor.AQUA) + "Aqua Dash!");
                break;
            }
            case MINE: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, duration, level - 1));
                p.sendMessage(String.valueOf(ChatColor.YELLOW) + "Haste applied!");
                break;
            }
            case DRAGON: {
                p.setAllowFlight(true);
                p.setFlying(true);
                p.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE) + "Flight enabled temporarily!");

                // Auto-disable flight
                plugin.getServer().getScheduler().runTaskLater((org.bukkit.plugin.Plugin) plugin, () -> {
                    if (p.isOnline() && p.getAllowFlight()) {
                        p.sendMessage(String.valueOf(ChatColor.RED) + "Flight ending in 3 seconds!");
                        plugin.getServer().getScheduler().runTaskLater((org.bukkit.plugin.Plugin) plugin, () -> {
                            if (p.isOnline()) {
                                p.setAllowFlight(false);
                                p.setFlying(false);
                                p.sendMessage(String.valueOf(ChatColor.DARK_RED) + "Flight disabled!");
                            }
                        }, 60L);
                    }
                }, (long) duration - 60L);
                break;
            }
            case PVP: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, level - 1));
                p.sendMessage(String.valueOf(ChatColor.RED) + "Strength applied!");
                break;
            }
            case WARDEN: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, level - 1));
                p.sendMessage(String.valueOf(ChatColor.DARK_GREEN) + "Regeneration applied!");
                break;
            }
            case HONOR: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 1));
                p.sendMessage(String.valueOf(ChatColor.DARK_AQUA) + "Ultimate Buff applied!");
            }
        }
        p.playSound(p.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 1.0f);
        CooldownManager.setCooldown(p, ability, com.scoressmp.config.ConfigManager.getCooldown(type, "left_click"));
    }

    private void handleRightClick(Player p, ScoreType type, int level) {
        String ability = "RightClick_" + type.name();
        long now = System.currentTimeMillis();
        if (now - lastRightClick.getOrDefault(p.getUniqueId(), 0L) < 50) {
            return;
        }
        lastRightClick.put(p.getUniqueId(), now);

        if (CooldownManager.isOnCooldown(p, ability)) {
            p.sendMessage(String.valueOf(ChatColor.RED) + "Ability on cooldown: "
                    + CooldownManager.getRemainingCooldown(p, ability) + "s");
            return;
        }
        switch (type) {
            case FIRE: {
                Fireball fb = (Fireball) p.launchProjectile(Fireball.class);
                double baseYield = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "yield_base", 1.5);
                double perLevelYield = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "yield_per_level", 0.5);
                fb.setYield((float) (baseYield + (float) level * perLevelYield));
                break;
            }
            case WATER: {
                double range = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "range_per_level", 4.0) * level;
                double damage = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "damage_per_level", 6.0) * level;
                double baseVelY = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "velocity_y_base", 1.5);
                double perLevelVelY = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "velocity_y_per_level", 0.2);
                int particleCount = com.scoressmp.config.ConfigManager.getAbilityInt(type, "right_click", "particle_count_per_level", 100) * level;

                p.getWorld().getNearbyEntities(p.getLocation(), range, range, range).forEach(ent -> {
                    if (ent instanceof LivingEntity && ent != p) {
                        ((LivingEntity) ent).damage(damage, (Entity) p);
                        ent.setVelocity(new Vector(0, baseVelY + (level * perLevelVelY), 0));
                    }
                });
                p.getWorld().spawnParticle(Particle.SPLASH, p.getLocation(), particleCount, 2.0, 2.0, 2.0);
                p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1.0f, 1.0f);
                break;
            }
            case MINE: {
                TNTPrimed tnt = (TNTPrimed) p.getWorld().spawn(p.getEyeLocation(), TNTPrimed.class);
                double velMul = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "velocity_multiplier", 1.5);
                int fuse = com.scoressmp.config.ConfigManager.getAbilityInt(type, "right_click", "fuse_ticks", 25);
                double yield = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "yield", 4.0);
                tnt.setVelocity(p.getLocation().getDirection().multiply(velMul));
                tnt.setFuseTicks(fuse);
                tnt.setYield((float) yield);
                break;
            }
            case DRAGON: {
                DragonFireball dfb = (DragonFireball) p.launchProjectile(DragonFireball.class);
                double velMul = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "velocity_multiplier", 1.5);
                double yield = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "yield", 2.0);
                dfb.setVelocity(p.getLocation().getDirection().multiply(velMul));
                dfb.setYield((float) yield);
                break;
            }
            case PVP: {
                double range = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "range_per_level", 3.0) * level;
                double damage = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "damage_per_level", 7.0) * level;

                p.getWorld().getNearbyEntities(p.getLocation().add(p.getLocation().getDirection().multiply(3)), range, range, range).forEach(ent -> {
                    if (ent instanceof LivingEntity && ent != p) {
                        ((LivingEntity) ent).damage(damage, (Entity) p);
                    }
                });
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
                break;
            }
            case WARDEN: {
                Location eye = p.getEyeLocation();
                Vector dir = eye.getDirection();
                int range = com.scoressmp.config.ConfigManager.getAbilityInt(type, "right_click", "range_per_level", 15) * level;
                double damage = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "damage_per_level", 8.0) * level;
                java.util.Set<LivingEntity> damaged = new java.util.HashSet<>();

                for (int i = 0; i < range; ++i) {
                    Location point = eye.clone().add(dir.clone().multiply(i));
                    try {
                        p.getWorld().spawnParticle(Particle.valueOf((String) "SONIC_BOOM"), point, 1);
                    } catch (Exception ignored) {
                        p.getWorld().spawnParticle(Particle.SWEEP_ATTACK, point, 1);
                    }
                    p.getWorld().getNearbyEntities(point, 1.5, 1.5, 1.5).forEach(ent -> {
                        if (ent instanceof LivingEntity && ent != p) {
                            LivingEntity le = (LivingEntity) ent;
                            if (damaged.add(le)) {
                                le.damage(damage, (Entity) p);
                            }
                        }
                    });
                }
                p.playSound(p.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 1.0f);
                break;
            }
            case HONOR: {
                int arrowCount = com.scoressmp.config.ConfigManager.getAbilityInt(type, "right_click", "arrows_per_level", 10) * level;
                double velMul = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "velocity_multiplier", 2.0);
                double damage = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "damage_per_level", 2.0) * level;

                for (int i = 0; i < arrowCount; ++i) {
                    Arrow arrow = (Arrow) p.launchProjectile(Arrow.class);
                    arrow.setVelocity(p.getLocation().getDirection().multiply(velMul)
                            .add(Vector.getRandom().subtract(new Vector(0.5, 0.5, 0.5)).multiply(0.5)));
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    arrow.setDamage(damage);
                }
                p.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
                break;
            }
            default: {
                p.sendMessage(String.valueOf(ChatColor.GRAY) + "Ability specific complex logic placeholder triggered.");
            }
        }
        CooldownManager.setCooldown(p, ability, com.scoressmp.config.ConfigManager.getCooldown(type, "right_click"));
    }

    private void handleShiftClick(Player p, ScoreType type, int level) {
        String ability = "ShiftClick_" + type.name();
        if (CooldownManager.isOnCooldown(p, ability)) {
            p.sendMessage(String.valueOf(ChatColor.RED) + "Ultimate on cooldown: "
                    + CooldownManager.getRemainingCooldown(p, ability) + "s");
            return;
        }
        switch (type) {
            case FIRE: {
                Location loc = p.getLocation();
                double range = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "shift_click", "range_per_level", 5.0) * level;
                int fireTicks = com.scoressmp.config.ConfigManager.getAbilityInt(type, "shift_click", "fire_ticks_per_level", 100) * level;

                p.getWorld().getNearbyEntities(loc, range, range, range)
                        .forEach(ent -> {
                            if (ent != p && ent instanceof LivingEntity) {
                                ent.setFireTicks(fireTicks);
                            }
                        });
                p.sendMessage(String.valueOf(ChatColor.GOLD) + "Ignited nearby enemies!");
                break;
            }
            case WARDEN: {
                int duration = com.scoressmp.config.ConfigManager.getAbilityInt(type, "shift_click", "absorption_duration_per_level", 400) * level;
                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration, level - 1));
                p.sendMessage(String.valueOf(ChatColor.DARK_GREEN) + "Gained Absorption hearts!");
                break;
            }
            case WATER: {
                int duration = com.scoressmp.config.ConfigManager.getAbilityInt(type, "shift_click", "resistance_duration_per_level", 200) * level;
                p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, level));
                p.sendMessage(String.valueOf(ChatColor.AQUA) + "Water Shield activated! Gained Resistance.");
                break;
            }
            case MINE: {
                int particleCount = level;
                double range = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "shift_click", "range_per_level", 4.0) * level;
                double damage = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "shift_click", "damage_per_level", 10.0) * level;

                try {
                    p.getWorld().spawnParticle(Particle.valueOf((String) "EXPLOSION_HUGE"), p.getLocation(), particleCount);
                } catch (Exception e) {
                    p.getWorld().spawnParticle(Particle.EXPLOSION, p.getLocation(), particleCount);
                }
                p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                p.getWorld().getNearbyEntities(p.getLocation(), range, range, range).forEach(ent -> {
                            if (ent instanceof LivingEntity && ent != p) {
                                ((LivingEntity) ent).damage(damage, (Entity) p);
                            }
                        });
                p.sendMessage(String.valueOf(ChatColor.YELLOW) + "Safe Blast triggered!");
                break;
            }
            case DRAGON: {
                double range = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "shift_click", "range_per_level", 5.0) * level;
                int duration = com.scoressmp.config.ConfigManager.getAbilityInt(type, "shift_click", "levitation_duration_per_level", 100) * level;

                p.getWorld().getNearbyEntities(p.getLocation(), range, range, range).forEach(ent -> {
                            if (ent instanceof LivingEntity && ent != p) {
                                ((LivingEntity) ent).addPotionEffect(
                                        new PotionEffect(PotionEffectType.LEVITATION, duration, level));
                            }
                        });
                p.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE) + "Ground Trap activated! Enemies levitating.");
                break;
            }
            case PVP: {
                int minionCount = com.scoressmp.config.ConfigManager.getAbilityInt(type, "shift_click", "minions_per_level", 2) * level;
                int duration = com.scoressmp.config.ConfigManager.getAbilityInt(type, "shift_click", "wolf_duration_per_level", 400) * level;

                for (int i = 0; i < minionCount; ++i) {
                    Wolf wolf = (Wolf) p.getWorld().spawn(p.getLocation(), Wolf.class);
                    wolf.setOwner((AnimalTamer) p);
                    wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 1));
                    wolf.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, 1));
                }
                p.sendMessage(String.valueOf(ChatColor.RED) + "Minions summoned!");
                break;
            }
            case HONOR: {
                double range = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "shift_click", "range_per_level", 10.0) * level;
                int blindness = com.scoressmp.config.ConfigManager.getAbilityInt(type, "shift_click", "blindness_duration_per_level", 200) * level;
                int wither = com.scoressmp.config.ConfigManager.getAbilityInt(type, "shift_click", "wither_duration_per_level", 200) * level;

                p.getWorld().getNearbyEntities(p.getLocation(), range, range, range).forEach(ent -> {
                            if (ent instanceof LivingEntity && ent != p) {
                                LivingEntity le = (LivingEntity) ent;
                                le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindness, 0));
                                le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, wither, level - 1));
                                le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, blindness, 0));
                            }
                        });
                try {
                    p.getWorld().spawnParticle(Particle.valueOf((String) "SCULK_SOUL"), p.getLocation(), 50 * level,
                            5.0, 2.0, 5.0, 0.1);
                } catch (Exception exception) {
                    // empty catch block
                }
                p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
                p.sendMessage(String.valueOf(ChatColor.DARK_AQUA) + "Shamak activated! Enemies blinded and withered.");
                break;
            }
            default: {
                p.sendMessage(String.valueOf(ChatColor.GRAY) + "Shift-click ultimate logic placeholder triggered.");
            }
        }
        CooldownManager.setCooldown(p, ability, com.scoressmp.config.ConfigManager.getCooldown(type, "shift_click"));
    }
}
