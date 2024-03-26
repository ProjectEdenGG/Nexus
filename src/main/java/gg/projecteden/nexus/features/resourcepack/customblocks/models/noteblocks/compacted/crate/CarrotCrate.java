package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.crate;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Crate of Carrots",
		modelId = 20054
)
@CustomNoteBlockConfig(
		instrument = Instrument.BASS_DRUM,
		step = 4
)
public class CarrotCrate implements ICrate {
}
