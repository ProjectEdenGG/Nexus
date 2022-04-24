package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Black Terracotta Shingles",
	modelId = 20213
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 13
)
public class BlackTerracottaShingles implements IColoredTerracottaShingles {}
