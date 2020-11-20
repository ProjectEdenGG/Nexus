package me.pugabyte.nexus.features.recipes;

import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.annotations.Disabled;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.HashMap;
import java.util.Map;

@Disabled
public class CustomRecipes extends Feature {
	@Getter
	public static Map<NamespacedKey, Recipe> recipes = new HashMap<>();

	@Override
	public void startup() {
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
		});
	}

	public static void addRecipe(Recipe recipe) {
		try {
			if (recipe == null) return;
			for (Recipe recipe1 : Bukkit.getServer().getRecipesFor(recipe.getResult()))
				if (RecipeUtils.areEqual(recipe, recipe1)) return;

			Tasks.sync(() -> {
				try {
					Bukkit.addRecipe(recipe);
					recipes.put(((Keyed) recipe).getKey(), recipe);
				} catch (IllegalStateException duplicate) {
					Nexus.log(duplicate.getMessage());
				} catch (Exception ex) {
					Nexus.log("Error while adding custom recipe " + ((Keyed) recipe).getKey() + " to Bukkit");
					ex.printStackTrace();
				}
			});
		} catch (Exception ex) {
			Nexus.log("Error while adding custom recipe " + ((Keyed) recipe).getKey());
			ex.printStackTrace();
		}
	}

	public ShapelessRecipe createSingleItemShapelessRecipe(Material inputItem, int requiredAmount, Material outputItem, int outputAmount, CraftingMenuType type) {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_" + inputItem.name() + "_" + outputItem.name());
		ShapelessRecipe recipe = new ShapelessRecipe(key, new ItemStack(outputItem, outputAmount));
		recipe.addIngredient(requiredAmount, inputItem);
		type.getList().add(new CraftingRecipeMenu.CraftingRecipe(inputItem, requiredAmount, outputItem, outputAmount));
		recipes.put(key, recipe);
		return recipe;
	}

	public ShapelessRecipe createShapelessRecipe(RecipeChoice.MaterialChoice inputItem, Material dye, Material outputItem, CraftingMenuType type) {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_" + outputItem.name() + "_color");
		ShapelessRecipe recipe = new ShapelessRecipe(key, new ItemStack(outputItem, 1));
		recipe.addIngredient(inputItem);
		recipe.addIngredient(dye);
		type.getList().add(new CraftingRecipeMenu.CraftingRecipe(inputItem, dye, 1, outputItem, 1));
		recipes.put(key, recipe);
		return recipe;
	}

	public ShapedRecipe createColorChangingRecipe(RecipeChoice.MaterialChoice inputItem, Material dye, Material outputItem) {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_" + outputItem.name() + "_color");
		ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(outputItem, 8));
		recipe.shape("iii", "idi", "iii");
		recipe.setIngredient('i', inputItem);
		recipe.setIngredient('d', dye);
		CraftingMenuType.DYES.getList().add(new CraftingRecipeMenu.CraftingRecipe(inputItem, dye, 1, outputItem, 1));
		recipes.put(key, recipe);
		return recipe;
	}

	public void misc() {
		addRecipe(createSingleItemShapelessRecipe(Material.NETHER_WART_BLOCK, 1, Material.NETHER_WART, 9, CraftingMenuType.MISC));
		addRecipe(createSingleItemShapelessRecipe(Material.BLUE_ICE, 1, Material.PACKED_ICE, 9, CraftingMenuType.MISC));
		addRecipe(createSingleItemShapelessRecipe(Material.PACKED_ICE, 1, Material.ICE, 9, CraftingMenuType.MISC));
		addRecipe(createSingleItemShapelessRecipe(Material.CHISELED_RED_SANDSTONE, 1, Material.RED_SANDSTONE_SLAB, 2, CraftingMenuType.MISC));
		addRecipe(createSingleItemShapelessRecipe(Material.CHISELED_SANDSTONE, 1, Material.SANDSTONE_SLAB, 2, CraftingMenuType.MISC));
		addRecipe(createSingleItemShapelessRecipe(Material.HONEYCOMB_BLOCK, 1, Material.HONEYCOMB, 4, CraftingMenuType.MISC));
	}

	public void slabsToBlocks() {
		addRecipe(createSingleItemShapelessRecipe(Material.OAK_SLAB, 4, Material.OAK_PLANKS, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.SPRUCE_SLAB, 4, Material.SPRUCE_PLANKS, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.BIRCH_SLAB, 4, Material.BIRCH_PLANKS, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.JUNGLE_SLAB, 4, Material.JUNGLE_PLANKS, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.ACACIA_SLAB, 4, Material.ACACIA_PLANKS, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.DARK_OAK_SLAB, 4, Material.DARK_OAK_PLANKS, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.STONE_SLAB, 4, Material.STONE, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.SANDSTONE_SLAB, 4, Material.SANDSTONE, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.COBBLESTONE_SLAB, 4, Material.COBBLESTONE, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.BRICK_SLAB, 4, Material.BRICKS, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.STONE_BRICK_SLAB, 4, Material.STONE_BRICKS, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.NETHER_BRICK_SLAB, 4, Material.NETHER_BRICKS, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.QUARTZ_SLAB, 4, Material.QUARTZ_BLOCK, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.RED_SANDSTONE_SLAB, 4, Material.RED_SANDSTONE, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.PURPUR_SLAB, 4, Material.PURPUR_BLOCK, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.PRISMARINE_SLAB, 4, Material.PRISMARINE, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.PRISMARINE_BRICK_SLAB, 4, Material.PRISMARINE_BRICKS, 2, CraftingMenuType.SLABS));
		addRecipe(createSingleItemShapelessRecipe(Material.DARK_PRISMARINE_SLAB, 4, Material.DARK_PRISMARINE, 2, CraftingMenuType.SLABS));
	}

	public void quartsUncrafting() {
		addRecipe(createSingleItemShapelessRecipe(Material.QUARTZ_BLOCK, 1, Material.QUARTZ, 4, CraftingMenuType.QUARTZ));
		addRecipe(createSingleItemShapelessRecipe(Material.QUARTZ_PILLAR, 1, Material.QUARTZ_BLOCK, 2, CraftingMenuType.QUARTZ));
		addRecipe(createSingleItemShapelessRecipe(Material.CHISELED_QUARTZ_BLOCK, 1, Material.QUARTZ_SLAB, 2, CraftingMenuType.QUARTZ));
	}

	public void stoneBricksUncrafting() {
		addRecipe(createSingleItemShapelessRecipe(Material.CHISELED_STONE_BRICKS, 1, Material.STONE_BRICK_SLAB, 2, CraftingMenuType.STONE_BRICK));
		addRecipe(createSingleItemShapelessRecipe(Material.STONE_BRICKS, 4, Material.STONE, 4, CraftingMenuType.STONE_BRICK));
		addRecipe(createSingleItemShapelessRecipe(Material.MOSSY_STONE_BRICKS, 1, Material.STONE_BRICKS, 1, CraftingMenuType.STONE_BRICK));
	}

	RecipeChoice.MaterialChoice wool = new RecipeChoice.MaterialChoice(MaterialTag.WOOL.toArray());
	RecipeChoice.MaterialChoice concretePowder = new RecipeChoice.MaterialChoice(MaterialTag.CONCRETE_POWDERS.toArray());
	RecipeChoice.MaterialChoice stainedGlass = new RecipeChoice.MaterialChoice(MaterialTag.STAINED_GLASS.toArray());
	RecipeChoice.MaterialChoice stainedGlassPane = new RecipeChoice.MaterialChoice(MaterialTag.STAINED_GLASS_PANES.toArray());
	RecipeChoice.MaterialChoice terracotta = new RecipeChoice.MaterialChoice(MaterialTag.COLORED_TERRACOTTAS.toArray());
	RecipeChoice.MaterialChoice bed = new RecipeChoice.MaterialChoice(MaterialTag.BEDS.toArray());

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
		addRecipe(createShapelessRecipe(bed, Material.WHITE_DYE, Material.WHITE_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.BLACK_DYE, Material.BLACK_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.BLUE_DYE, Material.BLUE_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.BROWN_DYE, Material.BROWN_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.CYAN_DYE, Material.CYAN_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.GREEN_DYE, Material.GREEN_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.GRAY_DYE, Material.GRAY_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.LIGHT_BLUE_DYE, Material.LIGHT_BLUE_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.LIGHT_GRAY_DYE, Material.LIGHT_GRAY_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.LIME_DYE, Material.LIME_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.MAGENTA_DYE, Material.MAGENTA_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.ORANGE_DYE, Material.ORANGE_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.PINK_DYE, Material.PINK_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.PURPLE_DYE, Material.PURPLE_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.RED_DYE, Material.RED_BED, CraftingMenuType.BEDS));
		addRecipe(createShapelessRecipe(bed, Material.YELLOW_DYE, Material.YELLOW_BED, CraftingMenuType.BEDS));
	}

	public void setWoolUndyingRecipe() {
		ShapedRecipe woolUndyingRecipe = new ShapedRecipe(new NamespacedKey(Nexus.getInstance(), "custom_whiteWool"), new ItemStack(Material.WHITE_WOOL, 8));
		woolUndyingRecipe.shape("www", "wbw", "www");
		woolUndyingRecipe.setIngredient('w', wool);
		woolUndyingRecipe.setIngredient('b', Material.WATER_BUCKET);
		addRecipe(woolUndyingRecipe);
		recipes.put(new NamespacedKey(Nexus.getInstance(), "custom_whiteWool"), woolUndyingRecipe);
		CraftingMenuType.WOOL.getList().add(new CraftingRecipeMenu.CraftingRecipe(wool, Material.WATER_BUCKET, 1, Material.WHITE_WOOL, 8));
	}

}
