package gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.rocks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;

@CustomBlockConfig(
	name = "Rocks",
	modelId = 21105
)
@CustomTripwireConfig(
	north_NS = false,
	east_NS = true,
	south_NS = true,
	west_NS = false,
	attached_NS = false,
	disarmed_NS = false,
	powered_NS = false
)
public class Rocks_2 implements IRocks {}
