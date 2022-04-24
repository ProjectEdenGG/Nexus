package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "White Terracotta Shingles",
	modelId = 20216
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 16
)
public class WhiteTerracottaShingles implements IColoredTerracottaShingles {}
