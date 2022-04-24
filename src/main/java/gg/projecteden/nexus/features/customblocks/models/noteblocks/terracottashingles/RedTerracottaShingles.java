package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Red Terracotta Shingles",
	modelId = 20201
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 1
)
public class RedTerracottaShingles implements IColoredTerracottaShingles {}
