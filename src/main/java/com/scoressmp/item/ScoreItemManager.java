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
        meta.getPersistentDataContainer().set(SCORE_TYPE_KEY, PersistentDataType.STRING, type.name());
        meta.getPersistentDataContainer().set(SCORE_LEVEL_KEY, PersistentDataType.INTEGER, level);
        meta.setCustomModelData(getBaseModelData(type) + level);
        item.setItemMeta(meta);
        
        updateProgressLore(item, type, level, 0);
        return item;
    }

    public static void updateProgressLore(ItemStack item, ScoreType type, int level, int progress) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.getPersistentDataContainer().set(SCORE_PROGRESS_KEY, PersistentDataType.INTEGER, progress);
        
        ArrayList<Component> lore = new ArrayList<Component>();
        lore.add(((TextComponent) Component.text("Level: " + level + "/3")
                .color(NamedTextColor.AQUA)).decoration(TextDecoration.ITALIC, false));
        if (level < 3) {
            lore.add(((TextComponent) Component
                    .text("Progress: " + progress + "/" + ScoreItemManager.getRequiredProgress(type, level))
                    .color(NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, false));
            lore.add(((TextComponent) Component.text(ScoreItemManager.getChallengeDescription(type, level))
                    .color(NamedTextColor.GRAY)).decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(((TextComponent) ((TextComponent) Component.text("MAX LEVEL")
                    .color(NamedTextColor.GOLD)).decorate(TextDecoration.BOLD))
                    .decoration(TextDecoration.ITALIC, false));
        }
        lore.add(Component.empty());
        lore.add(((TextComponent) Component.text("Abilities:").color(NamedTextColor.GREEN))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(((TextComponent) Component.text("Left-Click: " + ScoreItemManager.getLeftClickAbility(type)))
                .color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(((TextComponent) Component.text("Right-Click: " + ScoreItemManager.getRightClickAbility(type)))
                .color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(((TextComponent) Component.text("Shift-Click: " + ScoreItemManager.getShiftClickAbility(type)))
                .color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        item.setItemMeta(meta);
    }

    private static Material getBaseMaterial(ScoreType type) {
        return com.scoressmp.config.ConfigManager.getBaseMaterial(type);
    }

    private static int getBaseModelData(ScoreType type) {
        return com.scoressmp.config.ConfigManager.getBaseModelData(type);
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
        return com.scoressmp.config.ConfigManager.getRequiredProgress(type, level);
    }

    private static String getChallengeDescription(ScoreType type, int level) {
        return com.scoressmp.config.ConfigManager.getChallengeDescription(type);
    }

    private static String getLeftClickAbility(ScoreType type) {
        return com.scoressmp.config.ConfigManager.getAbilityName(type, "left_click");
    }

    private static String getRightClickAbility(ScoreType type) {
        return com.scoressmp.config.ConfigManager.getAbilityName(type, "right_click");
    }

    private static String getShiftClickAbility(ScoreType type) {
        return com.scoressmp.config.ConfigManager.getAbilityName(type, "shift_click");
    }
}
