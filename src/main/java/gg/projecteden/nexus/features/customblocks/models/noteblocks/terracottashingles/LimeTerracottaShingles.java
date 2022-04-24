package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Lime Terracotta Shingles",
	modelId = 20204
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 4
)
public class LimeTerracottaShingles implements IColoredTerracottaShingles {}
