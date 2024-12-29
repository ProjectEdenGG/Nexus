package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.IDirectionalNoteBlock;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public interface ILantern extends IDirectionalNoteBlock {

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
		return 0.8;
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
}
