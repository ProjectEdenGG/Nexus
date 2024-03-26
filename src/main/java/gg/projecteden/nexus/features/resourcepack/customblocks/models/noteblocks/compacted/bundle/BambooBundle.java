package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.bundle;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Bundle of Bamboo",
		modelId = 20060
)
@CustomNoteBlockConfig(
		instrument = Instrument.BASS_DRUM,
		step = 10
)
@DirectionalConfig(
		step_NS = 11,
		step_EW = 12
)
public class BambooBundle implements IBundle {
}
