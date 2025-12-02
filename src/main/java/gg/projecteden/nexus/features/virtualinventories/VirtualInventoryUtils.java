package gg.projecteden.nexus.features.virtualinventories;

import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener.CustomInventoryHolder;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.VirtualInventory;
import gg.projecteden.nexus.utils.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class VirtualInventoryUtils {

	private static final Map<Class<? extends CookingRecipe<?>>, Map<ItemStack, CookingRecipe<?>>> CACHE = new HashMap<>();

	public static CookingRecipe<?> getCookingRecipes(ItemStack ingredient, Class<? extends CookingRecipe<?>> recipeType) {
		var cache = CACHE.computeIfAbsent(recipeType, $ -> new HashMap<>());
		for (Entry<ItemStack, CookingRecipe<?>> entry : cache.entrySet())
			if (ItemUtils.isFuzzyMatch(entry.getKey(), ingredient))
				return entry.getValue();

		return cache.computeIfAbsent(ingredient, $ -> {
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
		});
	}

	@Data
	@AllArgsConstructor
	public static class VirtualInventoryHolder extends CustomInventoryHolder {
		private final VirtualInventory<?> virtualInventory;
	}
}
