package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Warped Shroom Lantern",
		modelId = 20408
)
@CustomNoteBlockConfig(
		instrument = Instrument.FLUTE,
		step = 22
)
@DirectionalConfig(
		step_NS = 23,
		step_EW = 24
)
public class WarpedShroomLantern implements IShroomLantern {
}
