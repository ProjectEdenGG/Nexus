package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Black Terracotta Shingles",
	modelId = 20213,
	instrument = Instrument.BIT,
	step = 13
)
public class BlackTerracottaShingles implements ITerracottaShingles {}
