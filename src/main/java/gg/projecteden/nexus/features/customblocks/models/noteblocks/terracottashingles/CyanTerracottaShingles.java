package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Cyan Terracotta Shingles",
	modelId = 20206
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 6
)
public class CyanTerracottaShingles implements IColoredTerracottaShingles {}
