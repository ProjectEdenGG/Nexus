package gg.projecteden.nexus.features.customblocks.models.tripwire.cover.aubrieta;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.cover.ICover;

@CustomBlockConfig(
	name = "Orange Aubrieta",
	modelId = 21109
)
@CustomTripwireConfig(
	north_NS = false,
	east_NS = false,
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
public class OrangeAubrieta implements ICover {}
