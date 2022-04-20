package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IQuiltedWool;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Black Quilted Wool",
	modelId = 20313,
	instrument = Instrument.COW_BELL,
	step = 13
)
public class BlackQuiltedWool implements IQuiltedWool {}
