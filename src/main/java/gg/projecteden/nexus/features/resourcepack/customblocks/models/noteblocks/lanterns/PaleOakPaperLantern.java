package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Pale Oak Paper Lantern",
	itemModel = ItemModelType.LANTERNS_PAPER_PALE_OAK_PAPER_LANTERN
)
@CustomNoteBlockConfig(
	instrument = Instrument.GUITAR,
	step = 7
)
@DirectionalConfig(
	step_NS = 8,
	step_EW = 9
)
public class PaleOakPaperLantern implements IPaperLantern {
}
