package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Blue Terracotta Shingles",
	modelId = 20208,
	instrument = Instrument.BIT,
	step = 8
)
public class BlueTerracottaShingles implements IColoredTerracottaShingles {}
