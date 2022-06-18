package gg.projecteden.nexus.features.customblocks.models.noteblocks.genericcrate;

import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICustomNoteBlock;
import org.bukkit.Material;

public interface IGenericCrate extends ICustomNoteBlock {
	@Override
	default double getBlockHardness() {
		return 2.0;
	}

	@Override
	default Material getMinimumPreferredTool() {
		return Material.WOODEN_AXE;
	}
}
