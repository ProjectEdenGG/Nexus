package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Purple Terracotta Shingles",
	modelId = 20209,
	instrument = Instrument.BIT,
	step = 9
)
public class PurpleTerracottaShingles implements ITerracottaShingles {}
