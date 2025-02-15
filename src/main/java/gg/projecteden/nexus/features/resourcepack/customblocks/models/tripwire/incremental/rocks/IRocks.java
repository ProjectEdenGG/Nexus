package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.rocks;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IRequireSupport;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.IIncremental;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;

import java.util.List;

public interface IRocks extends IIncremental, IRequireSupport {

	@Override
	default List<String> getModelIdList() {
		return List.of(ItemModelType.ROCKS_ROCKS_0.getModel(), ItemModelType.ROCKS_ROCKS_1.getModel(), ItemModelType.ROCKS_ROCKS_2.getModel());
	}

	@Override
	@NonNull
	default ItemBuilder getItemBuilder() {
		return new ItemBuilder(ItemModelType.ROCKS_ROCKS_0).name("Rocks");
	}

	@Override
	default boolean requiresCorrectToolForDrops() {
		return false;
	}
}
