package gg.projecteden.nexus.features.customblocks.models.tripwire.tripwire;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.common.NonObtainable;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.IDirectionalTripwire;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;

@CustomBlockConfig(
	name = "Tripwire",
	modelId = 21000
)
@CustomTripwireConfig(
	north_NS = true,
	east_NS = false,
	south_NS = true,
	west_NS = false,
	attached_NS = false,
	disarmed_NS = false,
	powered_NS = false,
	ignorePowered = true
)
@DirectionalConfig(
	north_EW = false,
	east_EW = true,
	south_EW = false,
	west_EW = true,
	attached_EW = false,
	disarmed_EW = false,
	powered_EW = false,
	ignorePowered = true
)
@NonObtainable
public class Tripwire implements IDirectionalTripwire {

	@Override
	public @NonNull ItemBuilder getItemBuilder() {
		return new ItemBuilder(itemMaterial).customModelData(getModelId()).name("String");
	}
}
