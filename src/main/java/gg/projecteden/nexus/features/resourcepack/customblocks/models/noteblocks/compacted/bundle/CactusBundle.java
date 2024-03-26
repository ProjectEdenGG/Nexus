package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.bundle;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Bundle of Cactus",
		modelId = 20061
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
