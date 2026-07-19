package com.scoressmp.config;

import com.scoressmp.ScoresSMPPlugin;
import com.scoressmp.item.ScoreType;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.List;

public class ConfigManager {
    private static ScoresSMPPlugin plugin;
    private static FileConfiguration config;

    public static void load(ScoresSMPPlugin instance) {
        plugin = instance;
        config = plugin.getConfig();
    }

    public static void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public static String getDisplayName(ScoreType type) {
        String path = "items." + type.name() + ".display_name";
        return config.getString(path, getDefaultDisplayName(type));
    }

    public static Material getBaseMaterial(ScoreType type) {
        String path = "items." + type.name() + ".base_material";
        String matStr = config.getString(path);
        if (matStr != null) {
            try {
                return Material.valueOf(matStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        return getDefaultBaseMaterial(type);
    }

    public static int getBaseModelData(ScoreType type) {
        String path = "items." + type.name() + ".base_model_data";
        return config.getInt(path, getDefaultBaseModelData(type));
    }

    public static int getCooldown(ScoreType type, String action) {
        String path = "items." + type.name() + ".cooldowns." + action.toLowerCase();
        int def = switch (action.toLowerCase()) {
            case "left_click" -> 30;
            case "right_click" -> 15;
            case "shift_click" -> 60;
            default -> 0;
        };
        return config.getInt(path, def);
    }

    public static int getRequiredProgress(ScoreType type, int level) {
        String path = "items." + type.name() + ".progression.required_progress_lv" + level;
        int def = (level == 1) ? 50 : 150;
        return config.getInt(path, def);
    }

    public static String getChallengeDescription(ScoreType type) {
        String path = "items." + type.name() + ".progression.challenge_description";
        return config.getString(path, getDefaultChallengeDescription(type));
    }

    public static List<String> getChallengeEntities(ScoreType type) {
        String path = "items." + type.name() + ".progression.entities";
        return config.getStringList(path);
    }

    public static List<String> getChallengeBlocks(ScoreType type) {
        String path = "items." + type.name() + ".progression.blocks";
        return config.getStringList(path);
    }

    public static Material getRecipeItem(ScoreType type, String key) {
        String path = "items." + type.name() + ".recipe." + key.toLowerCase();
        String matStr = config.getString(path);
        if (matStr != null) {
            try {
                return Material.valueOf(matStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        return getDefaultRecipeItem(type, key);
    }

    public static double getLootStructureChance(ScoreType type, String structure) {
        String path = "items." + type.name() + ".loot.structures." + structure.toLowerCase();
        return config.getDouble(path, getDefaultLootStructureChance(type, structure));
    }

    public static double getLootEntityChance(ScoreType type, String entity) {
        String path = "items." + type.name() + ".loot.entities." + entity.toUpperCase();
        return config.getDouble(path, getDefaultLootEntityChance(type, entity));
    }

    // Ability-specific config value getters
    public static String getAbilityName(ScoreType type, String click) {
        String path = "items." + type.name() + ".abilities." + click.toLowerCase() + ".name";
        return config.getString(path, getDefaultAbilityName(type, click));
    }

    public static int getAbilityInt(ScoreType type, String click, String key, int def) {
        String path = "items." + type.name() + ".abilities." + click.toLowerCase() + "." + key.toLowerCase();
        return config.getInt(path, def);
    }

    public static double getAbilityDouble(ScoreType type, String click, String key, double def) {
        String path = "items." + type.name() + ".abilities." + click.toLowerCase() + "." + key.toLowerCase();
        return config.getDouble(path, def);
    }

    // Default Fallbacks
    private static String getDefaultDisplayName(ScoreType type) {
        return type.name().charAt(0) + type.name().substring(1).toLowerCase() + " Score";
    }

    private static Material getDefaultBaseMaterial(ScoreType type) {
        return switch (type) {
            case FIRE -> Material.ORANGE_DYE;
            case DRAGON -> Material.MAGENTA_DYE;
            case MINE -> Material.BLACK_DYE;
            case PVP -> Material.YELLOW_DYE;
            case WARDEN -> Material.CYAN_DYE;
            case WATER -> Material.BLUE_DYE;
            case HONOR -> Material.GREEN_DYE;
            case LUCK -> Material.LIME_DYE;
        };
    }

    private static int getDefaultBaseModelData(ScoreType type) {
        return switch (type) {
            case FIRE -> 1000000;
            case DRAGON -> 2000000;
            case MINE -> 3000000;
            case PVP -> 4000000;
            case WARDEN -> 5000000;
            case WATER -> 6000000;
            case HONOR -> 7000000;
            case LUCK -> 8000000;
        };
    }

    private static String getDefaultChallengeDescription(ScoreType type) {
        return switch (type) {
            case FIRE -> "Kill Nether Mobs.";
            case WATER -> "Kill Ocean Mobs.";
            case MINE -> "Mine Diamond Ore.";
            case DRAGON -> "Kill Endermen/End Mobs.";
            case PVP -> "Kill other Players.";
            case WARDEN -> "Kill Wardens or Shriekers.";
            case HONOR -> "";
            case LUCK -> "Kill Rabbits or Mine Emeralds.";
        };
    }

    private static Material getDefaultRecipeItem(ScoreType type, String key) {
        if ("item1".equalsIgnoreCase(key)) {
            return switch (type) {
                case FIRE -> Material.BLAZE_ROD;
                case WATER -> Material.HEART_OF_THE_SEA;
                case MINE -> Material.DIAMOND_BLOCK;
                case DRAGON -> Material.DRAGON_BREATH;
                case PVP -> Material.GOLDEN_APPLE;
                case WARDEN -> Material.ECHO_SHARD;
                case LUCK -> Material.ENCHANTED_GOLDEN_APPLE;
                default -> Material.AIR;
            };
        } else if ("item2".equalsIgnoreCase(key) || "item".equalsIgnoreCase(key)) {
            return switch (type) {
                case FIRE -> Material.MAGMA_CREAM;
                case WATER -> Material.PRISMARINE_CRYSTALS;
                case MINE -> Material.NETHERITE_INGOT;
                case DRAGON -> Material.END_CRYSTAL;
                case PVP -> Material.NETHERITE_SWORD;
                case WARDEN -> Material.SCULK_CATALYST;
                case LUCK -> Material.EMERALD_BLOCK;
                default -> Material.AIR;
            };
        } else if ("item3".equalsIgnoreCase(key)) {
            return switch (type) {
                case LUCK -> Material.DIAMOND_BLOCK;
                default -> Material.AIR;
            };
        } else if ("item4".equalsIgnoreCase(key)) {
            return switch (type) {
                case LUCK -> Material.TOTEM_OF_UNDYING;
                default -> Material.AIR;
            };
        }
        return Material.AIR;
    }

    private static double getDefaultLootStructureChance(ScoreType type, String structure) {
        return switch (type) {
            case FIRE -> 0.20; // bastion, ruined_portal, nether_bridge
            case WATER -> "buried_treasure".equalsIgnoreCase(structure) ? 1.0 : 0.25; // shipwreck
            case MINE -> 0.20; // abandoned_mineshaft, simple_dungeon
            case DRAGON -> 0.20; // end_city_treasure
            case WARDEN -> 0.20; // ancient_city
            case LUCK -> 0.15; // villages
            default -> 0.0;
        };
    }

    private static double getDefaultLootEntityChance(ScoreType type, String entity) {
        return switch (type) {
            case FIRE -> 0.05; // blaze, ghast, magma_cube, piglin
            case WATER -> "guardian".equalsIgnoreCase(entity) ? 0.25 : ("elder_guardian".equalsIgnoreCase(entity) ? 0.10 : 0.05); // drowned
            case DRAGON -> 0.50; // ender_dragon
            case PVP -> 0.05; // player
            case WARDEN -> 0.50; // warden
            case LUCK -> "rabbit".equalsIgnoreCase(entity) ? 0.10 : 0.0;
            default -> 0.0;
        };
    }

    private static String getDefaultAbilityName(ScoreType type, String click) {
        if ("left_click".equalsIgnoreCase(click)) {
            return switch (type) {
                case FIRE -> "Fire Resistance";
                case WATER -> "Aqua Dash";
                case MINE -> "Haste";
                case DRAGON -> "Flight";
                case PVP -> "Strength";
                case WARDEN -> "Regeneration";
                case HONOR -> "Ultimate Buff";
                case LUCK -> "Lucky Boost";
            };
        } else if ("right_click".equalsIgnoreCase(click)) {
            return switch (type) {
                case FIRE -> "Fireball";
                case WATER -> "Geyser";
                case MINE -> "TNT Cannon";
                case DRAGON -> "Dragon's Breath";
                case PVP -> "Giant Sword";
                case WARDEN -> "Sonic Boom";
                case HONOR -> "Arrow Swarm";
                case LUCK -> "Fate Roll";
            };
        } else {
            return switch (type) {
                case FIRE -> "Ignite Ring";
                case WATER -> "Water Shield";
                case MINE -> "Safe Blast";
                case DRAGON -> "Ground Trap";
                case PVP -> "Summon Minions";
                case WARDEN -> "Absorption Hearts";
                case HONOR -> "Shamak";
                case LUCK -> "Jackpot";
            };
        }
    }
}
