package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IQuiltedWool;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Green Quilted Wool",
	modelId = 20305,
	instrument = Instrument.COW_BELL,
	step = 5
)
public class GreenQuiltedWool implements IQuiltedWool {}
