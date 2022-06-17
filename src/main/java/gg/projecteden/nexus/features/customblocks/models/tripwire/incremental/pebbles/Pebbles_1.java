package gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.pebbles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.common.NonObtainable;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;

@CustomBlockConfig(
	name = "Pebbles",
	modelId = 21107
)
@CustomTripwireConfig(
	north_NS = false,
	east_NS = false,
	south_NS = false,
	west_NS = true,
	attached_NS = true,
	disarmed_NS = false,
	powered_NS = false
)
@NonObtainable
public class Pebbles_1 implements IPebbles {}
