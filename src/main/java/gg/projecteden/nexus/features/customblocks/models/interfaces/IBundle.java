package gg.projecteden.nexus.features.customblocks.models.interfaces;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.utils.StringUtils.camelToSnake;

public interface IBundle extends ICompacted, IDirectional {

	default @NotNull Material getMaterial() {
		return Material.valueOf(camelToSnake(getClass().getSimpleName().replace("Bundle", "")).toUpperCase());
	}

}
