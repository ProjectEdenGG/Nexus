package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.IReDyeable;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;

public interface IColoredTerracottaShingles extends ITerracottaShingles, IReDyeable {

	@Override
	default CustomBlockTag getReDyeTag() {
		return CustomBlockTag.COLORED_TERRACOTTA_SHINGLES;
	}

	@Override
	default Material getMaterial() {
		return Material.valueOf(StringUtils.camelToSnake(getClass().getSimpleName().replace("TerracottaShingles", "")).toUpperCase() + "_TERRACOTTA");
	}

}
