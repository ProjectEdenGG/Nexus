package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Cyan Quilted Wool",
	modelId = 20306,
	instrument = Instrument.COW_BELL,
	step = 6
)
public class CyanQuiltedWool implements IQuiltedWool {}
