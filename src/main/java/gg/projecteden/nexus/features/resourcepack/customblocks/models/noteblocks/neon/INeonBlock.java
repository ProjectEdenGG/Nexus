package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.neon;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ILightableNoteBlock;
import org.bukkit.Material;

public interface INeonBlock extends ICustomNoteBlock, ILightableNoteBlock {

	@Override
	default double getBlockHardness() {
		return 1.5;
	}

	@Override
	default Material getMinimumPreferredTool() {
		return Material.WOODEN_PICKAXE;
	}
}
