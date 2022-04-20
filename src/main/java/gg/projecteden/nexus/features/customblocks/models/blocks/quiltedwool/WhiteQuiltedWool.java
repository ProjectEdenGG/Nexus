package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IQuiltedWool;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "White Quilted Wool",
	modelId = 20316,
	instrument = Instrument.COW_BELL,
	step = 16
)
public class WhiteQuiltedWool implements IQuiltedWool {}
