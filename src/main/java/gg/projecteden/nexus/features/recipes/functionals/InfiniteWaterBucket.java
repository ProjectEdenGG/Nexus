package gg.projecteden.nexus.features.recipes.functionals;

import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
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
					original[i] = getResult();
					found = true;
				}
			}
			if (found)
				Tasks.wait(1, () -> event.getInventory().setMatrix(original));
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		// prevent placing water in the nether
		if (event.getPlayer().getWorld().isUltraWarm())
			return;

		final ItemStack item = event.getItem();
		if (!isInfiniteWaterBucket(item))
			return;

		final Block clickedBlock = event.getClickedBlock();
		if (Nullables.isNullOrAir(clickedBlock))
			return;

		final BlockState clickedState = clickedBlock.getState();

		if (clickedBlock.getType() == Material.CAULDRON) {
			final Levelled cauldron = (Levelled) Material.WATER_CAULDRON.createBlockData();
			cauldron.setLevel(cauldron.getMaximumLevel());
			clickedState.setBlockData(cauldron);

			final CauldronLevelChangeEvent fillEvent = new CauldronLevelChangeEvent(clickedBlock, event.getPlayer(), ChangeReason.BUCKET_EMPTY, clickedState);
			if (!fillEvent.callEvent())
				return;

			clickedBlock.setBlockData(cauldron);
			return;
		}

		final Block block;

		if (clickedBlock.getBlockData() instanceof Waterlogged)
			block = clickedBlock;
		else
			block = clickedBlock.getRelative(event.getBlockFace());

		final BlockState state = block.getState();

		BlockPlaceEvent placeEvent = new BlockPlaceEvent(block, state, clickedBlock, item, event.getPlayer(), true, EquipmentSlot.HAND);
		if (!placeEvent.callEvent() || !placeEvent.canBuild())
			return;

		if (block.getBlockData() instanceof Waterlogged waterlogged) {
			waterlogged.setWaterlogged(true);
			block.setBlockData(waterlogged);
		} else
			block.setType(Material.WATER);
	}

	@Contract("null -> false")
	private static boolean isInfiniteWaterBucket(ItemStack item) {
		return ItemModelType.of(item) == ItemModelType.INFINITE_WATER_BUCKET;
	}

}

