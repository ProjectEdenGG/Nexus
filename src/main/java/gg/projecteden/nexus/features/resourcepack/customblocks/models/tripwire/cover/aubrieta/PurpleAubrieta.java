package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover.aubrieta;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover.ICover;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

@CustomBlockConfig(
	name = "Purple Aubrieta",
	itemModel = ItemModelType.FLORA_COVER_AUBRIETA_AUBRIETA_PURPLE_ITEM
)
@CustomTripwireConfig(
	north_NS = true,
	east_NS = false,
	south_NS = true,
	west_NS = false,
	attached_NS = true,
	disarmed_NS = false,
	powered_NS = false,
	customBreakSound = "block.azalea_leaves.break",
	customPlaceSound = "block.azalea_leaves.place",
	customStepSound = "block.azalea_leaves.step",
	customHitSound = "block.azalea_leaves.hit",
	customFallSound = "block.azalea_leaves.fall"
)
public class PurpleAubrieta implements ICover {}
