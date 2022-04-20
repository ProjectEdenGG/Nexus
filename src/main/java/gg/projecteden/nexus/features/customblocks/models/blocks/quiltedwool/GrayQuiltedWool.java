package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IQuiltedWool;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Gray Quilted Wool",
	modelId = 20314,
	instrument = Instrument.COW_BELL,
	step = 14
)
public class GrayQuiltedWool implements IQuiltedWool {}
