package com.scoressmp.item;

import com.scoressmp.ScoresSMPPlugin;
import java.util.ArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class ScoreItemManager {
    public static final NamespacedKey SCORE_TYPE_KEY = new NamespacedKey((Plugin) ScoresSMPPlugin.getInstance(),
            "score_type");
    public static final NamespacedKey SCORE_LEVEL_KEY = new NamespacedKey((Plugin) ScoresSMPPlugin.getInstance(),
            "score_level");
    public static final NamespacedKey SCORE_PROGRESS_KEY = new NamespacedKey((Plugin) ScoresSMPPlugin.getInstance(),
            "score_progress");
    public static final NamespacedKey IS_TEMPLATE_KEY = new NamespacedKey((Plugin) ScoresSMPPlugin.getInstance(),
            "is_template");

    public static ItemStack createTemplate(ScoreType type) {
        Material baseMaterial = getBaseMaterial(type);
        ItemStack item = new ItemStack(baseMaterial);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.displayName(
                ((TextComponent) ((TextComponent) Component.text((String) (type.getDisplayName() + " Template"))
                        .color((TextColor) NamedTextColor.GOLD)).decorate(TextDecoration.BOLD))
                        .decoration(TextDecoration.ITALIC, false));
        ArrayList<Component> lore = new ArrayList<Component>();
        lore.add(((TextComponent) Component
                .text((String) ("A rare template used to craft the " + type.getDisplayName() + "."))
                .color((TextColor) NamedTextColor.GRAY)).decoration(TextDecoration.ITALIC, false));
        lore.add(((TextComponent) Component.text((String) "Combine this with other rare materials!")
                .color((TextColor) NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        meta.getPersistentDataContainer().set(SCORE_TYPE_KEY, PersistentDataType.STRING, type.name());
        meta.getPersistentDataContainer().set(IS_TEMPLATE_KEY, PersistentDataType.BYTE, (byte) 1);
        meta.setCustomModelData(getBaseModelData(type));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createScoreWeapon(ScoreType type, int level) {
        Material baseMaterial = getBaseMaterial(type);
        ItemStack item = new ItemStack(baseMaterial);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.displayName(((TextComponent) ((TextComponent) Component
                .text((String) (type.getDisplayName() + " [Lv " + level + "]"))
                .color((TextColor) NamedTextColor.LIGHT_PURPLE)).decorate(TextDecoration.BOLD))
                .decoration(TextDecoration.ITALIC, false));
        ArrayList<Component> lore = new ArrayList<Component>();
        lore.add(((TextComponent) Component.text((String) ("Level: " + level + "/3"))
                .color((TextColor) NamedTextColor.AQUA)).decoration(TextDecoration.ITALIC, false));
        if (level < 3) {
            lore.add(((TextComponent) Component
                    .text((String) ("Progress: 0/" + ScoreItemManager.getRequiredProgress(type, level)))
                    .color((TextColor) NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, false));
            lore.add(((TextComponent) Component.text((String) ScoreItemManager.getChallengeDescription(type, level))
                    .color((TextColor) NamedTextColor.GRAY)).decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(((TextComponent) ((TextComponent) Component.text((String) "MAX LEVEL")
                    .color((TextColor) NamedTextColor.GOLD)).decorate(TextDecoration.BOLD))
                    .decoration(TextDecoration.ITALIC, false));
        }
        lore.add(Component.empty());
        lore.add(((TextComponent) Component.text((String) "Abilities:").color((TextColor) NamedTextColor.GREEN))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(((TextComponent) Component.text((String) ("Left-Click: " + ScoreItemManager.getLeftClickAbility(type)))
                .color((TextColor) NamedTextColor.GRAY)).decoration(TextDecoration.ITALIC, false));
        lore.add(((TextComponent) Component
                .text((String) ("Right-Click: " + ScoreItemManager.getRightClickAbility(type)))
                .color((TextColor) NamedTextColor.GRAY)).decoration(TextDecoration.ITALIC, false));
        lore.add(((TextComponent) Component
                .text((String) ("Shift-Click: " + ScoreItemManager.getShiftClickAbility(type)))
                .color((TextColor) NamedTextColor.GRAY)).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        meta.getPersistentDataContainer().set(SCORE_TYPE_KEY, PersistentDataType.STRING, type.name());
        meta.getPersistentDataContainer().set(SCORE_LEVEL_KEY, PersistentDataType.INTEGER, level);
        meta.getPersistentDataContainer().set(SCORE_PROGRESS_KEY, PersistentDataType.INTEGER, 0);
        meta.setCustomModelData(getBaseModelData(type) + level);
        item.setItemMeta(meta);
        return item;
    }

    private static Material getBaseMaterial(ScoreType type) {
        return switch (type) {
            case FIRE -> Material.ORANGE_DYE;
            case DRAGON -> Material.MAGENTA_DYE;
            case MINE -> Material.BLACK_DYE;
            case PVP -> Material.YELLOW_DYE;
            case WARDEN -> Material.CYAN_DYE;
            case WATER -> Material.BLUE_DYE;
            case HONOR -> Material.GREEN_DYE;
        };
    }

    private static int getBaseModelData(ScoreType type) {
        return switch (type) {
            case FIRE -> 1000000;
            case DRAGON -> 2000000;
            case MINE -> 3000000;
            case PVP -> 4000000;
            case WARDEN -> 5000000;
            case WATER -> 6000000;
            case HONOR -> 7000000;
        };
    }

    public static boolean isScoreItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(SCORE_TYPE_KEY, PersistentDataType.STRING)
                && !item.getItemMeta().getPersistentDataContainer().has(IS_TEMPLATE_KEY, PersistentDataType.BYTE);
    }

    public static boolean isTemplateItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(IS_TEMPLATE_KEY, PersistentDataType.BYTE);
    }

    public static ScoreType getScoreType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        String typeStr = (String) item.getItemMeta().getPersistentDataContainer().get(SCORE_TYPE_KEY,
                PersistentDataType.STRING);
        if (typeStr == null) {
            return null;
        }
        try {
            return ScoreType.valueOf(typeStr);
        } catch (Exception e) {
            return null;
        }
    }

    public static int getScoreLevel(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return 0;
        }
        Integer level = (Integer) item.getItemMeta().getPersistentDataContainer().get(SCORE_LEVEL_KEY,
                PersistentDataType.INTEGER);
        return level == null ? 0 : level;
    }

    private static int getRequiredProgress(ScoreType type, int level) {
        return level == 1 ? 50 : 150;
    }

    private static String getChallengeDescription(ScoreType type, int level) {
        return switch (type) {
            case FIRE -> "Kill Nether Mobs.";
            case WATER -> "Kill Ocean Mobs.";
            case MINE -> "Mine Diamond Ore.";
            case DRAGON -> "Kill Endermen/End Mobs.";
            case PVP -> "Kill other Players.";
            case WARDEN -> "Kill Wardens or Shriekers.";
            case HONOR -> "";
        };
    }

    private static String getLeftClickAbility(ScoreType type) {
        return switch (type) {
            case FIRE -> "Fire Resistance";
            case WATER -> "Aqua Dash";
            case MINE -> "Haste";
            case DRAGON -> "Flight";
            case PVP -> "Strength";
            case WARDEN -> "Regeneration";
            case HONOR -> "Ultimate Buff";
        };
    }

    private static String getRightClickAbility(ScoreType type) {
        return switch (type) {
            case FIRE -> "Fireball";
            case WATER -> "Geyser";
            case MINE -> "TNT Cannon";
            case DRAGON -> "Dragon's Breath";
            case PVP -> "Giant Sword";
            case WARDEN -> "Sonic Boom";
            case HONOR -> "Arrow Swarm";
        };
    }

    private static String getShiftClickAbility(ScoreType type) {
        return switch (type) {
            case FIRE -> "Ignite Ring";
            case WATER -> "Water Shield";
            case MINE -> "Safe Blast";
            case DRAGON -> "Ground Trap";
            case PVP -> "Summon Minions";
            case WARDEN -> "Absorption Hearts";
            case HONOR -> "Shamak";
        };
    }
}
