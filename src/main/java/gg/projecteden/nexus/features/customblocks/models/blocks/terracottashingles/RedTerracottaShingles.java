package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Red Terracotta Shingles",
	modelId = 20201,
	instrument = Instrument.BIT,
	step = 1
)
public class RedTerracottaShingles implements ITerracottaShingles {}
