package gg.projecteden.nexus.features.customblocks.models.blocks.quilted_wool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IQuiltedWool;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Pink Quilted Wool",
	modelId = 20311,
	instrument = Instrument.COW_BELL,
	step = 11
)
public class PinkQuiltedWool implements IQuiltedWool { }
