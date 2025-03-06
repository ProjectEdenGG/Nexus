package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.crate;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Crate of Beetroot",
	itemModel = ItemModelType.CRATES_CRATE_BEET
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_DRUM,
	step = 2,
	customStepSound = "block.mud.step"
)
public class BeetrootCrate implements ICrate {
}
