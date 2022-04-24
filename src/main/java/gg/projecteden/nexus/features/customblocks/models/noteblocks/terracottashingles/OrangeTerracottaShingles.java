package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Orange Terracotta Shingles",
	modelId = 20202
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 2
)
public class OrangeTerracottaShingles implements IColoredTerracottaShingles {}
