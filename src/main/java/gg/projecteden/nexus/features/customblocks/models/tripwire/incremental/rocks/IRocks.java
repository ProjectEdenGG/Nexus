package gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.rocks;

import gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.IIncremental;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;

import java.util.List;

public interface IRocks extends IIncremental {

	@Override
	default List<Integer> getModelIdList() {
		return List.of(21103, 21104, 21105);
	}

	@Override
	@NonNull
	default ItemBuilder getItemBuilder() {
		return new ItemBuilder(itemMaterial).customModelData(21103).name("Rocks");
	}
}
