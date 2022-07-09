package gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.rocks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.common.Unobtainable;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;

@CustomBlockConfig(
	name = "Rocks",
	modelId = 21104
)
@CustomTripwireConfig(
	north_NS = false,
	east_NS = false,
	south_NS = true,
	west_NS = false,
	attached_NS = false,
	disarmed_NS = false,
	powered_NS = false
)
@Unobtainable
public class Rocks_1 implements IRocks {}