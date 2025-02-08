package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Bamboo Paper Lantern",
	material = CustomMaterial.LANTERNS_PAPER_BAMBOO_PAPER_LANTERN
)
@CustomNoteBlockConfig(
	instrument = Instrument.GUITAR,
	step = 4
)
@DirectionalConfig(
	step_NS = 5,
	step_EW = 6
)
public class BambooPaperLantern implements IPaperLantern {
}
