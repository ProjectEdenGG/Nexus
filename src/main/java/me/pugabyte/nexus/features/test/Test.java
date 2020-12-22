package me.pugabyte.nexus.features.test;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.recipes.RecipeUtils;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.HashMap;
import java.util.Map;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

@NoArgsConstructor
public class Test extends Feature implements Listener {
	static ItemStack infiniteWaterBucket = new ItemBuilder(Material.WATER_BUCKET).name("Infinite Bucket of Water").amount(1).build();
	static ItemStack air = new ItemStack(Material.AIR, 1);
	static ItemStack[] air9 = {air, air, air, air, air, air, air, air, air};

	private static Map<NamespacedKey, Recipe> recipes = new HashMap<>();

	@Override
	public void startup() {
		super.startup();

		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_infinite_water_bucket");
		ShapelessRecipe recipe = new ShapelessRecipe(key, infiniteWaterBucket);
		recipe.addIngredient(new ItemStack(Material.WATER_BUCKET, 1));
		recipe.addIngredient(new ItemStack(Material.WATER_BUCKET, 1));

		Bukkit.removeRecipe(key);
		Tasks.wait(5, () -> addRecipe(recipe));

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

	@EventHandler
	public void onCraft(CraftItemEvent event) {
		ItemStack result = event.getInventory().getResult();
		if (isNullOrAir(result))
			return;

		if (ItemUtils.isFuzzyMatch(infiniteWaterBucket, result)) {
			Tasks.wait(1, () -> {
//				event.getInventory().setMatrix(air9);   // This worked, but it also removed the result??

				ItemStack[] matrix = event.getInventory().getMatrix();
				for (ItemStack itemStack : matrix) {
					if (isNullOrAir(itemStack))
						continue;

					if (Material.BUCKET.equals(itemStack.getType()))
						itemStack.setType(Material.AIR);
				}
				event.getInventory().setMatrix(matrix);
			});
		}
	}
}
