package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Magenta Terracotta Shingles",
	modelId = 20210
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 10
)
public class MagentaTerracottaShingles implements IColoredTerracottaShingles {}
