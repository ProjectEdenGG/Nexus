package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Dark Oak Paper Lantern",
		modelId = 20406
)
@CustomNoteBlockConfig(
		instrument = Instrument.FLUTE,
		step = 16
)
@DirectionalConfig(
		step_NS = 17,
		step_EW = 18
)
public class DarkOakPaperLantern implements IPaperLantern {
}
