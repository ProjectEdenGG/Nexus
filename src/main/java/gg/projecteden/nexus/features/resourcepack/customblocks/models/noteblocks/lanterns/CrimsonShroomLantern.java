package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Crimson Shroom Lantern",
		modelId = 20407
)
@CustomNoteBlockConfig(
		instrument = Instrument.FLUTE,
		step = 19
)
@DirectionalConfig(
		step_NS = 20,
		step_EW = 21
)
public class CrimsonShroomLantern implements IShroomLantern {
}
