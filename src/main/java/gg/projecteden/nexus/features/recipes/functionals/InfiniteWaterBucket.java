package gg.projecteden.nexus.features.recipes.functionals;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.ItemUtils.isFuzzyMatch;
import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

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

	private void restoreInfiniteWaterBucket(Player player, EquipmentSlot hand) {
		PlayerInventory inventory = player.getInventory();
		ItemStack tool = inventory.getItem(hand);

		if (tool == null || tool.getType() != Material.BUCKET) {
			ItemStack missingBucket = new ItemStack(Material.BUCKET);
			if (inventory.containsAtLeast(missingBucket, 1)) {
				inventory.removeItem(missingBucket);
				PlayerUtils.giveItem(player, infiniteWaterBucket.clone());
			}
		} else
			inventory.setItem(hand, infiniteWaterBucket.clone());
	}

	@EventHandler
	public void onPlaceInfiniteWater(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		ItemStack tool = inventory.getItem(event.getHand());
		if (isNullOrAir(tool))
			return;

		ItemStack waterBucket = tool.clone();

		if (isNullOrAir(waterBucket))
			return;
		if (!isFuzzyMatch(infiniteWaterBucket, waterBucket))
			return;

		Tasks.wait(1, () -> restoreInfiniteWaterBucket(player, event.getHand()));
	}

	@EventHandler
	public void onCauldron(CauldronLevelChangeEvent event) {
		if (event.getReason() != CauldronLevelChangeEvent.ChangeReason.BUCKET_EMPTY)
			return;
		if (!(event.getEntity() instanceof Player player))
			return;

		EquipmentSlot hand = ItemUtils.getHandWithTool(player, Material.WATER_BUCKET);
		if (hand == null)
			return;

		ItemStack tool = player.getInventory().getItem(hand);
		if (isNullOrAir(tool))
			return;

		if (isNullOrAir(tool))
			return;
		if (!isFuzzyMatch(infiniteWaterBucket, tool))
			return;

		Tasks.wait(1, () -> restoreInfiniteWaterBucket(player, hand));
	}

}
