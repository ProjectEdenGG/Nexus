package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Red Quilted Wool",
	modelId = 20301,
	instrument = Instrument.COW_BELL,
	step = 1
)
public class RedQuiltedWool implements IQuiltedWool {}
