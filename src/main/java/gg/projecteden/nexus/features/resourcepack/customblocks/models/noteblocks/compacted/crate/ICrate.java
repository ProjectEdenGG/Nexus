package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.crate;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.ICompacted;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public interface ICrate extends ICompacted {

	default @NotNull Material getMaterial() {
		return Material.valueOf(StringUtils.camelToSnake(getClass().getSimpleName().replace("Crate", "")).toUpperCase());
	}

	@Override
	default double getBlockHardness() {
		return 0.6;
	}

	@Override
	default boolean requiresCorrectToolForDrops() {
		return false;
	}

}
