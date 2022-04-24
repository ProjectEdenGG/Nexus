package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.chiseled;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Chiseled Granite",
	modelId = 20356
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 7
)
public class ChiseledGranite implements IChiseledStone {
}
