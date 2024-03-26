package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import org.bukkit.Material;

public interface IPlanks extends ICraftableNoteBlock {

	@Override
	default double getBlockHardness() {
		return 2.0;
	}

	@Override
	default Material getMinimumPreferredTool() {
		return Material.WOODEN_AXE;
	}

	@Override
	default boolean requiresCorrectToolForDrops() {
		return false;
	}


}
