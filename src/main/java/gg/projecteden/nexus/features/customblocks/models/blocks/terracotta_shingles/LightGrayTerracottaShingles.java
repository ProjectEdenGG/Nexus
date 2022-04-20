package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Gray Terracotta Shingles",
	modelId = 20215,
	instrument = Instrument.BIT,
	step = 15
)
public class LightGrayTerracottaShingles implements ITerracottaShingles {}
