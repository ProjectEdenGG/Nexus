package gg.projecteden.nexus.features.customblocks.models.tripwire;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.ICustomTripwire;

@CustomBlockConfig(
	name = "Tall Support",
	modelId = 21001 // TODO
)
@CustomTripwireConfig(
	north_NS = false,
	south_NS = false,
	east_NS = false,
	west_NS = false,
	attached_NS = false,
	disarmed_NS = false,
	powered_NS = false,
	ignorePowered = true
)
public class TallSupport implements ICustomTripwire {}
