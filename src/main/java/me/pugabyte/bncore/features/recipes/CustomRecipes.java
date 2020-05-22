package me.pugabyte.bncore.features.recipes;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.HashMap;
import java.util.Map;

public class CustomRecipes {

	public static int amount = 0;
	public static Map<NamespacedKey, Recipe> recipes = new HashMap<>();

	public CustomRecipes() {
		Tasks.async(() -> {
			slabsToBlocks();
			quartsUncrafting();
			stoneBricksUncrafting();
			concretePowderDying();
			stainedGlassDying();
			stainedGlassPaneDying();
			terracottaDying();
			bedDying();
			setWoolUndyingRecipe();
			misc();
			BNCore.getInstance().getLogger().info("Registered " + amount + " new custom crafting recipes");
			BNCore.getInstance().getLogger().info(recipes.size() + " total custom recipes are loaded on the server");
		});
	}

	public static void addRecipe(Recipe recipe) {
		for (Recipe recipe1 : Bukkit.getServer().getRecipesFor(recipe.getResult())) {
			if (RecipeUtils.areEqual(recipe1, recipe)) return;
		}
		try {
			Bukkit.addRecipe(recipe);
			amount++;
		} catch (IllegalStateException duplicate) {
			BNCore.log(duplicate.getMessage());
		}
	}

	public ShapelessRecipe createSingleItemShapelessRecipe(Material inputItem, int requiredAmount, Material outputItem, int outputAmount) {
		NamespacedKey key = new NamespacedKey(BNCore.getInstance(), "custom_" + inputItem.name() + "_" + outputItem.name());
		ShapelessRecipe recipe = new ShapelessRecipe(key, new ItemStack(outputItem, outputAmount));
		recipe.addIngredient(requiredAmount, inputItem);
		recipes.put(key, recipe);
		return recipe;
	}

	public ShapelessRecipe createShapelessRecipe(RecipeChoice.MaterialChoice inputItem, Material dye, Material outputItem) {
		NamespacedKey key = new NamespacedKey(BNCore.getInstance(), "custom_" + outputItem.name() + "_color");
		ShapelessRecipe recipe = new ShapelessRecipe(key, new ItemStack(outputItem, 1));
		recipe.addIngredient(inputItem);
		recipe.addIngredient(dye);
		recipes.put(key, recipe);
		return recipe;
	}

	public ShapedRecipe createColorChangingRecipe(RecipeChoice.MaterialChoice inputItem, Material dye, Material outputItem) {
		NamespacedKey key = new NamespacedKey(BNCore.getInstance(), "custom_" + outputItem.name() + "_color");
		ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(outputItem, 8));
		recipe.shape("iii", "idi", "iii");
		recipe.setIngredient('i', inputItem);
		recipe.setIngredient('d', dye);
		recipes.put(key, recipe);
		return recipe;
	}

	public void misc() {
		addRecipe(createSingleItemShapelessRecipe(Material.NETHER_WART_BLOCK, 1, Material.NETHER_WART, 9));
		addRecipe(createSingleItemShapelessRecipe(Material.BLUE_ICE, 1, Material.PACKED_ICE, 9));
		addRecipe(createSingleItemShapelessRecipe(Material.CHISELED_RED_SANDSTONE, 1, Material.RED_SANDSTONE_SLAB, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.CHISELED_SANDSTONE, 1, Material.SANDSTONE_SLAB, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.HONEYCOMB_BLOCK, 1, Material.HONEYCOMB, 4));
	}

	public void slabsToBlocks() {
		addRecipe(createSingleItemShapelessRecipe(Material.OAK_SLAB, 4, Material.OAK_PLANKS, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.SPRUCE_SLAB, 4, Material.SPRUCE_PLANKS, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.BIRCH_SLAB, 4, Material.BIRCH_PLANKS, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.JUNGLE_SLAB, 4, Material.JUNGLE_PLANKS, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.ACACIA_SLAB, 4, Material.ACACIA_PLANKS, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.DARK_OAK_SLAB, 4, Material.DARK_OAK_PLANKS, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.STONE_SLAB, 4, Material.STONE, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.SANDSTONE_SLAB, 4, Material.SANDSTONE, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.COBBLESTONE_SLAB, 4, Material.COBBLESTONE, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.BRICK_SLAB, 4, Material.BRICKS, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.STONE_BRICK_SLAB, 4, Material.STONE_BRICKS, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.NETHER_BRICK_SLAB, 4, Material.NETHER_BRICKS, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.QUARTZ_SLAB, 4, Material.QUARTZ_BLOCK, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.RED_SANDSTONE_SLAB, 4, Material.RED_SANDSTONE, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.PURPUR_SLAB, 4, Material.PURPUR_BLOCK, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.PRISMARINE_SLAB, 4, Material.PRISMARINE, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.PRISMARINE_BRICK_SLAB, 4, Material.PRISMARINE_BRICKS, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.DARK_PRISMARINE_SLAB, 4, Material.DARK_PRISMARINE, 2));
	}

	public void quartsUncrafting() {
		addRecipe(createSingleItemShapelessRecipe(Material.QUARTZ_BLOCK, 1, Material.QUARTZ, 4));
		addRecipe(createSingleItemShapelessRecipe(Material.QUARTZ_PILLAR, 1, Material.QUARTZ_BLOCK, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.CHISELED_QUARTZ_BLOCK, 1, Material.QUARTZ_SLAB, 2));
	}

	public void stoneBricksUncrafting() {
		addRecipe(createSingleItemShapelessRecipe(Material.CHISELED_STONE_BRICKS, 1, Material.STONE_BRICK_SLAB, 2));
		addRecipe(createSingleItemShapelessRecipe(Material.STONE_BRICKS, 4, Material.STONE, 4));
		addRecipe(createSingleItemShapelessRecipe(Material.MOSSY_STONE_BRICKS, 1, Material.STONE_BRICKS, 1));
	}

	RecipeChoice.MaterialChoice wool = new RecipeChoice.MaterialChoice(Material.WHITE_WOOL, Material.BLACK_WOOL, Material.BLUE_WOOL, Material.BROWN_WOOL,
			Material.CYAN_WOOL, Material.GREEN_WOOL, Material.GRAY_WOOL, Material.LIGHT_BLUE_WOOL, Material.LIGHT_GRAY_WOOL, Material.LIME_WOOL,
			Material.MAGENTA_WOOL, Material.ORANGE_WOOL, Material.PINK_WOOL, Material.PURPLE_WOOL, Material.RED_WOOL, Material.YELLOW_WOOL);

	RecipeChoice.MaterialChoice concretePowder = new RecipeChoice.MaterialChoice(Material.WHITE_CONCRETE_POWDER, Material.BLACK_CONCRETE_POWDER, Material.BLUE_CONCRETE_POWDER, Material.BROWN_CONCRETE_POWDER,
			Material.CYAN_CONCRETE_POWDER, Material.GREEN_CONCRETE_POWDER, Material.GRAY_CONCRETE_POWDER, Material.LIGHT_BLUE_CONCRETE_POWDER, Material.LIGHT_GRAY_CONCRETE_POWDER, Material.LIME_CONCRETE_POWDER,
			Material.MAGENTA_CONCRETE_POWDER, Material.ORANGE_CONCRETE_POWDER, Material.PINK_CONCRETE_POWDER, Material.PURPLE_CONCRETE_POWDER, Material.RED_CONCRETE_POWDER, Material.YELLOW_CONCRETE_POWDER);

	RecipeChoice.MaterialChoice stainedGlass = new RecipeChoice.MaterialChoice(Material.WHITE_STAINED_GLASS, Material.BLACK_STAINED_GLASS, Material.BLUE_STAINED_GLASS, Material.BROWN_STAINED_GLASS,
			Material.CYAN_STAINED_GLASS, Material.GREEN_STAINED_GLASS, Material.GRAY_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS, Material.LIME_STAINED_GLASS,
			Material.MAGENTA_STAINED_GLASS, Material.ORANGE_STAINED_GLASS, Material.PINK_STAINED_GLASS, Material.PURPLE_STAINED_GLASS, Material.RED_STAINED_GLASS, Material.YELLOW_STAINED_GLASS);

	RecipeChoice.MaterialChoice stainedGlassPane = new RecipeChoice.MaterialChoice(Material.WHITE_STAINED_GLASS_PANE, Material.BLACK_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE, Material.BROWN_STAINED_GLASS_PANE,
			Material.CYAN_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, Material.GRAY_STAINED_GLASS_PANE, Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.LIGHT_GRAY_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE,
			Material.MAGENTA_STAINED_GLASS_PANE, Material.ORANGE_STAINED_GLASS_PANE, Material.PINK_STAINED_GLASS_PANE, Material.PURPLE_STAINED_GLASS_PANE, Material.RED_STAINED_GLASS_PANE, Material.YELLOW_STAINED_GLASS_PANE);

	RecipeChoice.MaterialChoice terracotta = new RecipeChoice.MaterialChoice(Material.WHITE_TERRACOTTA, Material.BLACK_TERRACOTTA, Material.BLUE_TERRACOTTA, Material.BROWN_TERRACOTTA,
			Material.CYAN_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.GRAY_TERRACOTTA, Material.LIGHT_BLUE_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA, Material.LIME_TERRACOTTA,
			Material.MAGENTA_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.PINK_TERRACOTTA, Material.PURPLE_TERRACOTTA, Material.RED_TERRACOTTA, Material.YELLOW_TERRACOTTA);

	RecipeChoice.MaterialChoice bed = new RecipeChoice.MaterialChoice(Material.WHITE_BED, Material.BLACK_BED, Material.BLUE_BED, Material.BROWN_BED,
			Material.CYAN_BED, Material.GREEN_BED, Material.GRAY_BED, Material.LIGHT_BLUE_BED, Material.LIGHT_GRAY_BED, Material.LIME_BED,
			Material.MAGENTA_BED, Material.ORANGE_BED, Material.PINK_BED, Material.PURPLE_BED, Material.RED_BED, Material.YELLOW_BED);

	public void concretePowderDying() {
		addRecipe(createColorChangingRecipe(concretePowder, Material.WHITE_DYE, Material.WHITE_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.BLACK_DYE, Material.BLACK_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.BLUE_DYE, Material.BLUE_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.BROWN_DYE, Material.BROWN_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.CYAN_DYE, Material.CYAN_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.GREEN_DYE, Material.GREEN_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.GRAY_DYE, Material.GRAY_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.LIGHT_BLUE_DYE, Material.LIGHT_BLUE_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.LIGHT_GRAY_DYE, Material.LIGHT_GRAY_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.LIME_DYE, Material.LIME_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.MAGENTA_DYE, Material.MAGENTA_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.ORANGE_DYE, Material.ORANGE_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.PINK_DYE, Material.PINK_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.PURPLE_DYE, Material.PURPLE_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.RED_DYE, Material.RED_CONCRETE_POWDER));
		addRecipe(createColorChangingRecipe(concretePowder, Material.YELLOW_DYE, Material.YELLOW_CONCRETE_POWDER));
	}

	public void stainedGlassDying() {
		addRecipe(createColorChangingRecipe(stainedGlass, Material.WHITE_DYE, Material.WHITE_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.BLACK_DYE, Material.BLACK_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.BLUE_DYE, Material.BLUE_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.BROWN_DYE, Material.BROWN_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.CYAN_DYE, Material.CYAN_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.GREEN_DYE, Material.GREEN_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.GRAY_DYE, Material.GRAY_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.LIGHT_BLUE_DYE, Material.LIGHT_BLUE_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.LIGHT_GRAY_DYE, Material.LIGHT_GRAY_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.LIME_DYE, Material.LIME_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.MAGENTA_DYE, Material.MAGENTA_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.ORANGE_DYE, Material.ORANGE_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.PINK_DYE, Material.PINK_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.PURPLE_DYE, Material.PURPLE_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.RED_DYE, Material.RED_STAINED_GLASS));
		addRecipe(createColorChangingRecipe(stainedGlass, Material.YELLOW_DYE, Material.YELLOW_STAINED_GLASS));
	}

	public void stainedGlassPaneDying() {
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.WHITE_DYE, Material.WHITE_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.BLACK_DYE, Material.BLACK_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.BLUE_DYE, Material.BLUE_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.BROWN_DYE, Material.BROWN_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.CYAN_DYE, Material.CYAN_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.GREEN_DYE, Material.GREEN_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.GRAY_DYE, Material.GRAY_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.LIGHT_BLUE_DYE, Material.LIGHT_BLUE_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.LIGHT_GRAY_DYE, Material.LIGHT_GRAY_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.LIME_DYE, Material.LIME_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.MAGENTA_DYE, Material.MAGENTA_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.ORANGE_DYE, Material.ORANGE_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.PINK_DYE, Material.PINK_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.PURPLE_DYE, Material.PURPLE_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.RED_DYE, Material.RED_STAINED_GLASS_PANE));
		addRecipe(createColorChangingRecipe(stainedGlassPane, Material.YELLOW_DYE, Material.YELLOW_STAINED_GLASS_PANE));
	}

	public void terracottaDying() {
		addRecipe(createColorChangingRecipe(terracotta, Material.WHITE_DYE, Material.WHITE_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.BLACK_DYE, Material.BLACK_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.BLUE_DYE, Material.BLUE_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.BROWN_DYE, Material.BROWN_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.CYAN_DYE, Material.CYAN_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.GREEN_DYE, Material.GREEN_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.GRAY_DYE, Material.GRAY_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.LIGHT_BLUE_DYE, Material.LIGHT_BLUE_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.LIGHT_GRAY_DYE, Material.LIGHT_GRAY_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.LIME_DYE, Material.LIME_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.MAGENTA_DYE, Material.MAGENTA_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.ORANGE_DYE, Material.ORANGE_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.PINK_DYE, Material.PINK_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.PURPLE_DYE, Material.PURPLE_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.RED_DYE, Material.RED_TERRACOTTA));
		addRecipe(createColorChangingRecipe(terracotta, Material.YELLOW_DYE, Material.YELLOW_TERRACOTTA));
	}

	public void bedDying() {
		addRecipe(createShapelessRecipe(bed, Material.WHITE_DYE, Material.WHITE_BED));
		addRecipe(createShapelessRecipe(bed, Material.BLACK_DYE, Material.BLACK_BED));
		addRecipe(createShapelessRecipe(bed, Material.BLUE_DYE, Material.BLUE_BED));
		addRecipe(createShapelessRecipe(bed, Material.BROWN_DYE, Material.BROWN_BED));
		addRecipe(createShapelessRecipe(bed, Material.CYAN_DYE, Material.CYAN_BED));
		addRecipe(createShapelessRecipe(bed, Material.GREEN_DYE, Material.GREEN_BED));
		addRecipe(createShapelessRecipe(bed, Material.GRAY_DYE, Material.GRAY_BED));
		addRecipe(createShapelessRecipe(bed, Material.LIGHT_BLUE_DYE, Material.LIGHT_BLUE_BED));
		addRecipe(createShapelessRecipe(bed, Material.LIGHT_GRAY_DYE, Material.LIGHT_GRAY_BED));
		addRecipe(createShapelessRecipe(bed, Material.LIME_DYE, Material.LIME_BED));
		addRecipe(createShapelessRecipe(bed, Material.MAGENTA_DYE, Material.MAGENTA_BED));
		addRecipe(createShapelessRecipe(bed, Material.ORANGE_DYE, Material.ORANGE_BED));
		addRecipe(createShapelessRecipe(bed, Material.PINK_DYE, Material.PINK_BED));
		addRecipe(createShapelessRecipe(bed, Material.PURPLE_DYE, Material.PURPLE_BED));
		addRecipe(createShapelessRecipe(bed, Material.RED_DYE, Material.RED_BED));
		addRecipe(createShapelessRecipe(bed, Material.YELLOW_DYE, Material.YELLOW_BED));
	}

	public void setWoolUndyingRecipe() {
		ShapedRecipe woolUndyingRecipe = new ShapedRecipe(new NamespacedKey(BNCore.getInstance(), "custom_whiteWool"), new ItemStack(Material.WHITE_WOOL, 8));
		woolUndyingRecipe.shape("www", "wbw", "www");
		woolUndyingRecipe.setIngredient('w', wool);
		woolUndyingRecipe.setIngredient('b', Material.WATER_BUCKET);
		addRecipe(woolUndyingRecipe);
		amount++;
	}

}
