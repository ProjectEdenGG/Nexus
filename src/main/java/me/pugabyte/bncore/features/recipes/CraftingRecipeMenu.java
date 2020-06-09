package me.pugabyte.bncore.features.recipes;

import fr.minuskube.inv.SmartInventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice;

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
	@AllArgsConstructor
	public static class CraftingRecipe {

		public RecipeChoice.MaterialChoice choice = null;
		public Material ingredient;
		public int ingredientAmount;
		public Material output;
		public int outputAmount;

		public CraftingRecipe(Material ingredient, int ingredientAmount, Material output, int outputAmount) {
			this.ingredient = ingredient;
			this.ingredientAmount = ingredientAmount;
			this.output = output;
			this.outputAmount = outputAmount;
		}

	}

}
