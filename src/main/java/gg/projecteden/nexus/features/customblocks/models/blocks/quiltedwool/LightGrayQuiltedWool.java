package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IQuiltedWool;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Gray Quilted Wool",
	modelId = 20315,
	instrument = Instrument.COW_BELL,
	step = 15
)
public class LightGrayQuiltedWool implements IQuiltedWool {}
