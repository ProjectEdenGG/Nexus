package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.pebbles;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.Unobtainable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.CustomTripwireConfig;

@CustomBlockConfig(
		name = "Pebbles",
		modelId = 21107
)
@CustomTripwireConfig(
		north_NS = true,
		east_NS = true,
		south_NS = false,
		west_NS = true,
		attached_NS = true,
		disarmed_NS = false,
		powered_NS = false
)
@Unobtainable
public class Pebbles_1 implements IPebbles {
}
