package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.chiseled;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Chiseled Andesite",
	modelId = 20352
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 3
)
public class ChiseledAndesite implements IChiseledStone {
}
