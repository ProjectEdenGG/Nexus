package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.bundle;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.IDirectionalNoteBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.ICompacted;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public interface IBundle extends ICompacted, IDirectionalNoteBlock {

	default @NotNull Material getMaterial() {
		return Material.valueOf(StringUtils.camelToSnake(getClass().getSimpleName().replace("Bundle", "")).toUpperCase());
	}

	@Override
	default double getBlockHardness() {
		return 0.5;
	}

	@Override
	default boolean requiresCorrectToolForDrops() {
		return false;
	}

}
