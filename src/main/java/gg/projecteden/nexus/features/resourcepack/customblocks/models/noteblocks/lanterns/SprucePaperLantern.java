package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Spruce Paper Lantern",
		modelId = 20402
)
@CustomNoteBlockConfig(
		instrument = Instrument.FLUTE,
		step = 4
)
@DirectionalConfig(
		step_NS = 5,
		step_EW = 6
)
public class SprucePaperLantern implements IPaperLantern {
}
