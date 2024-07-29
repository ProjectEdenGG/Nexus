package gg.projecteden.nexus.features.virtualinventories;

import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener.CustomInventoryHolder;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.VirtualInventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;

public class VirtualInventoryUtils {

	@Data
	@AllArgsConstructor
	public static class VirtualInventoryHolder extends CustomInventoryHolder {
		private final VirtualInventory<?> virtualInventory;
	}

	public static CookingRecipe<?> getCookingRecipes(ItemStack ingredient, Class<? extends CookingRecipe<?>> recipeType) {
		Iterator<Recipe> iterator = Bukkit.recipeIterator();
		while (iterator.hasNext()) {
			Recipe recipe = iterator.next();
			if (!(recipe instanceof CookingRecipe<?> cookingRecipe))
				continue;

			if (!recipeType.isAssignableFrom(recipe.getClass()))
				continue;

			if (!cookingRecipe.getInputChoice().test(ingredient))
				continue;

			return cookingRecipe;
		}

		return null;
	}
}
