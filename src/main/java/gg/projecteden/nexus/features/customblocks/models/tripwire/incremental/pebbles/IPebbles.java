package gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.pebbles;

import gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.IIncremental;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;

import java.util.List;

public interface IPebbles extends IIncremental {

	@Override
	default List<Integer> getModelIdList() {
		return List.of(21106, 21107, 21108);
	}

	@Override
	@NonNull
	default ItemBuilder getItemBuilder() {
		return new ItemBuilder(itemMaterial).customModelData(21106).name("Pebbles");
	}
}
