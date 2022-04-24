package gg.projecteden.nexus.features.customblocks.models.tripwire;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.IDirectionalTripwire;

@CustomBlockConfig(
	name = "Tripwire",
	modelId = 21000
)
@CustomTripwireConfig(
	north_NS = true,
	south_NS = true,
	east_NS = false,
	west_NS = false,
	attached_NS = false,
	disarmed_NS = false,
	powered_NS = false,
	ignorePowered = true
)
@DirectionalConfig(
	north_EW = false,
	south_EW = false,
	east_EW = true,
	west_EW = true,
	attached_EW = false,
	disarmed_EW = false,
	powered_EW = false,
	ignorePowered = true
)
public class Tripwire implements IDirectionalTripwire {}
