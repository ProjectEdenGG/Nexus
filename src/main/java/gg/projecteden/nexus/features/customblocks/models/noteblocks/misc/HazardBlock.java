package gg.projecteden.nexus.features.customblocks.models.noteblocks.misc;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICustomNoteBlock;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Hazard Block",
	modelId = 20121
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_GUITAR,
	step = 21
)
public class HazardBlock implements ICustomNoteBlock {}
