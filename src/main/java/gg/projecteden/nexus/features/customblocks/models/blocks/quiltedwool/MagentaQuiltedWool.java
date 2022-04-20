package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IQuiltedWool;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Magenta Quilted Wool",
	modelId = 20310,
	instrument = Instrument.COW_BELL,
	step = 10
)
public class MagentaQuiltedWool implements IQuiltedWool {}
