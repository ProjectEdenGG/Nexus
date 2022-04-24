package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Yellow Terracotta Shingles",
	modelId = 20203
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 3
)
public class YellowTerracottaShingles implements IColoredTerracottaShingles {}
