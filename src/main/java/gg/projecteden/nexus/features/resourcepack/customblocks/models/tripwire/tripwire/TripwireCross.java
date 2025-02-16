package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tripwire;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.Unobtainable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;

@CustomBlockConfig(
	name = "Tripwire Cross",
	itemModel = ItemModelType.TRIPWIRE
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
public class TripwireCross implements IActualTripwire {

	@Override
	public @NonNull ItemBuilder getItemBuilder() {
		return new ItemBuilder(getVanillaItemMaterial());
	}

	@Override
	public boolean requiresCorrectToolForDrops() {
		return false;
	}
}
