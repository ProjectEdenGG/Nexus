package gg.projecteden.nexus.features.recipes.functionals;

import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shapeless;
import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;

public class InfiniteWaterBucket extends FunctionalRecipe {

	@Getter
	private static final ItemStack item = getCustomModel().getItem();

	public static CustomModel getCustomModel() {
		return CustomModel.of(Material.WATER_BUCKET, 2);
	}

	@Override
	public ItemStack getResult() {
		return item;
	}

	@Override
	public @NotNull Recipe getRecipe() {
		return shapeless()
			.add(Material.WATER_BUCKET, 2)
			.add(Material.GOLD_INGOT)
			.toMake(getResult())
			.getRecipe();
	}

	@EventHandler
	public void onCraft(CraftItemEvent event) {
		ItemStack result = event.getInventory().getResult();
		if (!isInfiniteWaterBucket(result))
			return;

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

	private void restoreInfiniteWaterBucket(Player player, EquipmentSlot hand) {
		PlayerInventory inventory = player.getInventory();
		ItemStack tool = inventory.getItem(hand);

		if (tool == null || tool.getType() != Material.BUCKET) {
			ItemStack missingBucket = new ItemStack(Material.BUCKET);
			if (inventory.containsAtLeast(missingBucket, 1)) {
				inventory.removeItem(missingBucket);
				PlayerUtils.giveItem(player, item.clone());
			}
		} else
			inventory.setItem(hand, item.clone());
	}

	@EventHandler
	public void onPlaceInfiniteWater(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		ItemStack tool = inventory.getItem(event.getHand());
		if (!isInfiniteWaterBucket(tool))
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
		if (!isInfiniteWaterBucket(tool))
			return;

		Tasks.wait(1, () -> restoreInfiniteWaterBucket(player, hand));
	}

	private static boolean isInfiniteWaterBucket(ItemStack item) {
		final CustomModel customModel = getCustomModel();
		if (customModel == null)
			return false;

		return customModel.equals(CustomModel.of(item));
	}

}
