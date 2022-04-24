package gg.projecteden.nexus.features.customblocks.models.tripwire;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.ICustomTripwire;

@CustomBlockConfig(
	name = "Tripwire",
	modelId = 21000
)
@CustomTripwireConfig(
	north_NS = true,
	south_NS = true,
	east_NS = true,
	west_NS = true,
	attached_NS = false,
	disarmed_NS = false,
	powered_NS = false,
	ignorePowered = true
)
public class TripwireCross implements ICustomTripwire {}
