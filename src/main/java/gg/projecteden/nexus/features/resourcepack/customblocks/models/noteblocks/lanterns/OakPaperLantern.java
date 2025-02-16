package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Oak Paper Lantern",
	itemModel = ItemModelType.LANTERNS_PAPER_OAK_PAPER_LANTERN
)
@CustomNoteBlockConfig(
	instrument = Instrument.FLUTE,
	step = 1
)
@DirectionalConfig(
	step_NS = 2,
	step_EW = 3
)
public class OakPaperLantern implements IPaperLantern {
}
