package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Purple Terracotta Shingles",
	modelId = 20209,
	instrument = Instrument.BIT,
	step = 9
)
public class PurpleTerracottaShingles implements ITerracottaShingles {}
