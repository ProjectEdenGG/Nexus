package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Acacia Paper Lantern",
		modelId = 20405
)
@CustomNoteBlockConfig(
		instrument = Instrument.FLUTE,
		step = 13
)
@DirectionalConfig(
		step_NS = 14,
		step_EW = 15
)
public class AcaciaPaperLantern implements IPaperLantern {
}
