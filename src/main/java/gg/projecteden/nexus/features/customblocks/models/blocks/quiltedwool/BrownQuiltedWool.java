package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IQuiltedWool;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Brown Quilted Wool",
	modelId = 20312,
	instrument = Instrument.COW_BELL,
	step = 12
)
public class BrownQuiltedWool implements IQuiltedWool {}
