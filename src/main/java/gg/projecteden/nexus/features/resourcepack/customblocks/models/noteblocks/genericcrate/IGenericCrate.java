package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.genericcrate;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;

public interface IGenericCrate extends ICustomNoteBlock {
	@Override
	default double getBlockHardness() {
		return 2.0;
	}

	@Override
	default boolean requiresCorrectToolForDrops() {
		return false;
	}

}
