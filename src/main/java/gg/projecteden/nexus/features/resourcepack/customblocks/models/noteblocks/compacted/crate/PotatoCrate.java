package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.crate;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Crate of Potatoes",
	itemModel = ItemModelType.CRATES_CRATE_POTATO
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_DRUM,
	step = 5
)
public class PotatoCrate implements ICrate {
}
