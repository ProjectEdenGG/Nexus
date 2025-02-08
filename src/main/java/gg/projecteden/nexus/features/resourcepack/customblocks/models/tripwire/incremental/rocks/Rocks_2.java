package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.rocks;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.Unobtainable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.CustomTripwireConfig;

@CustomBlockConfig(
	name = "Rocks",
	material = CustomMaterial.ROCKS_ROCKS_2
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
@Unobtainable
public class Rocks_2 implements IRocks {
}
