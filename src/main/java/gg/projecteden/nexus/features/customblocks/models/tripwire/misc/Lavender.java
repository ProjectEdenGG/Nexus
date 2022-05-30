package gg.projecteden.nexus.features.customblocks.models.tripwire.misc;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.ICustomTripwire;

@CustomBlockConfig(
	name = "Lavender",
	modelId = 21117
)
@CustomTripwireConfig(
	north_NS = true,
	east_NS = false,
	south_NS = false,
	west_NS = true,
	attached_NS = false,
	disarmed_NS = false,
	powered_NS = false,
	customBreakSound = "block.azalea_leaves.break",
	customPlaceSound = "block.azalea_leaves.place",
	customStepSound = "block.azalea_leaves.step",
	customHitSound = "block.azalea_leaves.hit",
	customFallSound = "block.azalea_leaves.fall"
)
public class Lavender implements ICustomTripwire {
}
