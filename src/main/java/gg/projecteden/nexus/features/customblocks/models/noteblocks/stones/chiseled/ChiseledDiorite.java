package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.chiseled;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Chiseled Diorite",
	modelId = 20354
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 5
)
public class ChiseledDiorite implements IChiseledStone {
}
