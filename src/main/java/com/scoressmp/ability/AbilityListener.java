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

    public AbilityListener(ScoresSMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
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
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            this.handleLeftClick(player, type, level);
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            this.handleRightClick(player, type, level);
        }
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
        int duration = 200 * level;
        switch (type) {
            case FIRE: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, 0));
                p.sendMessage(String.valueOf(ChatColor.GOLD) + "Fire Resistance applied!");
                break;
            }
            case WATER: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, duration, 0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, duration, 0));
                p.setVelocity(p.getLocation().getDirection().multiply(2.0 + (level * 0.5)));
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
        CooldownManager.setCooldown(p, ability, 30);
    }

    private void handleRightClick(Player p, ScoreType type, int level) {
        String ability = "RightClick_" + type.name();
        if (CooldownManager.isOnCooldown(p, ability)) {
            return;
        }
        switch (type) {
            case FIRE: {
                Fireball fb = (Fireball) p.launchProjectile(Fireball.class);
                fb.setYield(1.5f + (float) level * 0.5f);
                break;
            }
            case WATER: {
                p.getWorld().getNearbyEntities(p.getLocation(), (double) (4 * level), (double) (4 * level),
                        (double) (4 * level)).forEach(ent -> {
                            if (ent instanceof LivingEntity && ent != p) {
                                ((LivingEntity) ent).damage(6.0 * (double) level, (Entity) p);
                                ent.setVelocity(new Vector(0, 1.5 + (level * 0.2), 0));
                            }
                        });
                p.getWorld().spawnParticle(Particle.SPLASH, p.getLocation(), 100 * level, 2.0, 2.0, 2.0);
                p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1.0f, 1.0f);
                break;
            }
            case MINE: {
                TNTPrimed tnt = (TNTPrimed) p.getWorld().spawn(p.getEyeLocation(), TNTPrimed.class);
                tnt.setVelocity(p.getLocation().getDirection().multiply(1.5));
                tnt.setFuseTicks(25);
                break;
            }
            case DRAGON: {
                DragonFireball dfb = (DragonFireball) p.launchProjectile(DragonFireball.class);
                dfb.setVelocity(p.getLocation().getDirection().multiply(1.5));
                break;
            }
            case PVP: {
                p.getWorld().getNearbyEntities(p.getLocation().add(p.getLocation().getDirection().multiply(3)),
                        (double) (3 * level), (double) (3 * level), (double) (3 * level)).forEach(ent -> {
                            if (ent instanceof LivingEntity && ent != p) {
                                ((LivingEntity) ent).damage(7.0 * (double) level, (Entity) p);
                            }
                        });
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
                break;
            }
            case WARDEN: {
                Location eye = p.getEyeLocation();
                Vector dir = eye.getDirection();
                for (int i = 0; i < 15 * level; ++i) {
                    Location point = eye.clone().add(dir.clone().multiply(i));
                    try {
                        p.getWorld().spawnParticle(Particle.valueOf((String) "SONIC_BOOM"), point, 1);
                    } catch (Exception ignored) {
                        p.getWorld().spawnParticle(Particle.SWEEP_ATTACK, point, 1);
                    }
                    p.getWorld().getNearbyEntities(point, 1.5, 1.5, 1.5).forEach(ent -> {
                        if (ent instanceof LivingEntity && ent != p) {
                            ((LivingEntity) ent).damage(8.0 * (double) level, (Entity) p);
                        }
                    });
                }
                p.playSound(p.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 1.0f);
                break;
            }
            case HONOR: {
                for (int i = 0; i < 10 * level; ++i) {
                    Arrow arrow = (Arrow) p.launchProjectile(Arrow.class);
                    arrow.setVelocity(p.getLocation().getDirection().multiply(2)
                            .add(Vector.getRandom().subtract(new Vector(0.5, 0.5, 0.5)).multiply(0.5)));
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                }
                p.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
                break;
            }
            default: {
                p.sendMessage(String.valueOf(ChatColor.GRAY) + "Ability specific complex logic placeholder triggered.");
            }
        }
        CooldownManager.setCooldown(p, ability, 15);
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
                p.getWorld().getNearbyEntities(loc, (double) (5 * level), (double) (5 * level), (double) (5 * level))
                        .forEach(ent -> {
                            if (ent != p && ent instanceof LivingEntity) {
                                ent.setFireTicks(100 * level);
                            }
                        });
                p.sendMessage(String.valueOf(ChatColor.GOLD) + "Ignited nearby enemies!");
                break;
            }
            case WARDEN: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 400 * level, level - 1));
                p.sendMessage(String.valueOf(ChatColor.DARK_GREEN) + "Gained Absorption hearts!");
                break;
            }
            case WATER: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200 * level, level));
                p.sendMessage(String.valueOf(ChatColor.AQUA) + "Water Shield activated! Gained Resistance.");
                break;
            }
            case MINE: {
                try {
                    p.getWorld().spawnParticle(Particle.valueOf((String) "EXPLOSION_HUGE"), p.getLocation(), level);
                } catch (Exception e) {
                    p.getWorld().spawnParticle(Particle.EXPLOSION, p.getLocation(), level);
                }
                p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                p.getWorld().getNearbyEntities(p.getLocation(), (double) (4 * level), (double) (4 * level),
                        (double) (4 * level)).forEach(ent -> {
                            if (ent instanceof LivingEntity && ent != p) {
                                ((LivingEntity) ent).damage(10.0 * (double) level, (Entity) p);
                            }
                        });
                p.sendMessage(String.valueOf(ChatColor.YELLOW) + "Safe Blast triggered!");
                break;
            }
            case DRAGON: {
                p.getWorld().getNearbyEntities(p.getLocation(), (double) (5 * level), (double) (5 * level),
                        (double) (5 * level)).forEach(ent -> {
                            if (ent instanceof LivingEntity && ent != p) {
                                ((LivingEntity) ent).addPotionEffect(
                                        new PotionEffect(PotionEffectType.LEVITATION, 100 * level, level));
                            }
                        });
                p.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE) + "Ground Trap activated! Enemies levitating.");
                break;
            }
            case PVP: {
                for (int i = 0; i < level * 2; ++i) {
                    Wolf wolf = (Wolf) p.getWorld().spawn(p.getLocation(), Wolf.class);
                    wolf.setOwner((AnimalTamer) p);
                    wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400 * level, 1));
                    wolf.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 400 * level, 1));
                }
                p.sendMessage(String.valueOf(ChatColor.RED) + "Minions summoned!");
                break;
            }
            case HONOR: {
                p.getWorld().getNearbyEntities(p.getLocation(), (double) (10 * level), (double) (10 * level),
                        (double) (10 * level)).forEach(ent -> {
                            if (ent instanceof LivingEntity && ent != p) {
                                LivingEntity le = (LivingEntity) ent;
                                le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200 * level, 0));
                                le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200 * level, level - 1));
                                le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200 * level, 0));
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
        CooldownManager.setCooldown(p, ability, 60);
    }
}
