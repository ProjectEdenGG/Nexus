package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.pebbles;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.Unobtainable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.CustomTripwireConfig;

@CustomBlockConfig(
	name = "Pebbles",
	material = CustomMaterial.ROCKS_PEBBLES_2
)
@CustomTripwireConfig(
		north_NS = false,
		east_NS = false,
		south_NS = true,
		west_NS = true,
		attached_NS = true,
		disarmed_NS = false,
		powered_NS = false
)
@Unobtainable
public class Pebbles_2 implements IPebbles {
}
