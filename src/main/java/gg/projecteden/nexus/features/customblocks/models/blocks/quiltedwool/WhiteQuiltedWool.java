package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "White Quilted Wool",
	modelId = 20316,
	instrument = Instrument.COW_BELL,
	step = 16
)
public class WhiteQuiltedWool implements IQuiltedWool {}
