package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.pillar;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;

@CustomBlockConfig(
	name = "Granite Pillar",
	itemModel = ItemModelType.STONES_GRANITE_GRANITE_PILLAR
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 11,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class GraniteStonePillar implements IStonePillar {
	@Override
	public @NonNull Material getMaterial() {
		return Material.POLISHED_GRANITE;
	}
}
