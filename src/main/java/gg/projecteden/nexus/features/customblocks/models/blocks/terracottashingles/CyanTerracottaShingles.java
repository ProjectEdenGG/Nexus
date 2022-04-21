package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Cyan Terracotta Shingles",
	modelId = 20206,
	instrument = Instrument.BIT,
	step = 6
)
public class CyanTerracottaShingles implements IColoredTerracottaShingles {}
