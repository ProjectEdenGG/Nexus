package gg.projecteden.nexus.features.customblocks.models.tripwire.tall;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;

@CustomBlockConfig(
	name = "Cattail",
	modelId = 21101
)
@CustomTripwireConfig(
	north_NS = true,
	south_NS = false,
	east_NS = false,
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
public class Cattail implements ITall {
}
