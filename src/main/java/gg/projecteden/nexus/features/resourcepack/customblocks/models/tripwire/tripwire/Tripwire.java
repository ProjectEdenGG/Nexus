package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tripwire;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.Unobtainable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.DirectionalConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IDirectionalTripwire;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;

@CustomBlockConfig(
	name = "Tripwire",
	material = CustomMaterial.TRIPWIRE
)
@CustomTripwireConfig(
	attached_NS = false,
	disarmed_NS = false,
	north_NS = true,
	east_NS = false,
	south_NS = true,
	west_NS = false,
	powered_NS = false,
	ignorePowered = true
)
@DirectionalConfig(
	attached_EW = false,
	disarmed_EW = false,
	north_EW = false,
	east_EW = true,
	south_EW = false,
	west_EW = true,
	powered_EW = false,
	ignorePowered = true
)

@Unobtainable
public class Tripwire implements IDirectionalTripwire {

	@Override
	public @NonNull ItemBuilder getItemBuilder() {
		return new ItemBuilder(getVanillaItemMaterial());
	}

	@Override
	public boolean requiresCorrectToolForDrops() {
		return false;
	}
}
