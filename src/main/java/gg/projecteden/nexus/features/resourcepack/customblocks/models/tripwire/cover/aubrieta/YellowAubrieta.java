package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover.aubrieta;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover.ICover;

@CustomBlockConfig(
	name = "Yellow Aubrieta",
	modelId = 21111
)
@CustomTripwireConfig(
	north_NS = false,
	east_NS = true,
	south_NS = false,
	west_NS = false,
	attached_NS = true,
	disarmed_NS = false,
	powered_NS = false,
	customBreakSound = "block.azalea_leaves.break",
	customPlaceSound = "block.azalea_leaves.place",
	customStepSound = "block.azalea_leaves.step",
	customHitSound = "block.azalea_leaves.hit",
	customFallSound = "block.azalea_leaves.fall"
)
public class YellowAubrieta implements ICover {}
