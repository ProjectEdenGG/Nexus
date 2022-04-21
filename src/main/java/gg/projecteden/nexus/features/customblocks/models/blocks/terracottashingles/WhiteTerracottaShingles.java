package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "White Terracotta Shingles",
	modelId = 20216,
	instrument = Instrument.BIT,
	step = 16
)
public class WhiteTerracottaShingles implements IColoredTerracottaShingles {}
