package gg.projecteden.nexus.features.recipes.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class NexusRecipe {

	@NonNull
	public ItemStack result;
	public List<ItemStack> ingredients = new ArrayList<>();
	public RecipeChoice.MaterialChoice materialChoice;
	public String[] pattern;
	public Recipe recipe;
	public RecipeType type = RecipeType.MISC;
	public NamespacedKey namespacedKey;

	public String getPermission() {
		return null;
	}

	public ItemStack getResult() {
		return recipe.getResult();
	}

	public NexusRecipe type(RecipeType type) {
		this.type = type;
		return this;
	}

	public static NexusRecipe shapeless(ItemStack result, Material material, RecipeChoice.MaterialChoice ingredients, String namespaceKey) {
		NexusRecipe recipe = new NexusRecipe(result);
		recipe.getIngredients().add(new ItemStack(material));

		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), stripColor("custom_" + getItemName(result) + (namespaceKey != null ? "_" + namespaceKey : "")));
		ShapelessRecipe bukkitRecipe = new ShapelessRecipe(key, result);
		bukkitRecipe.addIngredient(material);
		bukkitRecipe.addIngredient(ingredients);
		recipe.setRecipe(bukkitRecipe);
		recipe.setMaterialChoice(ingredients);

		CustomRecipes.recipes.add(recipe);
		return recipe;
	}

	public static NexusRecipe shapeless(ItemStack result, Material material, RecipeChoice.MaterialChoice ingredients) {
		return shapeless(result, material, ingredients, null);
	}

	public static NexusRecipe shapeless(ItemStack result, Material... ingredients) {
		return shapeless(result, null, ingredients);
	}

	public static NexusRecipe shapeless(ItemStack result, String namespaceKey, Material... ingredients) {
		NexusRecipe recipe = new NexusRecipe(result);
		Arrays.asList(ingredients).forEach(mat -> recipe.getIngredients().add(new ItemStack(mat)));

		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), stripColor("custom_" + getItemName(result) + (namespaceKey != null ? "_" + namespaceKey : "")));
		ShapelessRecipe bukkitRecipe = new ShapelessRecipe(key, result);
		for (Material material : ingredients)
			bukkitRecipe.addIngredient(material);
		recipe.setRecipe(bukkitRecipe);

		CustomRecipes.recipes.add(recipe);
		return recipe;
	}


	public static NexusRecipe shaped(ItemStack result, String[] pattern, Material... ingredients) {
		NexusRecipe recipe = new NexusRecipe(result);
		recipe.setPattern(pattern);
		Arrays.asList(ingredients).forEach(mat -> recipe.getIngredients().add(new ItemStack(mat)));

		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_" + getItemName(result));
		recipe.setRecipe(shapedRecipe(key, result, pattern, ingredients));

		CustomRecipes.recipes.add(recipe);
		return recipe;
	}

	public static ShapedRecipe shapedRecipe(NamespacedKey key, ItemStack result, String[] pattern, Material... ingredients) {
		ShapedRecipe bukkitRecipe = new ShapedRecipe(key, result);
		bukkitRecipe.shape(pattern[0], pattern[1], pattern[2]);
		for (int i = 1; i <= ingredients.length; i++) {
			bukkitRecipe.setIngredient((char) i, ingredients[i - 1]);
		}
		return bukkitRecipe;
	}

	public static ShapedRecipe surroundRecipe(NamespacedKey key, ItemStack result, Material center, Material surround) {
		return surroundRecipe(key, result, new ItemStack(center), surround);
	}

	public static ShapedRecipe surroundRecipe(NamespacedKey key, ItemStack result, ItemStack center, Material surround) {
		return surroundRecipe(key, result, center, new RecipeChoice.MaterialChoice(surround));
	}

	public static ShapedRecipe surroundRecipe(NamespacedKey key, ItemStack result, ItemStack center, RecipeChoice.MaterialChoice surround) {
		ShapedRecipe bukkitRecipe = new ShapedRecipe(key, result);
		bukkitRecipe.shape("111", "121", "111");
		bukkitRecipe.setIngredient('1', surround);
		bukkitRecipe.setIngredient('2', center);
		return bukkitRecipe;
	}

	public static NexusRecipe surround(ItemStack result, ItemStack center, RecipeChoice.MaterialChoice surround) {
		NexusRecipe recipe = new NexusRecipe(result);
		recipe.setPattern(new String[] {"###", "#2#", "###"});
		recipe.setMaterialChoice(surround);
		recipe.getIngredients().add(center);

		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_" + getItemName(result));
		recipe.setRecipe(surroundRecipe(key, result, center, surround));

		CustomRecipes.recipes.add(recipe);
		return recipe;
	}

	public static NexusRecipe surround(ItemStack result, Material center, RecipeChoice.MaterialChoice surround) {
		return surround(result, new ItemStack(center), surround);
	}

	public static NexusRecipe surround(ItemStack result, Material center, Material surround) {
		return surround(result, center, new RecipeChoice.MaterialChoice(surround));
	}

	public void register() {
		CustomRecipes.register(getRecipe());
	}

	private static String getItemName(ItemStack result) {
		return StringUtils.stripColor(ItemUtils.getName(result).replaceAll(" ", "_").trim().toLowerCase());
	}

	@NotNull
	protected List<ItemStack> getFilteredMatrix(PrepareItemCraftEvent event) {
		List<ItemStack> matrix = new ArrayList<>(Arrays.asList(event.getInventory().getMatrix().clone()));
		matrix.removeIf(ItemUtils::isNullOrAir);
		return matrix;
	}

}
