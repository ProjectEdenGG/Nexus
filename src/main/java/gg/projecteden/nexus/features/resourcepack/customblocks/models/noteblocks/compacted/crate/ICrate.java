package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.crate;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.ICompacted;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.utils.StringUtils.camelToSnake;

public interface ICrate extends ICompacted {

	default @NotNull Material getMaterial() {
		return Material.valueOf(camelToSnake(getClass().getSimpleName().replace("Crate", "")).toUpperCase());
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
