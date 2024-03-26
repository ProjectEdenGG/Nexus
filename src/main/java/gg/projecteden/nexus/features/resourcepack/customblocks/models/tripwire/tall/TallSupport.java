package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.Unobtainable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;

@CustomBlockConfig(
	name = "Tall Support",
	modelId = 21100
)
@CustomTripwireConfig(
	north_NS = false,
	east_NS = false,
	south_NS = false,
	west_NS = false,
	attached_NS = false,
	disarmed_NS = false,
	powered_NS = false,
	ignorePowered = true
)

// Purpose: allow for better hitbox detection for tall blocks
@Unobtainable
public class TallSupport implements ICustomTripwire {

	@Override
	public @NonNull ItemBuilder getItemBuilder() {
		return new ItemBuilder(Material.AIR);
	}
}
