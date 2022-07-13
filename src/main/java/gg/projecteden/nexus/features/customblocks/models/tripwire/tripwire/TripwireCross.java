package gg.projecteden.nexus.features.customblocks.models.tripwire.tripwire;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.common.Unobtainable;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.ICustomTripwire;
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

@Unobtainable
public class TripwireCross implements ICustomTripwire {

	@Override
	public @NonNull ItemBuilder getItemBuilder() {
		return new ItemBuilder(getVanillaItemMaterial());
	}

	@Override
	public boolean requiresCorrectToolForDrops() {
		return false;
	}
}
