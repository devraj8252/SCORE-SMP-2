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
    public static final java.util.Set<java.util.UUID> activeJackpots = new java.util.HashSet<>();

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
                if (level == 2) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 0));
                    p.sendMessage(String.valueOf(ChatColor.GOLD) + "Fire Resistance and Speed applied!");
                } else if (level >= 3) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 0));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, 0));
                    p.sendMessage(String.valueOf(ChatColor.GOLD) + "Fire Resistance, Speed, and Regeneration applied!");
                } else {
                    p.sendMessage(String.valueOf(ChatColor.GOLD) + "Fire Resistance applied!");
                }
                break;
            }
            case WATER: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, duration, 0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, duration, 0));
                double baseVel = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "left_click", "velocity_multiplier_base", 2.0);
                double perLevelVel = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "left_click", "velocity_multiplier_per_level", 0.5);
                p.setVelocity(p.getLocation().getDirection().multiply(baseVel + (level * perLevelVel)));
                
                if (level == 2) {
                    p.setFireTicks(0);
                    p.sendMessage(String.valueOf(ChatColor.AQUA) + "Aqua Dash! Extinguished fires.");
                } else if (level >= 3) {
                    p.setFireTicks(0);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, duration, 0));
                    p.sendMessage(String.valueOf(ChatColor.AQUA) + "Aqua Dash! Extinguished fires and gained Conduit Power.");
                } else {
                    p.sendMessage(String.valueOf(ChatColor.AQUA) + "Aqua Dash!");
                }
                break;
            }
            case MINE: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, duration, level - 1));
                if (level == 2) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 0));
                    p.sendMessage(String.valueOf(ChatColor.YELLOW) + "Haste II and Speed applied!");
                } else if (level >= 3) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 0));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, duration, 0));
                    p.sendMessage(String.valueOf(ChatColor.YELLOW) + "Haste III, Speed, and Night Vision applied!");
                } else {
                    p.sendMessage(String.valueOf(ChatColor.YELLOW) + "Haste applied!");
                }
                break;
            }
            case DRAGON: {
                p.setAllowFlight(true);
                p.setFlying(true);
                
                if (level == 2) {
                    p.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE) + "Flight enabled (Level 2)! Will gain Slow Falling on exit.");
                } else if (level >= 3) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 1));
                    p.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE) + "Flight and Speed II enabled (Level 3)! Will gain Slow Falling on exit.");
                } else {
                    p.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE) + "Flight enabled temporarily!");
                }

                // Auto-disable flight
                plugin.getServer().getScheduler().runTaskLater((org.bukkit.plugin.Plugin) plugin, () -> {
                    if (p.isOnline() && p.getAllowFlight()) {
                        p.sendMessage(String.valueOf(ChatColor.RED) + "Flight ending in 3 seconds!");
                        plugin.getServer().getScheduler().runTaskLater((org.bukkit.plugin.Plugin) plugin, () -> {
                            if (p.isOnline()) {
                                p.setAllowFlight(false);
                                p.setFlying(false);
                                p.sendMessage(String.valueOf(ChatColor.DARK_RED) + "Flight disabled!");
                                if (level >= 2) {
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 100, 0));
                                    p.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE) + "Slow Falling applied.");
                                }
                            }
                        }, 60L);
                    }
                }, (long) duration - 60L);
                break;
            }
            case PVP: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, level - 1));
                p.sendMessage(String.valueOf(ChatColor.RED) + "Strength " + (level == 1 ? "I" : (level == 2 ? "II" : "III")) + " applied!");
                break;
            }
            case WARDEN: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, level - 1));
                if (level >= 3) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 0));
                    p.sendMessage(String.valueOf(ChatColor.DARK_GREEN) + "Regeneration II and Resistance I applied!");
                } else {
                    p.sendMessage(String.valueOf(ChatColor.DARK_GREEN) + "Regeneration " + (level == 1 ? "I" : "II") + " applied!");
                }
                break;
            }
            case HONOR: {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 1));
                if (level == 2) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, 0));
                    p.sendMessage(String.valueOf(ChatColor.DARK_AQUA) + "Ultimate Buff + Fire Resistance applied!");
                } else if (level >= 3) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, 0));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, duration, 1));
                    p.sendMessage(String.valueOf(ChatColor.DARK_AQUA) + "Ultimate Buff + Fire Resistance + Haste II applied!");
                } else {
                    p.sendMessage(String.valueOf(ChatColor.DARK_AQUA) + "Ultimate Buff applied!");
                }
                break;
            }
            case LUCK: {
                int speedAmp = (level >= 2) ? 1 : 0;
                p.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, duration, level - 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, speedAmp));
                
                if (level == 2) {
                    if (new java.util.Random().nextDouble() < 0.10) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
                        p.sendMessage(String.valueOf(ChatColor.GREEN) + "Lucky Boost applied! You got bonus Regeneration!");
                    } else {
                        p.sendMessage(String.valueOf(ChatColor.GREEN) + "Lucky Boost applied!");
                    }
                } else if (level >= 3) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
                    if (new java.util.Random().nextDouble() < 0.20) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 0));
                        p.sendMessage(String.valueOf(ChatColor.GREEN) + "Lucky Boost applied! Gained Regeneration and bonus Resistance!");
                    } else {
                        p.sendMessage(String.valueOf(ChatColor.GREEN) + "Lucky Boost applied! Gained Regeneration!");
                    }
                } else {
                    p.sendMessage(String.valueOf(ChatColor.GREEN) + "Lucky Boost applied!");
                }
                break;
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
                
                if (level == 2) {
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
                } else if (level >= 3) {
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
                    p.getServer().getScheduler().runTaskLater(plugin, () -> {
                        if (p.isOnline()) {
                            Fireball fb2 = (Fireball) p.launchProjectile(Fireball.class);
                            fb2.setYield((float) (baseYield + (float) level * perLevelYield));
                        }
                    }, 5L);
                }
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
                        LivingEntity le = (LivingEntity) ent;
                        le.damage(damage, (Entity) p);
                        le.setVelocity(new Vector(0, baseVelY + (level * perLevelVelY), 0));
                        if (level == 2) {
                            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0));
                        } else if (level >= 3) {
                            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
                        }
                    }
                });
                if (level >= 3) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
                }
                p.getWorld().spawnParticle(Particle.SPLASH, p.getLocation(), particleCount, 2.0, 2.0, 2.0);
                p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1.0f, 1.0f);
                break;
            }
            case MINE: {
                double velMul = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "velocity_multiplier", 1.5);
                int fuse = com.scoressmp.config.ConfigManager.getAbilityInt(type, "right_click", "fuse_ticks", 25);
                double yield = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "yield", 4.0);
                
                if (level >= 3) {
                    Vector dir = p.getLocation().getDirection();
                    for (int i = -1; i <= 1; ++i) {
                        TNTPrimed tnt = (TNTPrimed) p.getWorld().spawn(p.getEyeLocation(), TNTPrimed.class);
                        Vector velocity = dir.clone().rotateAroundY(Math.toRadians(i * 15)).multiply(velMul);
                        tnt.setVelocity(velocity);
                        tnt.setFuseTicks(fuse);
                        tnt.setYield((float) yield);
                    }
                } else {
                    TNTPrimed tnt = (TNTPrimed) p.getWorld().spawn(p.getEyeLocation(), TNTPrimed.class);
                    double actualVelMul = (level == 2) ? velMul * 1.3 : velMul;
                    tnt.setVelocity(p.getLocation().getDirection().multiply(actualVelMul));
                    tnt.setFuseTicks(fuse);
                    tnt.setYield((float) yield);
                }
                break;
            }
            case DRAGON: {
                double velMul = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "velocity_multiplier", 1.5);
                double yield = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "yield", 2.0);
                
                if (level >= 3) {
                    Vector dir = p.getLocation().getDirection();
                    for (int i = -1; i <= 1; ++i) {
                        DragonFireball dfb = (DragonFireball) p.launchProjectile(DragonFireball.class);
                        Vector velocity = dir.clone().rotateAroundY(Math.toRadians(i * 15)).multiply(velMul);
                        dfb.setVelocity(velocity);
                        dfb.setYield((float) yield);
                    }
                } else {
                    DragonFireball dfb = (DragonFireball) p.launchProjectile(DragonFireball.class);
                    dfb.setVelocity(p.getLocation().getDirection().multiply(velMul));
                    dfb.setYield((float) yield);
                    if (level == 2) {
                        p.getWorld().spawnParticle(Particle.DRAGON_BREATH, p.getLocation().add(p.getLocation().getDirection().multiply(2)), 40, 1.0, 1.0, 1.0, 0.1);
                    }
                }
                break;
            }
            case PVP: {
                double range = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "range_per_level", 3.0) * level;
                double damage = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "damage_per_level", 7.0) * level;
                double actualDamage = (level >= 3) ? damage * 1.5 : damage;

                p.getWorld().getNearbyEntities(p.getLocation().add(p.getLocation().getDirection().multiply(3)), range, range, range).forEach(ent -> {
                    if (ent instanceof LivingEntity && ent != p) {
                        LivingEntity le = (LivingEntity) ent;
                        le.damage(actualDamage, (Entity) p);
                        if (level == 2) {
                            le.setVelocity(le.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(1.0).setY(0.3));
                        } else if (level >= 3) {
                            le.setVelocity(le.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(1.5).setY(0.4));
                            try {
                                le.getWorld().spawnParticle(Particle.valueOf("CRIT"), le.getLocation(), 20, 0.3, 0.3, 0.3, 0.1);
                            } catch (Exception ignored) {}
                        }
                    }
                });
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);

                // Spawn the visual giant sword (ItemDisplay)
                Location eye = p.getEyeLocation();
                Vector dir = eye.getDirection();
                Location spawnLoc = eye.clone().add(dir.clone().multiply(1.5));
                spawnLoc.setYaw(p.getLocation().getYaw());
                spawnLoc.setPitch(p.getLocation().getPitch());

                org.bukkit.entity.ItemDisplay swordDisplay = p.getWorld().spawn(spawnLoc, org.bukkit.entity.ItemDisplay.class, display -> {
                    display.setItemStack(new org.bukkit.inventory.ItemStack(org.bukkit.Material.NETHERITE_SWORD));
                    display.setBillboard(org.bukkit.entity.Display.Billboard.NONE);
                    display.setInterpolationDuration(1);
                    display.setInterpolationDelay(0);
                    
                    org.joml.Vector3f scale = new org.joml.Vector3f(5.0f, 5.0f, 5.0f);
                    org.joml.Quaternionf leftRot = new org.joml.Quaternionf()
                            .rotationXYZ((float) Math.toRadians(45), 0.0f, (float) Math.toRadians(-60));
                    display.setTransformation(new org.bukkit.util.Transformation(new org.joml.Vector3f(0, 0, 0), leftRot, scale, new org.joml.Quaternionf()));
                });

                new org.bukkit.scheduler.BukkitRunnable() {
                    int tick = 0;
                    @Override
                    public void run() {
                        if (!swordDisplay.isValid()) {
                            this.cancel();
                            return;
                        }
                        if (tick >= 5) {
                            swordDisplay.remove();
                            this.cancel();
                            return;
                        }
                        
                        float progress = (float) (tick + 1) / 5.0f; // 0.2 to 1.0
                        float zAngle = -60.0f + (progress * 120.0f); // swing from -60 to +60
                        float xAngle = 45.0f - (progress * 90.0f);   // swing from 45 to -45
                        
                        org.joml.Vector3f scale = new org.joml.Vector3f(5.0f, 5.0f, 5.0f);
                        org.joml.Quaternionf leftRot = new org.joml.Quaternionf()
                                .rotationXYZ((float) Math.toRadians(xAngle), 0.0f, (float) Math.toRadians(zAngle));
                        
                        swordDisplay.setTransformation(new org.bukkit.util.Transformation(new org.joml.Vector3f(0, 0, 0), leftRot, scale, new org.joml.Quaternionf()));
                        
                        Location particleLoc = swordDisplay.getLocation().clone().add(p.getLocation().getDirection().multiply(1.0));
                        swordDisplay.getWorld().spawnParticle(Particle.SWEEP_ATTACK, particleLoc, 1);
                        
                        tick++;
                    }
                }.runTaskTimer(plugin, 0L, 1L);
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
                                if (level == 2) {
                                    le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
                                } else if (level >= 3) {
                                    le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0));
                                    le.setVelocity(le.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(1.5).setY(0.4));
                                }
                            }
                        }
                    });
                }
                p.playSound(p.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 1.0f);
                break;
            }
            case HONOR: {
                int baseArrowCount = com.scoressmp.config.ConfigManager.getAbilityInt(type, "right_click", "arrows_per_level", 10) * level;
                int arrowCount = (level >= 3) ? baseArrowCount * 2 : baseArrowCount;
                double velMul = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "velocity_multiplier", 2.0);
                double damage = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "right_click", "damage_per_level", 2.0) * level;

                for (int i = 0; i < arrowCount; ++i) {
                    Arrow arrow = (Arrow) p.launchProjectile(Arrow.class);
                    arrow.setVelocity(p.getLocation().getDirection().multiply(velMul)
                            .add(Vector.getRandom().subtract(new Vector(0.5, 0.5, 0.5)).multiply(0.5)));
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    arrow.setDamage(damage);
                    if (level >= 2) {
                        arrow.setVisualFire(true);
                        arrow.setFireTicks(100);
                    }
                }
                p.playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
                break;
            }
            case LUCK: {
                int maxRoll = (level == 1) ? 4 : ((level == 2) ? 5 : 6);
                int roll = new java.util.Random().nextInt(maxRoll) + 1;
                int buffDuration = com.scoressmp.config.ConfigManager.getAbilityInt(type, "right_click", "buff_duration", 100);
                
                switch (roll) {
                    case 1: {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, buffDuration, 1));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, buffDuration, 1));
                        p.sendMessage(String.valueOf(ChatColor.GREEN) + "☘ Fate Roll: You rolled a 1! Gained health & nourishment.");
                        p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p.getLocation(), 30, 1.0, 1.0, 1.0, 0.1);
                        break;
                    }
                    case 2: {
                        int amount = com.scoressmp.config.ConfigManager.getAbilityInt(type, "right_click", "emerald_amount_per_level", 1) * level;
                        p.getInventory().addItem(new ItemStack(org.bukkit.Material.EMERALD, amount));
                        p.sendMessage(String.valueOf(ChatColor.GOLD) + "☘ Fate Roll: You rolled a 2! Emeralds spawned in your inventory.");
                        p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p.getLocation(), 30, 1.0, 1.0, 1.0, 0.1);
                        break;
                    }
                    case 3: {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, buffDuration, 1));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, buffDuration, 0));
                        p.sendMessage(String.valueOf(ChatColor.YELLOW) + "☘ Fate Roll: You rolled a 3! Shielded with Absorption & Resistance.");
                        p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p.getLocation(), 30, 1.0, 1.0, 1.0, 0.1);
                        break;
                    }
                    case 4: {
                        double range = 10.0;
                        org.bukkit.entity.Entity target = null;
                        double closestDist = Double.MAX_VALUE;
                        for (org.bukkit.entity.Entity ent : p.getNearbyEntities(range, range, range)) {
                            if (ent instanceof LivingEntity && ent != p) {
                                double dist = ent.getLocation().distance(p.getLocation());
                                if (dist < closestDist) {
                                    closestDist = dist;
                                    target = ent;
                                }
                            }
                        }
                        if (target != null) {
                            p.getWorld().strikeLightningEffect(target.getLocation());
                            ((LivingEntity) target).damage(6.0 * level, (Entity) p);
                            p.sendMessage(String.valueOf(ChatColor.AQUA) + "☘ Fate Roll: You rolled a 4! Lucky lightning struck a nearby enemy.");
                        } else {
                            p.sendMessage(String.valueOf(ChatColor.GREEN) + "☘ Fate Roll: You rolled a 4, but no enemies were nearby!");
                        }
                        break;
                    }
                    case 5: {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 200, 0));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
                        p.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE) + "☘ Fate Roll: You rolled a 5! A Miracle occurred! Gained Hero of the Village and Regeneration II.");
                        p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p.getLocation(), 50, 1.0, 1.0, 1.0, 0.1);
                        p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.0f, 1.0f);
                        break;
                    }
                    case 6: {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 300, 1));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 1));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 300, 0));
                        p.sendMessage(String.valueOf(ChatColor.GOLD) + "☘ Fate Roll: You rolled a 6! You rolled Death Defy! Shielded with totem-like energies.");
                        try {
                            p.getWorld().spawnParticle(Particle.valueOf("TOTEM_OF_UNDYING"), p.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
                        } catch (Exception ignored) {
                            p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
                        }
                        p.playSound(p.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);
                        break;
                    }
                }
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.2f);
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
                                if (level >= 3) {
                                    ent.setVelocity(ent.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1.2).setY(0.4));
                                }
                            }
                        });
                if (level == 2) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));
                    p.sendMessage(String.valueOf(ChatColor.GOLD) + "Ignited nearby enemies & gained Speed!");
                } else if (level >= 3) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
                    try {
                        p.getWorld().spawnParticle(Particle.valueOf("EXPLOSION"), p.getLocation(), 20, 1.0, 1.0, 1.0, 0.1);
                    } catch (Exception ignored) {}
                    p.sendMessage(String.valueOf(ChatColor.GOLD) + "Blast Ignited and knocked back nearby enemies!");
                } else {
                    p.sendMessage(String.valueOf(ChatColor.GOLD) + "Ignited nearby enemies!");
                }
                break;
            }
            case WARDEN: {
                int duration = com.scoressmp.config.ConfigManager.getAbilityInt(type, "shift_click", "absorption_duration_per_level", 400) * level;
                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration, level - 1));
                if (level >= 3) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 1));
                    p.sendMessage(String.valueOf(ChatColor.DARK_GREEN) + "Gained Absorption III & Resistance II!");
                } else if (level == 2) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 0));
                    p.sendMessage(String.valueOf(ChatColor.DARK_GREEN) + "Gained Absorption II & Resistance I!");
                } else {
                    p.sendMessage(String.valueOf(ChatColor.DARK_GREEN) + "Gained Absorption hearts!");
                }
                break;
            }
            case WATER: {
                int duration = com.scoressmp.config.ConfigManager.getAbilityInt(type, "shift_click", "resistance_duration_per_level", 200) * level;
                p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, level));
                if (level == 2) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 0));
                    p.sendMessage(String.valueOf(ChatColor.AQUA) + "Water Shield activated! Gained Resistance & Speed.");
                } else if (level >= 3) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 0));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, duration, 1));
                    p.sendMessage(String.valueOf(ChatColor.AQUA) + "Ultimate Water Shield! Gained Resistance, Speed & Dolphin's Grace.");
                } else {
                    p.sendMessage(String.valueOf(ChatColor.AQUA) + "Water Shield activated! Gained Resistance.");
                }
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
                if (level == 2) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 0));
                    p.sendMessage(String.valueOf(ChatColor.YELLOW) + "Safe Blast triggered! Gained Absorption shield.");
                } else if (level >= 3) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 1));
                    p.setVelocity(new Vector(0, 1.2, 0));
                    p.sendMessage(String.valueOf(ChatColor.YELLOW) + "Safe Blast Rocket Jump! Gained Absorption shield.");
                } else {
                    p.sendMessage(String.valueOf(ChatColor.YELLOW) + "Safe Blast triggered!");
                }
                break;
            }
            case DRAGON: {
                double range = com.scoressmp.config.ConfigManager.getAbilityDouble(type, "shift_click", "range_per_level", 5.0) * level;
                int duration = com.scoressmp.config.ConfigManager.getAbilityInt(type, "shift_click", "levitation_duration_per_level", 100) * level;

                p.getWorld().getNearbyEntities(p.getLocation(), range, range, range).forEach(ent -> {
                            if (ent instanceof LivingEntity && ent != p) {
                                LivingEntity le = (LivingEntity) ent;
                                le.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, duration, level));
                                if (level == 2) {
                                    le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration, 0));
                                } else if (level >= 3) {
                                    le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration, 1));
                                    le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0));
                                }
                            }
                        });
                if (level == 2) {
                    p.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE) + "Ground Trap: Levitating and Withering enemies.");
                } else if (level >= 3) {
                    p.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE) + "Ground Trap: Levitating, Withering, and Blinding enemies.");
                } else {
                    p.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE) + "Ground Trap activated! Enemies levitating.");
                }
                break;
            }
            case PVP: {
                int minionCount = com.scoressmp.config.ConfigManager.getAbilityInt(type, "shift_click", "minions_per_level", 2) * level;
                int duration = com.scoressmp.config.ConfigManager.getAbilityInt(type, "shift_click", "wolf_duration_per_level", 400) * level;

                for (int i = 0; i < minionCount; ++i) {
                    Wolf wolf = (Wolf) p.getWorld().spawn(p.getLocation(), Wolf.class);
                    wolf.setOwner((AnimalTamer) p);
                    if (level == 2) {
                        wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 1));
                        wolf.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, 1));
                        wolf.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 0));
                    } else if (level >= 3) {
                        wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 2));
                        wolf.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, 2));
                        wolf.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 1));
                    } else {
                        wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 0));
                    }
                }
                if (level >= 3) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration / 2, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration / 2, 1));
                    p.sendMessage(String.valueOf(ChatColor.RED) + "Legendary Minions summoned! Gained temporary Strength & Speed!");
                } else {
                    p.sendMessage(String.valueOf(ChatColor.RED) + "Minions summoned!");
                }
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
                                if (level == 2) {
                                    le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, blindness, 0));
                                } else if (level >= 3) {
                                    le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, blindness, 1));
                                    le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, blindness, 0));
                                }
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
            case LUCK: {
                int duration = com.scoressmp.config.ConfigManager.getAbilityInt(type, "shift_click", "duration_per_level", 200) * level;
                java.util.UUID uuid = p.getUniqueId();
                activeJackpots.add(uuid);
                
                String message = (level >= 3) 
                        ? "✦ JACKPOT ACTIVATED! Mob drops and XP are TRIPLED! ✦" 
                        : "✦ JACKPOT ACTIVATED! Mob drops and XP are DOUBLED! ✦";
                p.sendMessage(String.valueOf(ChatColor.GOLD) + message);
                p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                
                new org.bukkit.scheduler.BukkitRunnable() {
                    int elapsed = 0;
                    @Override
                    public void run() {
                        if (!p.isOnline() || !activeJackpots.contains(uuid) || elapsed >= duration) {
                            activeJackpots.remove(uuid);
                            if (p.isOnline()) {
                                p.sendMessage(String.valueOf(ChatColor.RED) + "✦ Jackpot has ended! ✦");
                            }
                            this.cancel();
                            return;
                        }
                        p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p.getLocation().add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0.05);
                        elapsed += 10;
                    }
                }.runTaskTimer(plugin, 0L, 10L);
                break;
            }
            default: {
                p.sendMessage(String.valueOf(ChatColor.GRAY) + "Shift-click ultimate logic placeholder triggered.");
            }
        }
        CooldownManager.setCooldown(p, ability, com.scoressmp.config.ConfigManager.getCooldown(type, "shift_click"));
    }

    @EventHandler
    public void onEntityDeath(org.bukkit.event.entity.EntityDeathEvent event) {
        LivingEntity dead = event.getEntity();
        Player killer = dead.getKiller();
        if (killer == null) {
            return;
        }
        if (activeJackpots.contains(killer.getUniqueId())) {
            ItemStack item = killer.getInventory().getItemInMainHand();
            int level = 1;
            if (ScoreItemManager.isScoreItem(item) && ScoreItemManager.getScoreType(item) == ScoreType.LUCK) {
                level = ScoreItemManager.getScoreLevel(item);
            }
            
            killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            int multiplier = (level >= 3) ? 3 : 2;
            event.getDrops().forEach(itemStack -> itemStack.setAmount(itemStack.getAmount() * multiplier));
            event.setDroppedExp(event.getDroppedExp() * multiplier);
            
            double gemstoneChance = (level == 2) ? 0.10 : ((level >= 3) ? 0.25 : 0.0);
            if (gemstoneChance > 0 && new java.util.Random().nextDouble() < gemstoneChance) {
                org.bukkit.Material[] gems = {org.bukkit.Material.EMERALD, org.bukkit.Material.DIAMOND, org.bukkit.Material.IRON_INGOT};
                org.bukkit.Material gem = gems[new java.util.Random().nextInt(gems.length)];
                event.getDrops().add(new ItemStack(gem, 1));
                killer.sendMessage(String.valueOf(ChatColor.GOLD) + "☘ Lucky drop! The mob dropped an extra " + gem.name() + "!");
            }
            
            dead.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, dead.getLocation(), 15, 0.5, 0.5, 0.5, 0.1);
        }
    }
}
