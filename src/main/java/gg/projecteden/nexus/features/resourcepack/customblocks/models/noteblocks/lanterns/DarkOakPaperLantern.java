package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Dark Oak Paper Lantern",
	itemModel = ItemModelType.LANTERNS_PAPER_DARK_OAK_PAPER_LANTERN
)
@CustomNoteBlockConfig(
	instrument = Instrument.FLUTE,
	step = 16
)
@DirectionalConfig(
	step_NS = 17,
	step_EW = 18
)
public class DarkOakPaperLantern implements IPaperLantern {
}
