/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.Recipe
 *  org.bukkit.inventory.RecipeChoice
 *  org.bukkit.inventory.RecipeChoice$ExactChoice
 *  org.bukkit.inventory.RecipeChoice$MaterialChoice
 *  org.bukkit.inventory.ShapedRecipe
 *  org.bukkit.plugin.Plugin
 */
package com.scoressmp.crafting;

import com.scoressmp.ScoresSMPPlugin;
import com.scoressmp.item.ScoreItemManager;
import com.scoressmp.item.ScoreType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public class ScoreRecipeManager {
    public static void registerRecipes(ScoresSMPPlugin plugin) {
        for (ScoreType type : ScoreType.values()) {
            if (type == ScoreType.HONOR || type == ScoreType.LUCK) continue;
            Material item1 = com.scoressmp.config.ConfigManager.getRecipeItem(type, "item1");
            Material item2 = com.scoressmp.config.ConfigManager.getRecipeItem(type, "item2");
            if (item1 != null && item1 != Material.AIR && item2 != null && item2 != Material.AIR) {
                ScoreRecipeManager.registerRecipe(plugin, type, item1, item2);
            }
        }
        ScoreRecipeManager.registerHonorRecipe(plugin);
        ScoreRecipeManager.registerLuckRecipe(plugin);
    }

    private static void registerLuckRecipe(ScoresSMPPlugin plugin) {
        NamespacedKey key = new NamespacedKey((Plugin)plugin, "luck_recipe");
        ItemStack result = ScoreItemManager.createScoreWeapon(ScoreType.LUCK, 1);
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(new String[]{" E ", "MTD", " U "});
        
        Material item1 = com.scoressmp.config.ConfigManager.getRecipeItem(ScoreType.LUCK, "item1");
        Material item2 = com.scoressmp.config.ConfigManager.getRecipeItem(ScoreType.LUCK, "item2");
        Material item3 = com.scoressmp.config.ConfigManager.getRecipeItem(ScoreType.LUCK, "item3");
        Material item4 = com.scoressmp.config.ConfigManager.getRecipeItem(ScoreType.LUCK, "item4");
        
        recipe.setIngredient('E', (RecipeChoice)new RecipeChoice.MaterialChoice(item1));
        recipe.setIngredient('M', (RecipeChoice)new RecipeChoice.MaterialChoice(item2));
        recipe.setIngredient('T', (RecipeChoice)new RecipeChoice.ExactChoice(ScoreItemManager.createTemplate(ScoreType.LUCK)));
        recipe.setIngredient('D', (RecipeChoice)new RecipeChoice.MaterialChoice(item3));
        recipe.setIngredient('U', (RecipeChoice)new RecipeChoice.MaterialChoice(item4));
        
        Bukkit.addRecipe((Recipe)recipe);
    }

    private static void registerRecipe(ScoresSMPPlugin plugin, ScoreType type, Material item1, Material item2) {
        NamespacedKey key = new NamespacedKey((Plugin)plugin, type.name().toLowerCase() + "_recipe");
        ItemStack result = ScoreItemManager.createScoreWeapon(type, 1);
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(new String[]{" I ", " T ", " M "});
        recipe.setIngredient('I', (RecipeChoice)new RecipeChoice.MaterialChoice(item1));
        recipe.setIngredient('T', (RecipeChoice)new RecipeChoice.ExactChoice(ScoreItemManager.createTemplate(type)));
        recipe.setIngredient('M', (RecipeChoice)new RecipeChoice.MaterialChoice(item2));
        Bukkit.addRecipe((Recipe)recipe);
    }

    private static void registerHonorRecipe(ScoresSMPPlugin plugin) {
        NamespacedKey key = new NamespacedKey((Plugin)plugin, "honor_recipe");
        ItemStack result = ScoreItemManager.createScoreWeapon(ScoreType.HONOR, 1);
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(new String[]{"AD ", "MW ", "PF "});
        recipe.setIngredient('A', (RecipeChoice)new RecipeChoice.ExactChoice(ScoreItemManager.createScoreWeapon(ScoreType.WARDEN, 3)));
        recipe.setIngredient('D', (RecipeChoice)new RecipeChoice.ExactChoice(ScoreItemManager.createScoreWeapon(ScoreType.DRAGON, 3)));
        recipe.setIngredient('M', (RecipeChoice)new RecipeChoice.ExactChoice(ScoreItemManager.createScoreWeapon(ScoreType.MINE, 3)));
        recipe.setIngredient('W', (RecipeChoice)new RecipeChoice.ExactChoice(ScoreItemManager.createScoreWeapon(ScoreType.WATER, 3)));
        recipe.setIngredient('P', (RecipeChoice)new RecipeChoice.ExactChoice(ScoreItemManager.createScoreWeapon(ScoreType.PVP, 3)));
        recipe.setIngredient('F', (RecipeChoice)new RecipeChoice.ExactChoice(ScoreItemManager.createScoreWeapon(ScoreType.FIRE, 3)));
        Bukkit.addRecipe((Recipe)recipe);
    }
}

