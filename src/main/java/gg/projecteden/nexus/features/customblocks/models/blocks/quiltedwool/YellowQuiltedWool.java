package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IQuiltedWool;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Yellow Quilted Wool",
	modelId = 20303,
	instrument = Instrument.COW_BELL,
	step = 3
)
public class YellowQuiltedWool implements IQuiltedWool {}
