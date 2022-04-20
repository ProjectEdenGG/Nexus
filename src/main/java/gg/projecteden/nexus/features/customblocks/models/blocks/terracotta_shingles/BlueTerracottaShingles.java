package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Blue Terracotta Shingles",
	modelId = 20208,
	instrument = Instrument.BIT,
	step = 8
)
public class BlueTerracottaShingles implements ITerracottaShingles {}
