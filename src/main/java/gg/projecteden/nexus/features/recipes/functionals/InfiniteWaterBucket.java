package gg.projecteden.nexus.features.recipes.functionals;

import com.viaversion.viaversion.api.minecraft.item.Item;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
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

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shapeless;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class InfiniteWaterBucket extends FunctionalRecipe {

	public static CustomModel getCustomModel() {
		return CustomMaterial.INFINITE_WATER_BUCKET.getCustomModel();
	}

	@Override
	public ItemStack getResult() {
		return getCustomModel().getItem();
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

			for (ItemStack itemStack : matrix)
				if (!isNullOrAir(itemStack))
					if (Material.BUCKET == itemStack.getType())
						itemStack.setType(Material.AIR);

			event.getInventory().setMatrix(matrix);
		});
	}

	@EventHandler
	public void onCraftWith(CraftItemEvent event) {
		ItemStack[] original = event.getInventory().getMatrix();
		if (event.getRecipe() instanceof ShapedRecipe shaped) {
			ItemStack[] items = shaped.getIngredientMap().values().toArray(new ItemStack[0]);
			boolean found = false;
			for (int i = 0; i < items.length; i++) {
				if (isNullOrAir(items[i])) continue;
				if (new ItemBuilder(items[i]).modelId() == new ItemBuilder(getResult()).modelId()) {
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
		if (isNullOrAir(clickedBlock))
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
		return CustomMaterial.of(item) == CustomMaterial.INFINITE_WATER_BUCKET;
	}

}

