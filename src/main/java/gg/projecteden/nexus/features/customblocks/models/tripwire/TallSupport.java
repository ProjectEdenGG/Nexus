package gg.projecteden.nexus.features.customblocks.models.tripwire;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;

@CustomBlockConfig(
	name = "Tall Support",
	modelId = 21100
)
@CustomTripwireConfig(
	north_NS = false,
	south_NS = false,
	east_NS = false,
	west_NS = false,
	attached_NS = false,
	disarmed_NS = false,
	powered_NS = false,
	ignorePowered = true
)

// This is not a block you can obtain, it's purpose is to allow for better hitbox detection for tall blocks
public class TallSupport implements ICustomTripwire {

	@Override
	public @NonNull ItemBuilder getItemBuilder() {
		return new ItemBuilder(Material.AIR);
	}
}
