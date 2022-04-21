package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Lime Terracotta Shingles",
	modelId = 20204,
	instrument = Instrument.BIT,
	step = 4
)
public class LimeTerracottaShingles implements ITerracottaShingles {}
