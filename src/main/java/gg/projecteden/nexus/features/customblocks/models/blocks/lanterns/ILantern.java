package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.IDirectional;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.utils.StringUtils.camelToSnake;

public interface ILantern extends IDirectional {

	@NotNull
	default Material getMaterial() {
		final String woodType = getClass().getSimpleName()
			.replace("Paper", "")
			.replace("Shroom", "")
			.replace("Lantern", "");
		return Material.valueOf(camelToSnake(woodType).toUpperCase() + "_PLANKS");
	}

}
