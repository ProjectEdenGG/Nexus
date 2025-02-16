package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.crate;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Crate of Apples",
	itemModel = ItemModelType.BLOCKS_CRATE_APPLE
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_DRUM,
	step = 1
)
public class AppleCrate implements ICrate {
}
