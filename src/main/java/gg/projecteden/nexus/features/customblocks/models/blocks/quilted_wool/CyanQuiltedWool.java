package gg.projecteden.nexus.features.customblocks.models.blocks.quilted_wool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IQuiltedWool;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Cyan Quilted Wool",
	modelId = 20306,
	instrument = Instrument.COW_BELL,
	step = 6
)
public class CyanQuiltedWool implements IQuiltedWool { }
