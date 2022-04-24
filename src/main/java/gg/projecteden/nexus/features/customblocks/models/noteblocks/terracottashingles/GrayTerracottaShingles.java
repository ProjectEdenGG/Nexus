package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Gray Terracotta Shingles",
	modelId = 20214
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 14
)
public class GrayTerracottaShingles implements IColoredTerracottaShingles {}
