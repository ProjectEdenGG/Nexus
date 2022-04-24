package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Purple Terracotta Shingles",
	modelId = 20209
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 9
)
public class PurpleTerracottaShingles implements IColoredTerracottaShingles {}
