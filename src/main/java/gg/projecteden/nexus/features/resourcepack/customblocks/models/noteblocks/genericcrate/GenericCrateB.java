package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.genericcrate;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Generic Crate B",
	itemModel = ItemModelType.CRATES_GENERIC_2
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_GUITAR,
	step = 2
)
public class GenericCrateB implements IGenericCrate {
}
