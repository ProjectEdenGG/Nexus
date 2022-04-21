package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Blue Quilted Wool",
	modelId = 20308,
	instrument = Instrument.COW_BELL,
	step = 8
)
public class BlueQuiltedWool implements IQuiltedWool {}
