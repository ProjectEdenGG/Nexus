package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.pebbles;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IRequireSupport;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.IIncremental;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;

import java.util.List;

public interface IPebbles extends IIncremental, IRequireSupport {

	@Override
	default List<String> getModelIdList() {
		return List.of(ItemModelType.ROCKS_PEBBLES_0.getModel(), ItemModelType.ROCKS_PEBBLES_1.getModel(), ItemModelType.ROCKS_PEBBLES_2.getModel());
	}

	@Override
	@NonNull
	default ItemBuilder getItemBuilder() {
		return new ItemBuilder(ItemModelType.ROCKS_PEBBLES_0).name("Pebbles");
	}

	@Override
	default boolean requiresCorrectToolForDrops() {
		return false;
	}
}
