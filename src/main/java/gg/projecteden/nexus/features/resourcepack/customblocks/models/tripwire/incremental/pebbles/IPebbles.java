package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.pebbles;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IRequireSupport;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.IIncremental;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;

import java.util.List;

public interface IPebbles extends IIncremental, IRequireSupport {

	@Override
	default List<Integer> getModelIdList() {
		return List.of(21106, 21107, 21108);
	}

	@Override
	@NonNull
	default ItemBuilder getItemBuilder() {
		return new ItemBuilder(CustomMaterial.BLOCKS_ROCKS_PEBBLES).name("Pebbles");
	}

	@Override
	default boolean requiresCorrectToolForDrops() {
		return false;
	}
}
