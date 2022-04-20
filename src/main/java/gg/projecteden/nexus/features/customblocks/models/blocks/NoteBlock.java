package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Generic Crate",
	modelId = 20000,
	instrument = Instrument.PIANO,
	step = 0
)
public class NoteBlock implements ICustomBlock {}
