package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICustomTripwire;

import java.util.List;

public interface IIncremental extends ICustomTripwire {

	List<Integer> getModelIdList();

	default int getIndex() {
		return getModelIdList().indexOf(getModelId());
	}
}
