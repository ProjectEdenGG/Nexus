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
	powered_NS = false
)
public class Cattail implements ITall {}
