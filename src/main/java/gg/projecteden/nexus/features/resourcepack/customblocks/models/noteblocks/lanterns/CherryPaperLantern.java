package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Cherry Paper Lantern",
	material = CustomMaterial.LANTERNS_PAPER_CHERRY_PAPER_LANTERN
)
@CustomNoteBlockConfig(
	instrument = Instrument.GUITAR,
	step = 1
)
@DirectionalConfig(
	step_NS = 2,
	step_EW = 3
)
public class CherryPaperLantern implements IPaperLantern {
}
