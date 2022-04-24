package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Green Terracotta Shingles",
	modelId = 20205
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 5
)
public class GreenTerracottaShingles implements IColoredTerracottaShingles {}
