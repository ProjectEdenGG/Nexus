package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.IDyeable;
import org.bukkit.Material;

import static gg.projecteden.nexus.utils.StringUtils.camelToSnake;

public interface IColoredTerracottaShingles extends ITerracottaShingles, IDyeable {

	@Override
	default CustomBlockTag getRedyeTag() {
		return CustomBlockTag.COLORED_TERRACOTTA_SHINGLES;
	}

	@Override
	default Material getMaterial() {
		return Material.valueOf(camelToSnake(getClass().getSimpleName().replace("TerracottaShingles", "")).toUpperCase() + "_TERRACOTTA");
	}

}
