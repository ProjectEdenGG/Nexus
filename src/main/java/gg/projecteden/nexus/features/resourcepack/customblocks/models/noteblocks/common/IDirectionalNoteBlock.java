package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.IDirectional;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;

public interface IDirectionalNoteBlock extends ICustomNoteBlock, IDirectional {
	default DirectionalConfig getDirectionalConfig() {
		return getClass().getAnnotation(DirectionalConfig.class);
	}

	default int getNoteBlockStep_NS(){
		return getDirectionalConfig().step_NS();
	}

	default int getNoteBlockStep_EW(){
		return getDirectionalConfig().step_EW();
	}

	@Override
	default BlockFace getFacing(Block block) {
		if (block.getBlockData() instanceof NoteBlock) {
			NoteBlockData data = new NoteBlockData(block);
			if (getNoteBlockStep_EW() == data.getStep())
				return BlockFace.EAST;

			if (getNoteBlockStep_NS() == data.getStep())
				return BlockFace.NORTH;
		}

		return BlockFace.UP;
	}
}
