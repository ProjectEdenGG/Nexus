package gg.projecteden.nexus.features.customblocks.models.noteblocks.misc;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICustomNoteBlock;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Note Block",
	modelId = 20000
)
@CustomNoteBlockConfig(
	instrument = Instrument.PIANO,
	step = 0
)
public class NoteBlock implements ICustomNoteBlock {}
