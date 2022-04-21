package gg.projecteden.nexus.features.customblocks.models.blocks.misc;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Note Block",
	modelId = 20000,
	instrument = Instrument.PIANO,
	step = 0
)
public class NoteBlock implements ICustomBlock {}
