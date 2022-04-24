package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Brown Terracotta Shingles",
	modelId = 20212
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 12
)
public class BrownTerracottaShingles implements IColoredTerracottaShingles {}
