package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Jungle Paper Lantern",
		modelId = 20404
)
@CustomNoteBlockConfig(
		instrument = Instrument.FLUTE,
		step = 10
)
@DirectionalConfig(
		step_NS = 11,
		step_EW = 12
)
public class JunglePaperLantern implements IPaperLantern {
}
