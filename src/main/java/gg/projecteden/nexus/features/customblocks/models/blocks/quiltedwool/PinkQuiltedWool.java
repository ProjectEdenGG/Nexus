package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Pink Quilted Wool",
	modelId = 20311,
	instrument = Instrument.COW_BELL,
	step = 11
)
public class PinkQuiltedWool implements IQuiltedWool {}
