package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.IDirectionalNoteBlock;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ILantern extends IDirectionalNoteBlock {
	int LIGHT_LEVEL = 14; // There's too many edge cases if this is dynamic

	@NotNull
	default Material getMaterial() {
		final String woodType = getClass().getSimpleName()
			.replace("Paper", "")
			.replace("Shroom", "")
			.replace("Lantern", "");
		return Material.valueOf(StringUtils.camelToSnake(woodType).toUpperCase() + "_PLANKS");
	}

	@Override
	default double getBlockHardness() {
		return 1.0;
	}

	@Override
	default Material getMinimumPreferredTool() {
		return Material.WOODEN_AXE;
	}

	@Override
	default boolean requiresCorrectToolForDrops() {
		return true;
	}

	@Override
	default boolean requiresSilkTouchForDrops() {
		return true;
	}

	@Override
	default boolean prefersSilkTouchForDrops() {
		return true;
	}

	@Override
	default PistonPushAction getPistonPushedAction() {
		return PistonPushAction.PREVENT;
	}

	@Override
	default void placeBlock(Player player, EquipmentSlot hand, Block block, Block placeAgainst, BlockFace facing, ItemStack itemInHand) {
		_placeLight(block);
	}

	@Override
	default void breakBlock(@Nullable Player player, @Nullable ItemStack tool, Block origin) {
		boolean fixOrigin = false;
		for (Block _block : BlockUtils.getAdjacentBlocks(origin)) {
			// if one of the adjacent blocks is a lantern, set origin to light
			CustomBlock _customBlock = CustomBlock.from(_block);
			if (_customBlock != null && _customBlock.get() instanceof ILantern) {
				fixOrigin = true;
				continue;
			}

			if (_block.getType() != Material.LIGHT)
				continue;

			Levelled levelled = (Levelled) _block.getBlockData();
			if (levelled.getLevel() != LIGHT_LEVEL)
				continue;

			boolean setAir = true;
			for (Block __block : BlockUtils.getAdjacentBlocks(_block)) {
				if (origin.getLocation().equals(__block.getLocation()))
					continue;

				CustomBlock __customBlock = CustomBlock.from(__block);
				if (__customBlock == null)
					continue;

				if (!(__customBlock.get() instanceof ILantern))
					continue;


				setAir = false;
				break;
			}

			if (setAir)
				_block.setType(Material.AIR, false);
		}

		if (fixOrigin) {
			Tasks.wait(1, () -> setLight(origin));
		}
	}

	default void _placeLight(Block origin) {
		for (Block _block : BlockUtils.getAdjacentBlocks(origin, true)) {
			if (MaterialTag.AIR.isTagged(_block.getType())) {
				setLight(_block);
			}
		}
	}

	static void setLight(Block origin) {
		origin.setType(Material.LIGHT, false);
		Levelled levelled = (Levelled) origin.getBlockData();
		levelled.setLevel(LIGHT_LEVEL);
		origin.setBlockData(levelled, false);
	}
}
