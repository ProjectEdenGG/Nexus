package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Purple Quilted Wool",
	modelId = 20309,
	instrument = Instrument.COW_BELL,
	step = 9
)
public class PurpleQuiltedWool implements IQuiltedWool {}
