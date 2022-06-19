package gg.projecteden.nexus.features.customblocks.models.tripwire.tripwire;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.common.NonObtainable;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;

@CustomBlockConfig(
	name = "Tripwire Cross",
	modelId = 21001
)
@CustomTripwireConfig(
	north_NS = true,
	east_NS = true,
	south_NS = true,
	west_NS = true,
	attached_NS = false,
	disarmed_NS = false,
	powered_NS = false,
	ignorePowered = true
)
@NonObtainable
public class TripwireCross implements ICustomTripwire {

	@Override
	public @NonNull ItemBuilder getItemBuilder() {
		// same as CustomBlock.TRIPWIRE
		return new ItemBuilder(CustomMaterial.TRIPWIRE_CROSS).name("String");
	}
}
