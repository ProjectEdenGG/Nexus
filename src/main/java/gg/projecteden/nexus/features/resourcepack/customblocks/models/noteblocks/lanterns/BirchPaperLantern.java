package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Birch Paper Lantern",
		modelId = 20403
)
@CustomNoteBlockConfig(
		instrument = Instrument.FLUTE,
		step = 7
)
@DirectionalConfig(
		step_NS = 8,
		step_EW = 9
)
public class BirchPaperLantern implements IPaperLantern {
}
