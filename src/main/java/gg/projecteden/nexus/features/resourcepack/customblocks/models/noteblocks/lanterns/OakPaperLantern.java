package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Oak Paper Lantern",
		modelId = 20401
)
@CustomNoteBlockConfig(
		instrument = Instrument.FLUTE,
		step = 1
)
@DirectionalConfig(
		step_NS = 2,
		step_EW = 3
)
public class OakPaperLantern implements IPaperLantern {
}
