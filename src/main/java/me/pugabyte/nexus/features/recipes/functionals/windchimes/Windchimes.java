package me.pugabyte.nexus.features.recipes.functionals.windchimes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.recipes.models.FunctionalRecipe;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

import static eden.utils.StringUtils.camelCase;

public abstract class Windchimes extends FunctionalRecipe {

	@Getter
	@AllArgsConstructor
	protected enum WindchimeType {
		IRON(Material.IRON_INGOT),
		GOLD(Material.GOLD_INGOT),
		COPPER(Material.COPPER_INGOT),
		;

		private final Material ingot;
	}

	@Getter
	public ItemStack item = new ItemBuilder(Material.AMETHYST_SHARD)
		.name(camelCase(getWindchimeType()) + " Windchimes")
		.customModelData(getWindchimeType().ordinal() + 1)
		.build();

	abstract WindchimeType getWindchimeType();

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public ItemStack getResult() {
		return item;
	}

	@Override
	public String[] getPattern() {
		return new String[]{"111", "222", "343"};
	}

	@Override
	public Recipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_windchimes_" + getWindchimeType().name().toLowerCase());
		ShapedRecipe recipe = new ShapedRecipe(key, item);
		recipe.shape(getPattern());
		recipe.setIngredient('1', Material.STICK);
		recipe.setIngredient('2', Material.CHAIN);
		recipe.setIngredient('3', getWindchimeType().getIngot());
		recipe.setIngredient('4', getMaterialChoice());
		return recipe;
	}

	@Override
	public List<ItemStack> getIngredients() {
		return new ArrayList<>(List.of(
			new ItemStack(Material.STICK),
			new ItemStack(Material.CHAIN),
			new ItemStack(getWindchimeType().getIngot()),
			new ItemStack(Material.OAK_BUTTON)
		));
	}

	@Override
	public MaterialChoice getMaterialChoice() {
		return new RecipeChoice.MaterialChoice(MaterialTag.WOOD_BUTTONS);
	}

}
