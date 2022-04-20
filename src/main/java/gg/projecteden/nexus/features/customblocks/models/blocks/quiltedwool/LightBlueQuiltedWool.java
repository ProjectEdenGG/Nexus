package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IQuiltedWool;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Blue Quilted Wool",
	modelId = 20307,
	instrument = Instrument.COW_BELL,
	step = 7
)
public class LightBlueQuiltedWool implements IQuiltedWool {}
