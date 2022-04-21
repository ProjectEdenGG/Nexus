package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Orange Quilted Wool",
	modelId = 20302,
	instrument = Instrument.COW_BELL,
	step = 2
)
public class OrangeQuiltedWool implements IQuiltedWool {}
