package gg.projecteden.nexus.features.customblocks.models.blocks.quilted_wool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IQuiltedWool;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Purple Quilted Wool",
	modelId = 20309,
	instrument = Instrument.COW_BELL,
	step = 9
)
public class PurpleQuiltedWool implements IQuiltedWool { }
