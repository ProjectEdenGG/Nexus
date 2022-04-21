package gg.projecteden.nexus.features.customblocks.models.blocks.compacted.crate;

import gg.projecteden.nexus.features.customblocks.models.blocks.compacted.ICompacted;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.utils.StringUtils.camelToSnake;

public interface ICrate extends ICompacted {

	default @NotNull Material getMaterial() {
		return Material.valueOf(camelToSnake(getClass().getSimpleName().replace("Crate", "")).toUpperCase());
	}
	
}
