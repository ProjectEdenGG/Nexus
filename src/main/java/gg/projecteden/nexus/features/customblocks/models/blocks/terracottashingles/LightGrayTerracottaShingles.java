package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Gray Terracotta Shingles",
	modelId = 20215,
	instrument = Instrument.BIT,
	step = 15
)
public class LightGrayTerracottaShingles implements IColoredTerracottaShingles {
}
