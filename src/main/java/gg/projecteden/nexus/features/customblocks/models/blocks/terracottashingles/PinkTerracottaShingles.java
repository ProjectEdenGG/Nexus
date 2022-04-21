package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Pink Terracotta Shingles",
	modelId = 20211,
	instrument = Instrument.BIT,
	step = 11
)
public class PinkTerracottaShingles implements ITerracottaShingles {}
