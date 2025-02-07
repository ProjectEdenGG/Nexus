package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.rocks;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IRequireSupport;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.IIncremental;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;

import java.util.List;

public interface IRocks extends IIncremental, IRequireSupport {

	@Override
	default List<String> getModelIdList() {
		return List.of(21103, 21104, 21105);
	}

	@Override
	@NonNull
	default ItemBuilder getItemBuilder() {
		return new ItemBuilder(CustomMaterial.BLOCKS_ROCKS).name("Rocks");
	}

	@Override
	default boolean requiresCorrectToolForDrops() {
		return false;
	}
}
