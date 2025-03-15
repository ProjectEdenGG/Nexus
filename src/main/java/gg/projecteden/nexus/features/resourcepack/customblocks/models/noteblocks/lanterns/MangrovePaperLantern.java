package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Mangrove Paper Lantern",
	itemModel = ItemModelType.LANTERNS_PAPER_MANGROVE_PAPER_LANTERN
)
@CustomNoteBlockConfig(
	instrument = Instrument.GUITAR,
	step = 10
)
@DirectionalConfig(
	step_NS = 11,
	step_EW = 12
)
public class MangrovePaperLantern implements IPaperLantern {
}
