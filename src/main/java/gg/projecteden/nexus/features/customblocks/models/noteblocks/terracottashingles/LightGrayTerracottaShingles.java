package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Gray Terracotta Shingles",
	modelId = 20215
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 15
)
public class LightGrayTerracottaShingles implements IColoredTerracottaShingles {}
