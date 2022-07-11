package gg.projecteden.nexus.features.customblocks.models.noteblocks.planks;

import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICraftableNoteBlock;

public interface IPlanks extends ICraftableNoteBlock {

	@Override
	default double getBlockHardness() {
		return 2.0;
	}

	@Override
	default boolean requiresCorrectToolForDrops() {
		return false;
	}


}
