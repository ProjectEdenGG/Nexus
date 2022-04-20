package gg.projecteden.nexus.features.customblocks.models.interfaces;

import gg.projecteden.nexus.features.customblocks.models.CustomBlockTag;

public interface IColoredPlanks extends IDyeable {

	@Override
	default CustomBlockTag getRedyeTag() {
		return CustomBlockTag.QUILTED_WOOL;
	}

}
