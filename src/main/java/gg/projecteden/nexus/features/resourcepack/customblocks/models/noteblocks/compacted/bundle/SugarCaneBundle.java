package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.bundle;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Bundle of Sugar Cane",
	itemModel = ItemModelType.CRATES_BUNDLE_SUGAR_CANE
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_DRUM,
	step = 19
)
@DirectionalConfig(
	step_NS = 20,
	step_EW = 21
)
public class SugarCaneBundle implements IBundle {
}
