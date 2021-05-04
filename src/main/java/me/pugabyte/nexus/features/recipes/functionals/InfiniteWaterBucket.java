package me.pugabyte.nexus.features.recipes.functionals;

import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.recipes.models.FunctionalRecipe;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.utils.ItemUtils.isFuzzyMatch;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

public class InfiniteWaterBucket extends FunctionalRecipe {

	@Getter
	private static final ItemStack infiniteWaterBucket = new ItemBuilder(Material.WATER_BUCKET).name("Infinite Bucket of Water").build();

	@Override
	public ItemStack getResult() {
		return infiniteWaterBucket;
	}

	@Override
	public Recipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), stripColor("custom_infinite_bucket_of_water"));
		ShapelessRecipe recipe = new ShapelessRecipe(key, infiniteWaterBucket);
		recipe.addIngredient(2, Material.WATER_BUCKET);
		return recipe;
	}

	@Override
	public List<ItemStack> getIngredients() {
		return new ArrayList<>() {{
			add(new ItemStack(Material.WATER_BUCKET));
			add(new ItemStack(Material.WATER_BUCKET));
		}};
	}

	@Override
	public String[] getPattern() {
		return null;
	}

	@Override
	public RecipeChoice.MaterialChoice getMaterialChoice() {
		return null;
	}

	@EventHandler
	public void onPlaceInfiniteWater(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		ItemStack waterBucket = player.getInventory().getItem(event.getHand()).clone();

		if (isNullOrAir(waterBucket))
			return;
		if (!isFuzzyMatch(infiniteWaterBucket, waterBucket))
			return;

		Tasks.wait(1, () -> player.getInventory().setItem(event.getHand(), waterBucket));
	}

	@EventHandler
	public void onCraft(CraftItemEvent event) {
		ItemStack result = event.getInventory().getResult();
		if (isNullOrAir(result))
			return;

		if (isFuzzyMatch(infiniteWaterBucket, result)) {
			Tasks.wait(1, () -> {
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

	@EventHandler
	public void onCauldron(CauldronLevelChangeEvent event) {
		if (event.getReason() != CauldronLevelChangeEvent.ChangeReason.BUCKET_EMPTY) return;
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		ItemStack item = player.getInventory().getItemInMainHand().clone();

		if (isNullOrAir(item))
			return;
		if (!isFuzzyMatch(infiniteWaterBucket, item))
			return;

		Tasks.wait(1, () -> player.getInventory().setItemInMainHand(item));
	}

}
