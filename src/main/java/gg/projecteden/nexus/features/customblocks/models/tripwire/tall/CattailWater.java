package gg.projecteden.nexus.features.customblocks.models.tripwire.tall;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;

@CustomBlockConfig(
	name = "Water Cattail",
	modelId = 21102
)
@CustomTripwireConfig(
	north_NS = false,
	south_NS = false,
	east_NS = true,
	west_NS = false,
	attached_NS = false,
	disarmed_NS = false,
	powered_NS = false,
	customBreakSound = "block.azalea_leaves.break",
	customPlaceSound = "block.azalea_leaves.place",
	customStepSound = "block.azalea_leaves.step",
	customHitSound = "block.azalea_leaves.hit",
	customFallSound = "block.azalea_leaves.fall"
)

public class CattailWater implements ITall {
// TODO: dynamic placement
//	@Override
//	public int getModelId() {
//		return 21101;
//	}

}
