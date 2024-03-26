package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.genericcrate;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Generic Crate",
		modelId = 20104
)
@CustomNoteBlockConfig(
		instrument = Instrument.BASS_GUITAR,
		step = 4
)
public class GenericCrateD implements IGenericCrate {
}
