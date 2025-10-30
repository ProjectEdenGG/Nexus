package gg.projecteden.nexus.features.recipes.functionals;

import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.CoreProtectUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent.ChangeReason;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InfiniteWaterBucket extends FunctionalRecipe {

	@Getter
	private static final ItemStack item = new ItemBuilder(ItemModelType.INFINITE_WATER_BUCKET)
			.name("&6Infinite Water Bucket")
			.build();

	@Override
	public ItemStack getResult() {
		return getItem();
	}

	@Override
	public @NotNull Recipe getRecipe() {
		return RecipeBuilder.shapeless()
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

			for (ItemStack itemStack : matrix)
				if (!Nullables.isNullOrAir(itemStack))
					if (Material.BUCKET == itemStack.getType())
						itemStack.setType(Material.AIR);

			event.getInventory().setMatrix(matrix);
		});
	}

	@EventHandler
	public void onCraftWith(CraftItemEvent event) {
		ItemStack[] original = event.getInventory().getMatrix();
		if (event.getRecipe() instanceof ShapedRecipe) {
			boolean found = false;
			for (int i = 0; i < original.length; i++) {
				if (Nullables.isNullOrAir(original[i])) continue;
				if (Objects.equals(new ItemBuilder(original[i]).model(), new ItemBuilder(getResult()).model())) {
					original[i] = new ItemBuilder(getResult()).amount(original[i].getAmount()).build();
					found = true;
				}
			}
			if (found)
				Tasks.wait(1, () -> event.getInventory().setMatrix(original));
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.useInteractedBlock() == Result.DENY || event.useItemInHand() == Result.DENY)
			return;

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Player player = event.getPlayer();
		if (player.getWorld().isUltraWarm()) // prevent placing water in the nether
			return;

		final ItemStack item = event.getItem();
		if (!isInfiniteWaterBucket(item))
			return;

		Block clickedBlock = event.getClickedBlock();
		if (Nullables.isNullOrAir(clickedBlock)) // can't do anything with a null origin block
			return;

		if (clickedBlock.getType() != Material.WATER && !(clickedBlock.getBlockData() instanceof Waterlogged)) {
			clickedBlock = clickedBlock.getRelative(event.getBlockFace());
			if (Nullables.isNotNullOrAir(clickedBlock)) {
				if (clickedBlock.getType() != Material.WATER && !(clickedBlock.getBlockData() instanceof Waterlogged))
					return;
			}
		}

		final BlockState clickedState = clickedBlock.getState();

		if (specialCases(clickedBlock, clickedState, player))
			return;

		BlockPlaceEvent placeEvent = new BlockPlaceEvent(clickedBlock, clickedState, clickedBlock, item, player, true, EquipmentSlot.HAND);
		if (!placeEvent.callEvent() || !placeEvent.canBuild())
			return;

		if (clickedBlock.getBlockData() instanceof Waterlogged waterlogged) {
			waterlogged.setWaterlogged(true);
			clickedBlock.setBlockData(waterlogged, true);
		} else {
			clickedBlock.setType(Material.WATER);
		}

		// Not caught by CoreProtect via BlockPlaceEvent ??
		// This method does not log waterlogging
		CoreProtectUtils.logPlacement(player, clickedBlock);
	}

	private static boolean specialCases(@NonNull Block clickedBlock, @NonNull BlockState clickedState, @NonNull Player player) {
		if (clickedBlock.getType() == Material.CAULDRON) {
			final Levelled cauldron = (Levelled) Material.WATER_CAULDRON.createBlockData();
			cauldron.setLevel(cauldron.getMaximumLevel());
			clickedState.setBlockData(cauldron);

			final CauldronLevelChangeEvent fillEvent = new CauldronLevelChangeEvent(clickedBlock, player, ChangeReason.BUCKET_EMPTY, clickedState);
			if (!fillEvent.callEvent())
				return true;

			clickedBlock.setBlockData(cauldron);
			return true;
		}

		return false;
	}

	@Contract("null -> false")
	private static boolean isInfiniteWaterBucket(ItemStack item) {
		return ItemModelType.of(item) == ItemModelType.INFINITE_WATER_BUCKET;
	}

}

