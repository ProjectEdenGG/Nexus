package me.pugabyte.nexus.features.recipes;

import fr.minuskube.inv.SmartInventory;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;

public class CraftingRecipeMenu {

	public static void open(CraftingMenuType menu, Player player) {
		SmartInventory.builder()
				.size(3, 9)
				.title("Custom Recipes")
				.provider(new CraftingRecipeProvider(menu))
				.build()
				.open(player);
	}

	@Data
	public static class CraftingRecipe {

		public RecipeChoice.MaterialChoice choice = null;
		public List<ItemStack> inputItems = null;
		public ItemStack customOutput = null;
		public Material ingredient;
		public int ingredientAmount;
		public Material output;
		public int outputAmount;

		public CraftingRecipe(RecipeChoice.MaterialChoice choice, Material ingredient, int ingredientAmount, Material output, int outputAmount) {
			this.choice = choice;
			this.ingredient = ingredient;
			this.ingredientAmount = ingredientAmount;
			this.output = output;
			this.outputAmount = outputAmount;
		}

		public CraftingRecipe(Material ingredient, int ingredientAmount, Material output, int outputAmount) {
			this.ingredient = ingredient;
			this.ingredientAmount = ingredientAmount;
			this.output = output;
			this.outputAmount = outputAmount;
		}

		public CraftingRecipe(List<ItemStack> inputItems, ItemStack customOutput) {
			this.inputItems = inputItems;
			this.customOutput = customOutput;
		}

	}

}
