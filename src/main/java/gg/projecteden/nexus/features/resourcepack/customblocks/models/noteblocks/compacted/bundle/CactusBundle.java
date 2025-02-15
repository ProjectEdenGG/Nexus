package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.bundle;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Bundle of Cactus",
	itemModel = ItemModelType.CRATES_BUNDLE_CACTUS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_DRUM,
	step = 13
)
@DirectionalConfig(
	step_NS = 14,
	step_EW = 15
)
public class CactusBundle implements IBundle {
}
