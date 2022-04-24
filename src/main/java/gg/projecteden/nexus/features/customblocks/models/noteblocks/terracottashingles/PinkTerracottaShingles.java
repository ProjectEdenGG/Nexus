package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Pink Terracotta Shingles",
	modelId = 20211
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 11
)
public class PinkTerracottaShingles implements IColoredTerracottaShingles {}
