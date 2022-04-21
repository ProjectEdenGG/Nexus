package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Lime Quilted Wool",
	modelId = 20304,
	instrument = Instrument.COW_BELL,
	step = 4
)
public class LimeQuiltedWool implements IQuiltedWool {}
