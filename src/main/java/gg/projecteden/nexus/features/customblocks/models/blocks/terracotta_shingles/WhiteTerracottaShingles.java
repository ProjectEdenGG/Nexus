package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "White Terracotta Shingles",
	modelId = 20216,
	instrument = Instrument.BIT,
	step = 16
)
public class WhiteTerracottaShingles implements ITerracottaShingles {}
