package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.crate;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Crate of Berries",
	itemModel = ItemModelType.CRATES_CRATE_BERRY
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_DRUM,
	step = 3,
	customStepSound = "block.honey_block.step"
)
public class SweetBerryCrate implements ICrate {

	@Override
	public @NotNull Material getMaterial() {
		return Material.SWEET_BERRIES;
	}

}
