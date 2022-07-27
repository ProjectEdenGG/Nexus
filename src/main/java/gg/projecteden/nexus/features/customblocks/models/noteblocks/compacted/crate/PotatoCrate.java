package gg.projecteden.nexus.features.customblocks.models.noteblocks.compacted.crate;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Crate of Potatoes",
	modelId = 20055
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_DRUM,
	step = 5
)
public class PotatoCrate implements ICrate {}
